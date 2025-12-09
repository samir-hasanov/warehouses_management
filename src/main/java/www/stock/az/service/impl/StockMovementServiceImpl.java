package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.StockInRequest;
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
import java.util.ArrayList;
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
            ? request.getProductBarcode().replaceAll("\\s+", "").trim() 
            : "";
        if (cleanedBarcode.isEmpty()) {
            throw new RuntimeException("Məhsul barcode boşdur");
        }
        Product product = barcodeRepository.findProductByBarcode(cleanedBarcode)
                .orElseThrow(() -> new RuntimeException("Barcode ilə məhsul tapılmadı: " + cleanedBarcode));
        
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
        
        if (movement.getItems() != null) {
            response.setItems(movement.getItems().stream()
                    .map(this::mapItemToResponse)
                    .collect(Collectors.toList()));
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
        StockMovementItemResponse response = new StockMovementItemResponse();
        response.setId(item.getId());
        ProductResponse productResponse = productService.mapToResponse(item.getProduct());
        response.setProduct(productResponse);
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
