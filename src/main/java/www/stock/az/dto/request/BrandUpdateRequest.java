package www.stock.az.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandUpdateRequest {

    @NotBlank(message = "Brand adı mütləqdir")
    @Size(max = 200, message = "Brand adı maksimum 200 simvol ola bilər")
    private String name;

    @Size(max = 500, message = "Təsvir maksimum 500 simvol ola bilər")
    private String description;

    @Size(max = 100, message = "Ölkə adı maksimum 100 simvol ola bilər")
    private String country;

    @Size(max = 200, message = "Website maksimum 200 simvol ola bilər")
    private String website;

    private Boolean isActive;
}
