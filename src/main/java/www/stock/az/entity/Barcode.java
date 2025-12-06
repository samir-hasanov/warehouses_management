package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Barcode entity for product barcode support
 */
@Entity
@Table(name = "barcodes", schema = "click_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"barcode", "product_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Barcode extends BaseEntity {

    @Column(name = "barcode", nullable = false, unique = true, length = 100)
    private String barcode;

    @Column(name = "barcode_type", length = 50)
    private String barcodeType; // EAN-13, UPC, CODE-128, etc.

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "description", length = 200)
    private String description;
}
