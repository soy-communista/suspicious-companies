package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "FOUNDERS")
public class Founder {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PERSON_ID")
    private String personId;

    @Column(name = "SHARE_PERCENT")
    private BigDecimal sharePercent;

    @Column(name = "SHARE_SUM")
    private BigDecimal shareSum;

    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    @Column(name = "PERSON_TYPE")
    private String personType;

    @Column(name = "COMPANY_TIN")
    private String companyTin;

    @Column(name = "ADDRESS_ID")
    private String addressId;

    @Column(name = "CONTACT_ID")
    private String contactId;

    //only user submitter
    @Transient
    private Company legal;

    @Transient
    private Address address;

    @Transient
    private String status;
}
