package www.stock.az.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating a new Product with classification and optional initial stock
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "Məhsul kodu mütləqdir")
    private String code;

    @NotBlank(message = "Məhsul adı mütləqdir")
    private String name;

    private String description;

    @NotNull(message = "Kateqoriya mütləqdir")
    private Long categoryId;

    @NotNull(message = "Brend mütləqdir")
    private Long brandId;

    @NotBlank(message = "Vahid mütləqdir")
    private String unit;

    private BigDecimal weight;

    private String dimensions;

    private Boolean isActive = true;

    // Barcode information
    @NotBlank(message = "Əsas barcode mütləqdir")
    private String mainBarcode;

    private List<String> additionalBarcodes;

    // Optional: default minimum stock level and tax
    private BigDecimal defaultMinStockLevel;

    private BigDecimal taxRate;

    // Optional initial stock
    private Long initialWarehouseId;

    @Positive(message = "İlkin miqdar müsbət olmalıdır")
    private BigDecimal initialQuantity;
}
