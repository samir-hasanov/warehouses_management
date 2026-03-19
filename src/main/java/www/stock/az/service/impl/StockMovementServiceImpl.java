package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.StockInRequest;
import www.stock.az.dto.request.StockMovementCreateRequest;
import www.stock.az.dto.response.ProductResponse;
import www.stock.az.dto.response.StockMovementItemResponse;
import www.stock.az.dto.response.StockMovementResponse;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.entity.*;
import www.stock.az.enums.MovementStatus;
import www.stock.az.enums.MovementType;
import www.stock.az.repository.*;
import www.stock.az.service.StockMovementService;
import www.stock.az.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementServiceImpl implements StockMovementService {
    
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final BarcodeRepository barcodeRepository;
    private final StockRepository stockRepository;
    private final ProductServiceImpl productService;
    
    /**
     * Barcode scanner ilə stok əlavə etmə (STOCK_IN)
     */
    public StockMovementResponse addStockByBarcode(StockInRequest request) {
        // Anbarı tap
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + request.getWarehouseId()));
        
        // Barcode ilə məhsulu tap - barcode-u təmizlə
        String cleanedBarcode = request.getProductBarcode() != null 
            ? request.getProductBarcode().replaceAll("[\\s\\-_\\.]", "").trim() 
            : "";
        
        if (cleanedBarcode.isEmpty()) {
            throw new RuntimeException("Barcode boşdur");
        }
        
        // Əvvəlcə təmizlənmiş barcode ilə axtar
        Optional<Product> productOpt = barcodeRepository.findProductByBarcode(cleanedBarcode);
        
        // Əgər tapılmadısa, təmizlənmiş barcode ilə bazada təmizləyərək axtar
        if (productOpt.isEmpty()) {
            productOpt = barcodeRepository.findProductByCleanedBarcode(cleanedBarcode);
        }
        
        // Əgər hələ də tapılmadısa, orijinal barcode ilə də yoxla
        if (productOpt.isEmpty() && request.getProductBarcode() != null && !cleanedBarcode.equals(request.getProductBarcode().trim())) {
            productOpt = barcodeRepository.findProductByBarcode(request.getProductBarcode().trim());
        }
        // USB skanner / Symbol: barcode cədvəlində yoxdursa, məhsul kodu (Product.code) ilə də yoxla
        if (productOpt.isEmpty()) {
            productOpt = productRepository.findByCodeAndIsActiveTrue(cleanedBarcode);
        }
        
        Product product = productOpt
                .orElseThrow(() -> new RuntimeException("Barcode/kod ilə məhsul tapılmadı: " + request.getProductBarcode()));
        
        // Stock Movement yarat
        StockMovement movement = new StockMovement();
        movement.setMovementNumber(generateMovementNumber());
        movement.setMovementType(MovementType.STOCK_IN);
        movement.setStatus(MovementStatus.COMPLETED); // Barcode scanner ilə dərhal tamamlanır
        movement.setTargetWarehouse(warehouse);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setNotes(request.getNotes());
        movement.setApprovedBy("SYSTEM"); // Barcode scanner əməliyyatı
        movement.setApprovedAt(LocalDateTime.now());
        
        // Stock Movement Item yarat
        StockMovementItem item = new StockMovementItem();
        item.setStockMovement(movement);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setUnitCost(request.getUnitCost());
        item.setTotalCost(request.getUnitCost() != null 
                ? request.getUnitCost().multiply(request.getQuantity())
                : null);
        item.setBatchNumber(request.getBatchNumber());
        item.setNotes(request.getNotes());
        
        movement.setItems(new ArrayList<>());
        movement.getItems().add(item);
        
        // Stock-u yenilə və ya yarat
        updateOrCreateStock(warehouse, product, request.getQuantity());
        
        // Stock Movement-i saxla
        StockMovement savedMovement = stockMovementRepository.save(movement);
        
        return mapToResponse(savedMovement);
    }
    
    /**
     * Stock Movement yarat (STOCK_IN, STOCK_OUT, TRANSFER)
     */
    public StockMovementResponse create(StockMovementCreateRequest request) {
        // Validation: warehouse requirements based on movement type
        if (request.getMovementType() == MovementType.STOCK_IN) {
            if (request.getTargetWarehouseId() == null) {
                throw new RuntimeException("STOCK_IN üçün hədəf anbar mütləqdir");
            }
        } else if (request.getMovementType() == MovementType.STOCK_OUT) {
            if (request.getSourceWarehouseId() == null) {
                throw new RuntimeException("STOCK_OUT üçün mənbə anbar mütləqdir");
            }
        } else if (request.getMovementType() == MovementType.TRANSFER) {
            if (request.getSourceWarehouseId() == null || request.getTargetWarehouseId() == null) {
                throw new RuntimeException("TRANSFER üçün həm mənbə, həm də hədəf anbar mütləqdir");
            }
            if (request.getSourceWarehouseId().equals(request.getTargetWarehouseId())) {
                throw new RuntimeException("Mənbə və hədəf anbar eyni ola bilməz");
            }
        }
        
        // Load warehouses if needed
        Warehouse sourceWarehouse = null;
        if (request.getSourceWarehouseId() != null) {
            sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Mənbə anbar tapılmadı: " + request.getSourceWarehouseId()));
        }
        
        Warehouse targetWarehouse = null;
        if (request.getTargetWarehouseId() != null) {
            targetWarehouse = warehouseRepository.findById(request.getTargetWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Hədəf anbar tapılmadı: " + request.getTargetWarehouseId()));
        }
        
        // Create Stock Movement
        StockMovement movement = new StockMovement();
        movement.setMovementNumber(generateMovementNumber());
        movement.setMovementType(request.getMovementType());
        movement.setStatus(MovementStatus.PENDING); // Default status, needs approval
        movement.setSourceWarehouse(sourceWarehouse);
        movement.setTargetWarehouse(targetWarehouse);
        movement.setMovementDate(request.getMovementDate() != null ? request.getMovementDate() : LocalDateTime.now());
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setNotes(request.getNotes());
        
        // Validate stock availability for STOCK_OUT and TRANSFER
        if (request.getMovementType() == MovementType.STOCK_OUT || request.getMovementType() == MovementType.TRANSFER) {
            if (sourceWarehouse != null) {
                for (StockMovementCreateRequest.StockMovementItemRequest itemRequest : request.getItems()) {
                    validateStockAvailability(sourceWarehouse, itemRequest.getProductId(), itemRequest.getQuantity());
                }
            }
        }
        
        // Create Stock Movement Items
        List<StockMovementItem> items = new ArrayList<>();
        for (StockMovementCreateRequest.StockMovementItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı: " + itemRequest.getProductId()));
            
            StockMovementItem item = new StockMovementItem();
            item.setStockMovement(movement);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        movement.setItems(items);
        
        // Save Stock Movement
        StockMovement savedMovement = stockMovementRepository.save(movement);
        
        // Reserve stock for STOCK_OUT and TRANSFER movements
        if (request.getMovementType() == MovementType.STOCK_OUT || request.getMovementType() == MovementType.TRANSFER) {
            if (sourceWarehouse != null) {
                for (StockMovementCreateRequest.StockMovementItemRequest itemRequest : request.getItems()) {
                    reserveStockForMovement(sourceWarehouse, itemRequest.getProductId(), itemRequest.getQuantity());
                }
            }
        }
        
        // STOCK_IN üçün stock-u dərhal əlavə et (təsdiqləməyə ehtiyac yoxdur)
        if (request.getMovementType() == MovementType.STOCK_IN) {
            if (targetWarehouse != null) {
                for (StockMovementItem item : savedMovement.getItems()) {
                    updateOrCreateStock(targetWarehouse, item.getProduct(), item.getQuantity());
                }
                // STOCK_IN dərhal tamamlanır
                savedMovement.setStatus(MovementStatus.COMPLETED);
                savedMovement.setApprovedBy("SYSTEM");
                savedMovement.setApprovedAt(LocalDateTime.now());
                savedMovement = stockMovementRepository.save(savedMovement);
            }
        }
        
        return mapToResponse(savedMovement);
    }
    
    /**
     * Stock mövcudluğunu yoxla (validation)
     */
    private void validateStockAvailability(Warehouse warehouse, Long productId, BigDecimal quantity) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouse.getId())
                .orElseThrow(() -> new RuntimeException("Məhsul stoku tapılmadı: Product ID " + productId + ", Warehouse ID " + warehouse.getId()));
        
        // Available quantity yoxla
        if (stock.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Kifayət qədər stok yoxdur. Mövcud: " + stock.getAvailableQuantity() + ", Tələb olunan: " + quantity);
        }
    }
    
    /**
     * Stock-u reserve et (hərəkət üçün)
     */
    private void reserveStockForMovement(Warehouse warehouse, Long productId, BigDecimal quantity) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouse.getId())
                .orElseThrow(() -> new RuntimeException("Məhsul stoku tapılmadı: Product ID " + productId + ", Warehouse ID " + warehouse.getId()));
        
        // Available quantity yoxla (yenidən, çünki eyni zamanda başqa hərəkət ola bilər)
        if (stock.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Kifayət qədər stok yoxdur. Mövcud: " + stock.getAvailableQuantity() + ", Tələb olunan: " + quantity);
        }
        
        // Reserve et
        stock.setReservedQuantity(stock.getReservedQuantity().add(quantity));
        stock.setAvailableQuantity(stock.getAvailableQuantity().subtract(quantity));
        stockRepository.save(stock);
    }
    
    /**
     * Stock-u yenilə və ya yarat
     */
    private void updateOrCreateStock(Warehouse warehouse, Product product, BigDecimal quantity) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElse(null);
        
        if (stock == null) {
            // Yeni stock yarat
            stock = new Stock();
            stock.setProduct(product);
            stock.setWarehouse(warehouse);
            stock.setQuantity(quantity);
            stock.setReservedQuantity(BigDecimal.ZERO);
            stock.setAvailableQuantity(quantity);
        } else {
            // Mövcud stock-u yenilə
            stock.setQuantity(stock.getQuantity().add(quantity));
            stock.setAvailableQuantity(stock.getAvailableQuantity().add(quantity));
        }
        
        stockRepository.save(stock);
    }
    
    /**
     * Movement nömrəsi yarat
     */
    private String generateMovementNumber() {
        String prefix = "MOV-";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        String uniqueId = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + "-" + uniqueId;
    }
    
    /**
     * Bütün Stock Movement-ləri tap (filter ilə)
     */
    public List<StockMovementResponse> findAll(MovementType type) {
        try {
            List<StockMovement> movements;
            if (type != null) {
                movements = stockMovementRepository.findByMovementTypeWithDetails(type);
            } else {
                movements = stockMovementRepository.findAllWithDetails();
            }
            // Tarixə görə sırala (ən yeni üstə)
            movements.sort((a, b) -> {
                LocalDateTime dateA = a.getMovementDate() != null ? a.getMovementDate() : a.getCreatedAt();
                LocalDateTime dateB = b.getMovementDate() != null ? b.getMovementDate() : b.getCreatedAt();
                if (dateA == null && dateB == null) return 0;
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });
            return movements.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Stok hərəkətləri yüklənərkən xəta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Stock Movement-i ID ilə tap
     */
    public StockMovementResponse findById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok hərəkəti tapılmadı: " + id));
        return mapToResponse(movement);
    }
    
    /**
     * Stock Movement-i nömrə ilə tap
     */
    public StockMovementResponse findByNumber(String number) {
        StockMovement movement = stockMovementRepository.findByMovementNumber(number)
                .orElseThrow(() -> new RuntimeException("Stok hərəkəti tapılmadı: " + number));
        return mapToResponse(movement);
    }
    
    /**
     * Stock Movement-i təsdiqlə və stock-u update et
     */
    public StockMovementResponse approve(Long id, String approvedBy) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok hərəkəti tapılmadı: " + id));
        
        if (movement.getStatus() != MovementStatus.PENDING) {
            throw new RuntimeException("Yalnız gözləyən hərəkətlər təsdiqlənə bilər");
        }
        
        movement.setStatus(MovementStatus.APPROVED);
        movement.setApprovedBy(approvedBy);
        movement.setApprovedAt(LocalDateTime.now());
        
        // Stock-u update et
        executeStockMovement(movement);
        
        movement.setStatus(MovementStatus.COMPLETED);
        StockMovement savedMovement = stockMovementRepository.save(movement);
        
        return mapToResponse(savedMovement);
    }
    
    /**
     * Stock Movement-i ləğv et və reserve-i geri qaytar
     */
    public StockMovementResponse cancel(Long id, String reason) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok hərəkəti tapılmadı: " + id));
        
        if (movement.getStatus() == MovementStatus.COMPLETED) {
            throw new RuntimeException("Tamamlanmış hərəkət ləğv edilə bilməz");
        }
        
        // Reserve-i geri qaytar (STOCK_OUT və TRANSFER üçün)
        if ((movement.getMovementType() == MovementType.STOCK_OUT || movement.getMovementType() == MovementType.TRANSFER)
                && movement.getSourceWarehouse() != null) {
            for (StockMovementItem item : movement.getItems()) {
                releaseReservedStock(movement.getSourceWarehouse(), item.getProduct().getId(), item.getQuantity());
            }
        }
        
        movement.setStatus(MovementStatus.CANCELLED);
        movement.setNotes(movement.getNotes() != null ? movement.getNotes() + "\nLəğv səbəbi: " + reason : "Ləğv səbəbi: " + reason);
        StockMovement savedMovement = stockMovementRepository.save(movement);
        
        return mapToResponse(savedMovement);
    }
    
    /**
     * Stock hərəkətini icra et (actual quantity dəyişdir)
     */
    private void executeStockMovement(StockMovement movement) {
        if (movement.getMovementType() == MovementType.STOCK_IN) {
            // STOCK_IN: hədəf anbara əlavə et
            if (movement.getTargetWarehouse() != null) {
                for (StockMovementItem item : movement.getItems()) {
                    updateOrCreateStock(movement.getTargetWarehouse(), item.getProduct(), item.getQuantity());
                }
            }
        } else if (movement.getMovementType() == MovementType.STOCK_OUT) {
            // STOCK_OUT: mənbə anbardan çıxar
            if (movement.getSourceWarehouse() != null) {
                for (StockMovementItem item : movement.getItems()) {
                    decreaseStock(movement.getSourceWarehouse(), item.getProduct().getId(), item.getQuantity());
                }
            }
        } else if (movement.getMovementType() == MovementType.TRANSFER) {
            // TRANSFER: mənbədən çıxar, hədəfə əlavə et
            if (movement.getSourceWarehouse() != null && movement.getTargetWarehouse() != null) {
                for (StockMovementItem item : movement.getItems()) {
                    decreaseStock(movement.getSourceWarehouse(), item.getProduct().getId(), item.getQuantity());
                    updateOrCreateStock(movement.getTargetWarehouse(), item.getProduct(), item.getQuantity());
                }
            }
        }
    }
    
    /**
     * Stock-u azalt (reserved quantity-dən çıxar, actual quantity azalt)
     */
    private void decreaseStock(Warehouse warehouse, Long productId, BigDecimal quantity) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouse.getId())
                .orElseThrow(() -> new RuntimeException("Məhsul stoku tapılmadı"));
        
        // Reserved quantity-dən çıxar
        stock.setReservedQuantity(stock.getReservedQuantity().subtract(quantity));
        // Actual quantity azalt
        stock.setQuantity(stock.getQuantity().subtract(quantity));
        // Available quantity yenilə
        stock.setAvailableQuantity(stock.getQuantity().subtract(stock.getReservedQuantity()));
        
        stockRepository.save(stock);
    }
    
    /**
     * Reserve edilmiş stock-u geri qaytar
     */
    private void releaseReservedStock(Warehouse warehouse, Long productId, BigDecimal quantity) {
        Stock stock = stockRepository.findByProductIdAndWarehouseId(productId, warehouse.getId())
                .orElse(null);
        
        if (stock != null) {
            stock.setReservedQuantity(stock.getReservedQuantity().subtract(quantity));
            stock.setAvailableQuantity(stock.getAvailableQuantity().add(quantity));
            stockRepository.save(stock);
        }
    }
    
    /**
     * Entity-ni Response-a çevir
     */
    private StockMovementResponse mapToResponse(StockMovement movement) {
        StockMovementResponse response = new StockMovementResponse();
        response.setId(movement.getId());
        response.setMovementNumber(movement.getMovementNumber());
        response.setMovementType(movement.getMovementType());
        response.setStatus(movement.getStatus());
        response.setMovementDate(movement.getMovementDate());
        response.setReferenceNumber(movement.getReferenceNumber());
        response.setNotes(movement.getNotes());
        response.setApprovedBy(movement.getApprovedBy());
        response.setApprovedAt(movement.getApprovedAt());
        response.setCreatedAt(movement.getCreatedAt());
        response.setUpdatedAt(movement.getUpdatedAt());
        
        if (movement.getSourceWarehouse() != null) {
            response.setSourceWarehouse(mapWarehouseToResponse(movement.getSourceWarehouse()));
        }
        
        if (movement.getTargetWarehouse() != null) {
            response.setTargetWarehouse(mapWarehouseToResponse(movement.getTargetWarehouse()));
        }
        
        if (movement.getItems() != null && !movement.getItems().isEmpty()) {
            try {
                response.setItems(movement.getItems().stream()
                        .map(this::mapItemToResponse)
                        .filter(item -> item != null)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                // Items mapping error, set empty list
                response.setItems(new ArrayList<>());
            }
        } else {
            response.setItems(new ArrayList<>());
        }
        
        return response;
    }
    
    private WarehouseResponse mapWarehouseToResponse(Warehouse warehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.setId(warehouse.getId());
        response.setCode(warehouse.getCode());
        response.setName(warehouse.getName());
        return response;
    }
    
    private StockMovementItemResponse mapItemToResponse(StockMovementItem item) {
        if (item == null) {
            return null;
        }
        StockMovementItemResponse response = new StockMovementItemResponse();
        response.setId(item.getId());
        if (item.getProduct() != null) {
            try {
                ProductResponse productResponse = productService.mapToResponse(item.getProduct());
                response.setProduct(productResponse);
            } catch (Exception e) {
                // Product mapping error, skip
            }
        }
        response.setQuantity(item.getQuantity());
        response.setUnitCost(item.getUnitCost());
        response.setTotalCost(item.getTotalCost());
        response.setBatchNumber(item.getBatchNumber());
        response.setExpiryDate(item.getExpiryDate());
        response.setSerialNumber(item.getSerialNumber());
        response.setNotes(item.getNotes());
        return response;
    }
}
