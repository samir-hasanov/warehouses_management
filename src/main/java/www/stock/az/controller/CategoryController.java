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
import www.stock.az.dto.request.CategoryCreateRequest;
import www.stock.az.dto.request.CategoryUpdateRequest;
import www.stock.az.dto.response.CategoryResponse;
import www.stock.az.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "Category management API endpoints")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    @Operation(summary = "Get all active categories", description = "Returns a list of all active categories")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories")
    public ResponseEntity<List<CategoryResponse>> getAllActiveCategories() {
        List<CategoryResponse> categories = categoryService.findAllActive();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        CategoryResponse category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get category by code", description = "Returns a category by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponse> getCategoryByCode(
            @Parameter(description = "Category code", required = true) @PathVariable String code) {
        CategoryResponse category = categoryService.findByCode(code);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by query string")
    @ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<List<CategoryResponse>> searchCategories(
            @Parameter(description = "Search query", required = true) @RequestParam String q) {
        List<CategoryResponse> categories = categoryService.search(q);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        try {
            CategoryResponse response = categoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an existing category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        try {
            CategoryResponse response = categoryService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Soft deletes a category by ID (sets isActive to false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
