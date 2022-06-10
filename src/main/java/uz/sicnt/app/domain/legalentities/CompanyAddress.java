package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "COMPANY_ADDRESSES")
public class CompanyAddress {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "TYPE")
    private String type;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Column(name = "COMPANY_TIN")
    private String company;
}
