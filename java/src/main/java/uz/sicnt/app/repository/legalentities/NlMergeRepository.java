package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.legalentities.MovedCompanyStatisticsDto;
import uz.sicnt.app.domain.legalentities.NlMerge;

import java.util.Collection;
import java.util.List;

@Repository
public interface NlMergeRepository extends JpaRepository<NlMerge, String>, JpaSpecificationExecutor<NlMerge> {
    @Query("SELECT " +
            "    new uz.sicnt.app.domain.legalentities.MovedCompanyStatisticsDto(nm.tinFrom, COUNT(DISTINCT nm.tinTo)) " +
            "FROM " +
            "    NlMerge nm " +
            "WHERE nm.tinFrom IN (:tins) " +
            "GROUP BY " +
            "    nm.tinFrom " +
            "HAVING COUNT(DISTINCT nm.tinTo) > 2"
    )
    List<MovedCompanyStatisticsDto> findUniqueMergedCompaniesByTins(@Param("tins") Collection<String> tins);
}
