package www.stock.az.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Stock IN operation via barcode scanner
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInRequest {
    
    @NotNull(message = "Anbar ID mütləqdir")
    private Long warehouseId;
    
    @NotNull(message = "Məhsul barcode mütləqdir")
    private String productBarcode;
    
    @NotNull(message = "Miqdar mütləqdir")
    @Positive(message = "Miqdar müsbət olmalıdır")
    private BigDecimal quantity;
    
    private BigDecimal unitCost;
    
    private String batchNumber;
    
    private String notes;
    
    private String referenceNumber;
}
