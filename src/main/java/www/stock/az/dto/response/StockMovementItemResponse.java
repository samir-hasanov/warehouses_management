package www.stock.az.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementItemResponse {
    private Long id;
    private ProductResponse product;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private String batchNumber;
    private LocalDate expiryDate;
    private String serialNumber;
    private String notes;
}
