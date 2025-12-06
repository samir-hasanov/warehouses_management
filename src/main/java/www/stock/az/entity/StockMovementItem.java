package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * StockMovementItem entity - items in a stock movement transaction
 */
@Entity
@Table(name = "stock_movement_items", schema = "click_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_movement_id", nullable = false)
    private StockMovement stockMovement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost; // Cost per unit

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost; // quantity * unitCost

    @Column(name = "batch_number", length = 100)
    private String batchNumber; // For batch tracking

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate; // For expiry tracking

    @Column(name = "serial_number", length = 100)
    private String serialNumber; // For serial number tracking

    @Column(name = "notes", length = 500)
    private String notes;
}
