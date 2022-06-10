package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@Table(name = "NLMERGE")
public class NlMerge {

    @Column(name = "NS10_CODE")
    private short ns10Code;

    @Column(name = "NS11_CODE")
    private short ns11Code;

    @Column(name = "TIN_FROM")
    private String tinFrom;

    @Column(name = "TIN_TO")
    private String tinTo;

    @Column(name = "STATE")
    private BigInteger state;

    @Column(name = "NA3_CODE")
    private String na3Code;

    @Column(name = "DATE_SYS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSys;

    @Id
    @Column(name = "PKEY")
    private String pkey;

    @Column(name = "DATE_SROK")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSrok;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "OWN_NS10_CODE")
    private Short ownNs10Code;

    @Column(name = "OWN_NS11_CODE")
    private Short ownNs11Code;

}