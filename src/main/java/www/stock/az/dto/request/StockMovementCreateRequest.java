package www.stock.az.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import www.stock.az.enums.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating a stock movement (STOCK_IN, STOCK_OUT, TRANSFER)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementCreateRequest {
    
    @NotNull(message = "Hərəkət növü mütləqdir")
    private MovementType movementType;
    
    private Long sourceWarehouseId; // null for STOCK_IN
    
    private Long targetWarehouseId; // null for STOCK_OUT
    
    private LocalDateTime movementDate;
    
    @NotEmpty(message = "Ən azı bir məhsul əlavə edilməlidir")
    @Valid
    private List<StockMovementItemRequest> items;
    
    private String notes;
    
    private String referenceNumber;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMovementItemRequest {
        @NotNull(message = "Məhsul ID mütləqdir")
        private Long productId;
        
        @NotNull(message = "Miqdar mütləqdir")
        @Positive(message = "Miqdar müsbət olmalıdır")
        private BigDecimal quantity;
    }
}

