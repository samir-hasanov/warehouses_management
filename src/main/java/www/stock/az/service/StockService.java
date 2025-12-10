package www.stock.az.service;

import www.stock.az.dto.response.StockResponse;

import java.util.List;

public interface StockService {
    List<StockResponse> findAll();
    
    StockResponse findById(Long id);
    
    List<StockResponse> findByWarehouseId(Long warehouseId);
    
    List<StockResponse> findByProductId(Long productId);
    
    List<StockResponse> findLowStockItems(Long warehouseId);
}

