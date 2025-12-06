package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Stock entity - represents stock quantity in a specific warehouse
 */
@Entity
@Table(name = "stocks", schema = "click_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "reserved_quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal reservedQuantity = BigDecimal.ZERO; // Reserved for orders

    @Column(name = "available_quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal availableQuantity = BigDecimal.ZERO; // quantity - reservedQuantity

    @Column(name = "min_stock_level", precision = 15, scale = 3)
    private BigDecimal minStockLevel; // Alert when stock goes below this

    @Column(name = "max_stock_level", precision = 15, scale = 3)
    private BigDecimal maxStockLevel;

    @Column(name = "location", length = 100)
    private String location; // Location within warehouse (shelf, rack, etc.)

    @Version
    @Column(name = "version")
    private Long version; // Optimistic locking for concurrent updates
}
