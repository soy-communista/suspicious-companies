package uz.sicnt.app.domain.sc;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "criterion_group2")
public class CriterionGroup2 {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "criterion_group2_seq")
    @SequenceGenerator(name = "criterion_group2_seq", sequenceName = "criterion_group2_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "TIN")
    private String tin;

    @Column(name = "CRITERIA_1")
    private Integer criteria1;

    @Column(name = "CRITERIA_2")
    private Integer criteria2;

    @Column(name = "CRITERIA_3")
    private Integer criteria3;

    @Column(name = "CRITERIA_4")
    private Integer criteria4;

    @Column(name = "CRITERIA_5")
    private Integer criteria5;

    @Column(name = "CRITERIA_6")
    private Integer criteria6;

    @Column(name = "CRITERIA_7")
    private Integer criteria7;

    @Column(name = "CRITERIA_8")
    private Integer criteria8;

    @Column(name = "CRITERIA_9")
    private Integer criteria9;

    @Column(name = "CRITERIA_10")
    private Integer criteria10;

    @Column(name = "CRITERIA_11")
    private Integer criteria11;

    @Column(name = "CRITERIA_12")
    private Integer criteria12;

    @Column(name = "CRITERIA_13")
    private Integer criteria13;

    @Column(name = "CRITERIA_14")
    private Integer criteria14;

    @Column(name = "CRITERIA_15")
    private Integer criteria15;

    @Column(name = "CRITERIA_16")
    private Integer criteria16;

    @Column(name = "CRITERIA_17")
    private Integer criteria17;

    @Column(name = "CRITERIA_18")
    private Integer criteria18;

    @Column(name = "CRITERIA_30")
    private Integer criteria30;

    @Column(name = "CRITERIA_31")
    private Integer criteria31;

    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "LABEL")
    private Integer label;

}
