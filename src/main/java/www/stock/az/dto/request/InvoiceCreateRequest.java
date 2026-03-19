package www.stock.az.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import www.stock.az.enums.InvoiceDirection;
import www.stock.az.enums.InvoiceType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String invoiceNumber;

    @NotNull
    private InvoiceDirection direction;

    private InvoiceType invoiceType;  // Excel: Qaimə Növü (boş ola bilər)

    @NotNull
    private LocalDateTime issueDate;

    @NotBlank
    private String sellerVoen;

    @NotBlank
    private String sellerName;

    @NotBlank
    private String buyerVoen;

    @NotBlank
    private String buyerName;

    @NotNull
    private Long warehouseId;

    private String currency;

    private String notes;

    @Valid
    @NotNull
    private List<InvoiceLineRequest> lines;
}

