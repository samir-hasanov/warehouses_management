package www.stock.az.dto.response;

import lombok.Data;
import www.stock.az.enums.CounterpartyType;

@Data
public class CounterpartyResponse {
    private Long id;
    private String voen;
    private String name;
    private CounterpartyType type;
    private String phone;
    private String address;
}

