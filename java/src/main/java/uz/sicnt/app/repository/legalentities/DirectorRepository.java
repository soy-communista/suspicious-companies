package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.legalentities.Director;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, String> {

    Optional<Director> findFirstByPinfl(String individualId);
    List<Director> findAllByPinfl(String pinfl);

    @Query(
            nativeQuery = true,
            value = "select * from" +
                    "( " +
                    "select " +
                    "       t1.company_tin as COMPANY_TIN " +
                    ",      to_char(t1.person_id) as PINFL " +
                    ",      t2.tin as TIN " +
                    ",      'founder' AS OCCUPATION " +
                    "from founders t1 " +
                    "left join individual t2 on t1.person_id = t2.pinfl " +
                    "union all " +
                    "select " +
                    "       t3.company_tin as COMPANY_TIN " +
                    ",      to_char(t3.pinfl) as PINFL " +
                    ",      t4.tin AS TIN " +
                    ",      'director' AS OCCUPATION " +
                    "from directors t3 " +
                    "left join individual t4 on t3.pinfl = t4.pinfl " +
                    ") " +
                    "where company_tin = :tin"
    )
    List<Object[]> getManagers(@Param("tin") String tin);

}
