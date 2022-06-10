package uz.sicnt.app.domain.individuals;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "INDIVIDUALS")
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Individual {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "INDIVIDUAL_ID")
    private Long individualId;

    @Column(name = "PINFL")
    private Long pinfl;

    @Column(name = "TIN")
    private String tin;

    @Column(name = "TIN_GIVEN_DATE")
    private Date tinGivenDate;

    @Column(name = "FIRST_NAME_CYRILLIC")
    private String firstNameCyrillic;

    @Column(name = "LAST_NAME_CYRILLIC")
    private String lastNameCyrillic;

    @Column(name = "MIDDLE_NAME_CYRILLIC")
    private String middleNameCyrillic;

    @Column(name = "FIRST_NAME_LATIN")
    private String firstNameLatin;

    @Column(name = "LAST_NAME_LATIN")
    private String lastNameLatin;

    @Column(name = "MIDDLE_NAME_LATIN")
    private String middleNameLatin;

    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @Column(name = "LIVE_STATE")
    private String liveState;

    @Column(name = "META_INFO")
    private Short metaInfo;

    @Column(name = "IS_RESIDENT")
    private Short isResident;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

}
