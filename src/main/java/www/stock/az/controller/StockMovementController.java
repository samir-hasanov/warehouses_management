package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.StockInRequest;
import www.stock.az.dto.request.StockMovementCreateRequest;
import www.stock.az.dto.response.StockMovementResponse;
import www.stock.az.enums.MovementType;
import www.stock.az.service.StockMovementService;
import www.stock.az.service.impl.StockMovementServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PostMapping
    public ResponseEntity<?> createStockMovement(
            @Valid @RequestBody StockMovementCreateRequest request) {
        try {
            StockMovementResponse response = stockMovementService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Stok hərəkəti yaradılarkən xəta baş verdi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
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
    
    @GetMapping
    public ResponseEntity<?> getAllStockMovements(
            @RequestParam(required = false) String type) {
        try {
            MovementType movementType = null;
            if (type != null && !type.isEmpty()) {
                try {
                    movementType = MovementType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid type, ignore and return all
                }
            }
            List<StockMovementResponse> movements = stockMovementService.findAll(movementType);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Stok hərəkətləri yüklənərkən xəta baş verdi");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveStockMovement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String approvedBy = request != null && request.containsKey("approvedBy") 
                    ? request.get("approvedBy") 
                    : "SYSTEM";
            StockMovementResponse response = stockMovementService.approve(id, approvedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Hərəkət təsdiqlənərkən xəta baş verdi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelStockMovement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String reason = request != null && request.containsKey("reason") 
                    ? request.get("reason") 
                    : "İstifadəçi tərəfindən ləğv edildi";
            StockMovementResponse response = stockMovementService.cancel(id, reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Hərəkət ləğv edilərkən xəta baş verdi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
