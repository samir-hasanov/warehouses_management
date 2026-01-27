package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.ProductCreateRequest;
import www.stock.az.dto.response.PageResponse;
import www.stock.az.dto.response.ProductResponse;
import www.stock.az.service.ProductService;
import www.stock.az.service.impl.ProductServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/1.1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        PageRequest pageable = PageRequest.of(page, size);

        PageResponse<ProductResponse> products = productService.findAllActive(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ProductResponse> getProductByCode(@PathVariable String code) {
        ProductResponse product = productService.findByCode(code);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponse> getProductByBarcode(@PathVariable String barcode) {
        ProductResponse product = productService.findByBarcode(barcode);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
