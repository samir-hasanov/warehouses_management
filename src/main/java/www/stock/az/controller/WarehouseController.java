package www.stock.az.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.service.WarehouseService;
import www.stock.az.service.impl.WarehouseServiceImpl;

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
}
