package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.legalentities.Company;

import java.util.Collection;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {

    List<Company> findByTinInAndStatus(Collection<String> tins, Long status);

    Long countAllByStatusIn(List<Long> statuses);

    @Query(value =
//            "select distinct tin from (  " +
//            "    select tin, rownum rn from ( " +
//            "        select distinct t.tin from companies t order by tin " +
//            "    ) where rownum <= :start " +
//            ") where rn > :capacity " +
//            "order by tin ",
            "SELECT tin FROM companies " +
            "where status in (0, 11) " +
            "order by tin " +
            "offset :start rows fetch next :capacity rows only ",
            nativeQuery = true
    )
    List<String> sliceCompaniesByPosition(@Param("start") Long start, @Param("capacity") Long capacity);

}
