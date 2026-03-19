package www.stock.az.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineRequest {

    private Long productId;       // İstəyə bağlı: Excel importda Kod ilə axtarılır

    private String productCode;   // Excel: Kod
    private String productName;   // Excel: Malın Adı
    private String unit;          // Excel: Vahid
    private String categoryName;  // Excel: Məhsul Kategoriyası
    private String stockNumber;   // Excel: Stok Nömrəsi
    private String manufacturer;  // Excel: İstehsalçı
    private String batchNumber;   // Excel: Partiya Nömrəsi

    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.0000")
    private BigDecimal unitPrice;

    private BigDecimal vatAmount;
}

