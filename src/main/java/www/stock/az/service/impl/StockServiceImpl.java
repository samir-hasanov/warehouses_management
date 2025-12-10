package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.response.ProductResponse;
import www.stock.az.dto.response.StockResponse;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.entity.Stock;
import www.stock.az.repository.StockRepository;
import www.stock.az.service.StockService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<StockResponse> findAll() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StockResponse findById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stok tapılmadı: " + id));
        return mapToResponse(stock);
    }

    @Override
    public List<StockResponse> findByWarehouseId(Long warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> findByProductId(Long productId) {
        return stockRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> findLowStockItems(Long warehouseId) {
        return stockRepository.findLowStockItems(warehouseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StockResponse mapToResponse(Stock stock) {
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setQuantity(stock.getQuantity());
        response.setReservedQuantity(stock.getReservedQuantity());
        response.setAvailableQuantity(stock.getAvailableQuantity());
        response.setMinStockLevel(stock.getMinStockLevel());
        response.setMaxStockLevel(stock.getMaxStockLevel());
        response.setLocation(stock.getLocation());
        response.setCreatedAt(stock.getCreatedAt());
        response.setUpdatedAt(stock.getUpdatedAt());

        // Product mapping
        if (stock.getProduct() != null) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(stock.getProduct().getId());
            productResponse.setCode(stock.getProduct().getCode());
            productResponse.setName(stock.getProduct().getName());
            response.setProduct(productResponse);
        }

        // Warehouse mapping
        if (stock.getWarehouse() != null) {
            WarehouseResponse warehouseResponse = new WarehouseResponse();
            warehouseResponse.setId(stock.getWarehouse().getId());
            warehouseResponse.setCode(stock.getWarehouse().getCode());
            warehouseResponse.setName(stock.getWarehouse().getName());
            response.setWarehouse(warehouseResponse);
        }

        return response;
    }
}

