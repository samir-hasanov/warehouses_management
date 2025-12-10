package www.stock.az.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.response.StockResponse;
import www.stock.az.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {
    
    private final StockService stockService;
    
    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stockService.findAll();
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long id) {
        StockResponse stock = stockService.findById(id);
        return ResponseEntity.ok(stock);
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockResponse>> getStocksByWarehouse(@PathVariable Long warehouseId) {
        List<StockResponse> stocks = stockService.findByWarehouseId(warehouseId);
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockResponse>> getStocksByProduct(@PathVariable Long productId) {
        List<StockResponse> stocks = stockService.findByProductId(productId);
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockResponse>> getLowStockItems(
            @RequestParam(required = false) Long warehouseId) {
        List<StockResponse> stocks = warehouseId != null 
            ? stockService.findLowStockItems(warehouseId)
            : stockService.findAll().stream()
                .filter(s -> s.getMinStockLevel() != null && 
                            s.getQuantity().compareTo(s.getMinStockLevel()) < 0)
                .toList();
        return ResponseEntity.ok(stocks);
    }
}

