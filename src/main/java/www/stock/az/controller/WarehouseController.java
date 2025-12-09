package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.WarehouseCreateRequest;
import www.stock.az.dto.request.WarehouseUpdateRequest;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllActiveWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.findAllActive();
        return ResponseEntity.ok(warehouses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse warehouse = warehouseService.findById(id);
        return ResponseEntity.ok(warehouse);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<WarehouseResponse> getWarehouseByCode(@PathVariable String code) {
        WarehouseResponse warehouse = warehouseService.findByCode(code);
        return ResponseEntity.ok(warehouse);
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseResponse response = warehouseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateRequest request) {
        WarehouseResponse response = warehouseService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
