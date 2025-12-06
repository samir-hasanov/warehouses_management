package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.StockInRequest;
import www.stock.az.dto.response.StockMovementResponse;
import www.stock.az.service.StockMovementService;
import www.stock.az.service.impl.StockMovementServiceImpl;

@RestController
@RequestMapping("/api/1.1/stock-movements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockMovementController {
    
    private final StockMovementService stockMovementService;
    
    /**
     * Barcode scanner ilə stok əlavə etmə endpoint-i
     * İlk öncə anbar barcode-u scan edilir, sonra məhsul barcode-u və miqdar
     */
    @PostMapping("/stock-in-by-barcode")
    public ResponseEntity<StockMovementResponse> addStockByBarcode(
            @Valid @RequestBody StockInRequest request) {
        try {
            StockMovementResponse response = stockMovementService.addStockByBarcode(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponse> getStockMovementById(@PathVariable Long id) {
        StockMovementResponse movement = stockMovementService.findById(id);
        return ResponseEntity.ok(movement);
    }
    
    @GetMapping("/number/{number}")
    public ResponseEntity<StockMovementResponse> getStockMovementByNumber(@PathVariable String number) {
        StockMovementResponse movement = stockMovementService.findByNumber(number);
        return ResponseEntity.ok(movement);
    }
}
