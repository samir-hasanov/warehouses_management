package www.stock.az.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import www.stock.az.enums.CounterpartyType;

@Entity
@Table(name = "counterparties", schema = "management")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Counterparty extends BaseEntity {

    @Column(name = "voen", nullable = false, length = 20)
    private String voen;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CounterpartyType type;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", length = 1000)
    private String address;
}

