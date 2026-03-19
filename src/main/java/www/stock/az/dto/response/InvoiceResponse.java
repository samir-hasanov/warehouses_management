package www.stock.az.dto.response;

import lombok.Data;
import www.stock.az.enums.InvoiceDirection;
import www.stock.az.enums.InvoiceStatus;
import www.stock.az.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private InvoiceDirection direction;
    private InvoiceStatus status;
    private InvoiceType invoiceType;
    private LocalDateTime issueDate;
    private CounterpartyResponse seller;
    private CounterpartyResponse buyer;
    private WarehouseResponse warehouse;
    private BigDecimal totalNet;
    private BigDecimal totalVat;
    private BigDecimal totalGross;
    private String currency;
    private String notes;
    private List<InvoiceLineResponse> lines;
}

