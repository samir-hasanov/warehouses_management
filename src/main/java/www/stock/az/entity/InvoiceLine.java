package www.stock.az.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines", schema = "management")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;  // Nullable: Excel importda məhsul kataloqda olmaya bilər

    @Column(name = "product_code", length = 100)
    private String productCode;   // Excel: Kod

    @Column(name = "product_name", length = 500)
    private String productName;   // Excel: Malın Adı

    @Column(name = "unit", length = 50)
    private String unit;          // Excel: Vahid (ədəd, kq və s.)

    @Column(name = "category_name", length = 200)
    private String categoryName;  // Excel: Məhsul Kategoriyası

    @Column(name = "stock_number", length = 100)
    private String stockNumber;   // Excel: Stok Nömrəsi

    @Column(name = "manufacturer", length = 200)
    private String manufacturer;  // Excel: İstehsalçı

    @Column(name = "batch_number", length = 100)
    private String batchNumber;   // Excel: Partiya Nömrəsi

    @Column(name = "quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal quantity;  // Excel: Miqdar

    @Column(name = "unit_price", precision = 18, scale = 4, nullable = false)
    private BigDecimal unitPrice; // Excel: Qiymət

    @Column(name = "amount_net", precision = 18, scale = 2)
    private BigDecimal amountNet; // Excel: Məbləğ

    @Column(name = "vat_amount", precision = 18, scale = 2)
    private BigDecimal vatAmount; // Excel: ƏDV

    @Column(name = "amount_gross", precision = 18, scale = 2)
    private BigDecimal amountGross; // Excel: Yekun
}

