package www.stock.az.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.BrandCreateRequest;
import www.stock.az.dto.request.BrandUpdateRequest;
import www.stock.az.dto.response.BrandResponse;
import www.stock.az.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Brands", description = "Brand management API endpoints")
public class BrandController {
    
    private final BrandService brandService;
    
    @GetMapping
    @Operation(summary = "Get all active brands", description = "Returns a list of all active brands")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of brands")
    public ResponseEntity<List<BrandResponse>> getAllActiveBrands() {
        List<BrandResponse> brands = brandService.findAllActive();
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID", description = "Returns a brand by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found"),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    public ResponseEntity<BrandResponse> getBrandById(
            @Parameter(description = "Brand ID", required = true) @PathVariable Long id) {
        BrandResponse brand = brandService.findById(id);
        return ResponseEntity.ok(brand);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get brand by code", description = "Returns a brand by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found"),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    public ResponseEntity<BrandResponse> getBrandByCode(
            @Parameter(description = "Brand code", required = true) @PathVariable String code) {
        BrandResponse brand = brandService.findByCode(code);
        return ResponseEntity.ok(brand);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search brands", description = "Search brands by query string")
    @ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<List<BrandResponse>> searchBrands(
            @Parameter(description = "Search query", required = true) @RequestParam String q) {
        List<BrandResponse> brands = brandService.search(q);
        return ResponseEntity.ok(brands);
    }

    @PostMapping
    @Operation(summary = "Create a new brand", description = "Creates a new brand with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Brand created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        try {
            BrandResponse response = brandService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update brand", description = "Updates an existing brand by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BrandResponse> updateBrand(
            @Parameter(description = "Brand ID", required = true) @PathVariable Long id,
            @Valid @RequestBody BrandUpdateRequest request) {
        try {
            BrandResponse response = brandService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete brand", description = "Soft deletes a brand by ID (sets isActive to false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "Brand ID", required = true) @PathVariable Long id) {
        try {
            brandService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
