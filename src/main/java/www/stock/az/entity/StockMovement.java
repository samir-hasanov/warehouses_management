package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.MovementStatus;
import www.stock.az.enums.MovementType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * StockMovement entity - represents Stock-IN, Stock-OUT, and Transfer operations
 */
@Entity
@Table(name = "stock_movements", schema = "click_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement extends BaseEntity {

    @Column(name = "movement_number", nullable = false, unique = true, length = 100)
    private String movementNumber; // Auto-generated document number

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 50)
    private MovementType movementType; // STOCK_IN, STOCK_OUT, TRANSFER

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MovementStatus status = MovementStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id")
    private Warehouse sourceWarehouse; // null for STOCK_IN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_warehouse_id")
    private Warehouse targetWarehouse; // null for STOCK_OUT

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // External reference (PO number, Invoice, etc.)

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "stockMovement", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<StockMovementItem> items = new ArrayList<>();
}
