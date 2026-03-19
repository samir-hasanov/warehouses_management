package www.stock.az.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private String categoryName;
    private String stockNumber;
    private String manufacturer;
    private String batchNumber;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amountNet;
    private BigDecimal vatAmount;
    private BigDecimal amountGross;
}

