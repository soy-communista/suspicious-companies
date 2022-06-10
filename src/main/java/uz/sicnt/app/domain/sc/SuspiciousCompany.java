package uz.sicnt.app.domain.sc;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "criterion_group1")
public class SuspiciousCompany {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "criterion_group1_seq")
    @SequenceGenerator(name = "criterion_group1_seq", sequenceName = "criterion_group1_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "TIN")
    private String tin;

    @Column(name = "PERSON_TIN")
    private String personTin;

    @Column(name = "PERSON_PINFL")
    private String personPinfl;

    @Column(name = "ACTION_ID")
    private String actionId;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CRITERIA")
    private String criteria;

    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

}
