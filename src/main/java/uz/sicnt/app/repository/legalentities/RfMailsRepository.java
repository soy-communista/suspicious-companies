package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sicnt.app.domain.legalentities.MovedCompanyStatisticsDto;
import uz.sicnt.app.domain.legalentities.RfMails;

import java.util.Collection;
import java.util.List;

public interface RfMailsRepository extends JpaRepository<RfMails, String>, JpaSpecificationExecutor<RfMails> {
    @Query("SELECT " +
            "    new uz.sicnt.app.domain.legalentities.MovedCompanyStatisticsDto(rm.tin, COUNT(rm.tin)) " +
            "FROM " +
            "    RfMails rm " +
            "WHERE      (rm.answerDate is not null) " +
            "       and (concat(rm.ns10Code, rm.ns11Code, rm.ownNs10Code, rm.ownNs11Code) <> concat(rm.sendNs10, rm.sendNs11, rm.ownSendNs10 , rm.ownSendNs11)) " +
            "       and rm.answerDate > :currMinus12" +
            "       and rm.tin in (:tins) " +
            "GROUP BY " +
            "    rm.tin " +
            "HAVING COUNT(rm.tin) > 3"
    )
    List<MovedCompanyStatisticsDto> findByTin(@Param("tins") Collection<String> tins, @Param("currMinus12") java.sql.Date currMinus12);
}
