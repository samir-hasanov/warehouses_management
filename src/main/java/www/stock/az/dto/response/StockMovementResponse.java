package www.stock.az.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import www.stock.az.enums.MovementStatus;
import www.stock.az.enums.MovementType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    private Long id;
    private String movementNumber;
    private MovementType movementType;
    private MovementStatus status;
    private WarehouseResponse sourceWarehouse;
    private WarehouseResponse targetWarehouse;
    private LocalDateTime movementDate;
    private String referenceNumber;
    private String notes;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private List<StockMovementItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
