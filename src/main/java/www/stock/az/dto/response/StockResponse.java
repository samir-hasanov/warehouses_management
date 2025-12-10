package www.stock.az.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long id;
    private ProductResponse product;
    private WarehouseResponse warehouse;
    private BigDecimal quantity;
    private BigDecimal reservedQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

