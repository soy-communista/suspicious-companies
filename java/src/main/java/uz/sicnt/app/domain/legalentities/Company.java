package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "COMPANIES")
public class Company {

    @Id
    @Column(name = "TIN")
    private String tin;

    @Column(name = "SHORT_NAME", length = 100)
    private String shortName;

    @Column(name = "NAME", length = 300)
    private String name;

    @Column(name = "REGISTRATION_DATE")
    private Date registrationDate;

    @Column(name = "REGISTRATION_NUMBER")
    private String registrationNumber;

    @Column(name = "REREGISTRATION_DATE")
    private Date reregistrationDate;

    @Column(name = "BUSINESS_FUND")
    private BigDecimal businessFund;

    @Column(name = "BUSINESS_TYPE")
    private Long businessType;

    @Column(name = "KFS")
    private Long kfs;

    @Column(name = "OKED")
    private String oked;

    @Column(name = "OPF")
    private Long opf;

    @Column(name = "SOATO")
    private Long soato;

    @Column(name = "SOOGU")
    private String soogu;

    @Column(name = "SOOGU_REGISTRATOR")
    private String sooguRegistrator;

    @Column(name = "STATUS")
    private Long status;

    @Column(name = "STATUS_UPDATED")
    private Date statusUpdated;

    @Column(name = "TAX_MODE")
    private Long taxMode;

    @Column(name = "TAXPAYER_TYPE")
    private Long taxpayerType;

    @Column(name = "VAT_NUMBER")
    private Long vatNumber;

    @Column(name = "LIQUIDATION_DATE")
    private Date liquidationDate;

    @Column(name = "LIQUIDATION_REASON")
    private Long liquidationReason;

    @Column(name = "BUSINESS_STRUCTURE")
    private Integer businessStructure;

    @Column(name = "BUSINESS_FUND_CURRENCY", length = 3)
    private String businessFundCurrency;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp created;

    @UpdateTimestamp
    private Timestamp updated;

    public Company(String tin) {
        this.tin = tin;
    }
}
