package www.stock.az.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "Kateqoriya kodu mütləqdir")
    @Size(max = 50, message = "Kateqoriya kodu maksimum 50 simvol ola bilər")
    private String code;

    @NotBlank(message = "Kateqoriya adı mütləqdir")
    @Size(max = 200, message = "Kateqoriya adı maksimum 200 simvol ola bilər")
    private String name;

    @Size(max = 500, message = "Təsvir maksimum 500 simvol ola bilər")
    private String description;

    private Long parentId; // For hierarchical categories

    private Boolean isActive = true;
}
