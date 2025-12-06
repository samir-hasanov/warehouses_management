package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.BrandCreateRequest;
import www.stock.az.dto.request.BrandUpdateRequest;
import www.stock.az.dto.response.BrandResponse;
import www.stock.az.service.BrandService;
import www.stock.az.service.impl.BrandServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrandController {
    
    private final BrandService brandService;
    
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllActiveBrands() {
        List<BrandResponse> brands = brandService.findAllActive();
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        BrandResponse brand = brandService.findById(id);
        return ResponseEntity.ok(brand);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<BrandResponse> getBrandByCode(@PathVariable String code) {
        BrandResponse brand = brandService.findByCode(code);
        return ResponseEntity.ok(brand);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BrandResponse>> searchBrands(
            @RequestParam String q) {
        List<BrandResponse> brands = brandService.search(q);
        return ResponseEntity.ok(brands);
    }

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        try {
            BrandResponse response = brandService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandUpdateRequest request) {
        try {
            BrandResponse response = brandService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        try {
            brandService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
