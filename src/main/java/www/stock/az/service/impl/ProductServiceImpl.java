package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.ProductCreateRequest;
import www.stock.az.dto.response.BrandResponse;
import www.stock.az.dto.response.CategoryResponse;
import www.stock.az.dto.response.ProductResponse;
import www.stock.az.entity.*;
import www.stock.az.repository.*;
import www.stock.az.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BarcodeRepository barcodeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;

    public ProductResponse findByBarcode(String barcode) {
        // Barcode-u təmizlə: boşluqları sil və trim et
        String cleanedBarcode = barcode != null ? barcode.replaceAll("\\s+", "").trim() : "";
        if (cleanedBarcode.isEmpty()) {
            throw new RuntimeException("Barcode boşdur");
        }
        Product product = barcodeRepository.findProductByBarcode(cleanedBarcode)
                .orElseThrow(() -> new RuntimeException("Barcode ilə məhsul tapılmadı: " + cleanedBarcode));
        return mapToResponse(product);
    }

    public ProductResponse findByCode(String code) {
        Product product = productRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı: " + code));
        return mapToResponse(product);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı: " + id));
        return mapToResponse(product);
    }

    public List<ProductResponse> findAllActive() {
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create new product with category, brand, barcodes and optional initial stock
     */
    public ProductResponse create(ProductCreateRequest request) {
        // Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: " + request.getCategoryId()));

        // Brand
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brend tapılmadı: " + request.getBrandId()));

        // Product
        Product product = new Product();
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setUnit(request.getUnit());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE);

        Product savedProduct = productRepository.save(product);

        // Main barcode - təmizlə və saxla
        Barcode mainBarcode = new Barcode();
        String cleanedMainBarcode = request.getMainBarcode() != null 
            ? request.getMainBarcode().replaceAll("\\s+", "").trim() 
            : "";
        if (cleanedMainBarcode.isEmpty()) {
            throw new RuntimeException("Əsas barcode boşdur");
        }
        mainBarcode.setBarcode(cleanedMainBarcode);
        mainBarcode.setBarcodeType(null);
        mainBarcode.setIsPrimary(true);
        mainBarcode.setProduct(savedProduct);
        barcodeRepository.save(mainBarcode);

        // Additional barcodes
        if (request.getAdditionalBarcodes() != null) {
            for (String code : request.getAdditionalBarcodes()) {
                if (code == null || code.isBlank()) continue;
                Barcode b = new Barcode();
                b.setBarcode(code.trim());
                b.setBarcodeType(null);
                b.setIsPrimary(false);
                b.setProduct(savedProduct);
                barcodeRepository.save(b);
            }
        }

        // Optional initial stock
        if (request.getInitialWarehouseId() != null &&
                request.getInitialQuantity() != null &&
                request.getInitialQuantity().compareTo(BigDecimal.ZERO) > 0) {

            Warehouse warehouse = warehouseRepository.findById(request.getInitialWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + request.getInitialWarehouseId()));

            Stock stock = stockRepository.findByProductIdAndWarehouseId(savedProduct.getId(), warehouse.getId())
                    .orElse(null);

            if (stock == null) {
                stock = new Stock();
                stock.setProduct(savedProduct);
                stock.setWarehouse(warehouse);
                stock.setQuantity(request.getInitialQuantity());
                stock.setReservedQuantity(BigDecimal.ZERO);
                stock.setAvailableQuantity(request.getInitialQuantity());
            } else {
                stock.setQuantity(stock.getQuantity().add(request.getInitialQuantity()));
                stock.setAvailableQuantity(stock.getAvailableQuantity().add(request.getInitialQuantity()));
            }

            // Default min stock level
            if (request.getDefaultMinStockLevel() != null) {
                stock.setMinStockLevel(request.getDefaultMinStockLevel());
            }

            stockRepository.save(stock);
        }

        return mapToResponse(savedProduct);
    }

    public ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setCode(product.getCode());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setUnit(product.getUnit());
        response.setWeight(product.getWeight());
        response.setDimensions(product.getDimensions());
        response.setIsActive(product.getIsActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        // Category mapping
        if (product.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(product.getCategory().getId());
            categoryResponse.setCode(product.getCategory().getCode());
            categoryResponse.setName(product.getCategory().getName());
            response.setCategory(categoryResponse);
        }

        // Brand mapping
        if (product.getBrand() != null) {
            BrandResponse brandResponse = new BrandResponse();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setCode(product.getBrand().getCode());
            brandResponse.setName(product.getBrand().getName());
            response.setBrand(brandResponse);
        }

        return response;
    }
}
