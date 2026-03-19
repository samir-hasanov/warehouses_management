package www.stock.az.service;

import www.stock.az.dto.request.InvoiceCreateRequest;
import www.stock.az.dto.response.InvoiceResponse;
import www.stock.az.enums.InvoiceDirection;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse create(InvoiceCreateRequest request);

    InvoiceResponse findById(Long id);

    InvoiceResponse findByNumber(String invoiceNumber);

    List<InvoiceResponse> search(InvoiceDirection direction, LocalDateTime fromDate, LocalDateTime toDate);
}

