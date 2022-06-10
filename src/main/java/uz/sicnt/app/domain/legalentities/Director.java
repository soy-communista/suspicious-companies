package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DIRECTORS")
public class Director {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "COMPANY_TIN")
    private String companyTin;

    @Column(name = "PINFL")
    private String pinfl;

}
