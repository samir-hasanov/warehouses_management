package www.stock.az.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import www.stock.az.dto.request.InvoiceCreateRequest;
import www.stock.az.dto.response.InvoiceResponse;
import www.stock.az.enums.InvoiceDirection;
import www.stock.az.service.InvoiceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/1.1/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceCreateRequest request) {
        InvoiceResponse response = invoiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> findByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.findByNumber(invoiceNumber));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> search(
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        InvoiceDirection dir = null;
        if (direction != null && !direction.isBlank()) {
            try {
                dir = InvoiceDirection.valueOf(direction.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        List<InvoiceResponse> responses = invoiceService.search(dir, fromDate, toDate);
        return ResponseEntity.ok(responses);
    }
}

