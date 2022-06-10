package uz.sicnt.app.domain.legalentities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ADDRESSES")
public class Address {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "COUNTRY_CODE")
    private Long countryCode;

    @Column(name = "DISTRICT_CODE")
    private Long districtCode;

    @Column(name = "SOATO_CODE")
    private Long soatoCode;

    @Column(name = "FLAT")
    private String flat;

    @Column(name = "HOUSE")
    private String house;

    @Column(name = "VILLAGE_CODE")
    private Integer villageCode;

    @Column(name = "REGION_CODE")
    private Long regionCode;

    @Column(name = "SECTOR_CODE")
    private Long sectorCode;

    @Column(name = "STREET_NAME")
    private String streetName;

    @Column(name = "CADASTRE_NUMBER", length = 36)
    private String cadastreNumber;

    @Column(name = "POSTCODE", length = 10)
    private String postcode;

}
