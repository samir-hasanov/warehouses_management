package www.stock.az.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateRequest {

    @NotBlank(message = "Anbar kodu mütləqdir")
    @Size(max = 50, message = "Anbar kodu maksimum 50 simvol ola bilər")
    private String code;

    @NotBlank(message = "Anbar adı mütləqdir")
    @Size(max = 200, message = "Anbar adı maksimum 200 simvol ola bilər")
    private String name;

    @Size(max = 500, message = "Təsvir maksimum 500 simvol ola bilər")
    private String description;

    @Size(max = 500, message = "Ünvan maksimum 500 simvol ola bilər")
    private String address;

    @Size(max = 100, message = "Şəhər adı maksimum 100 simvol ola bilər")
    private String city;

    @Size(max = 100, message = "Ölkə adı maksimum 100 simvol ola bilər")
    private String country;

    @Size(max = 50, message = "Telefon maksimum 50 simvol ola bilər")
    private String phone;

    @Size(max = 100, message = "Email maksimum 100 simvol ola bilər")
    private String email;

    private Boolean isActive = true;
}
