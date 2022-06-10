package uz.sicnt.app.repository.sc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.sc.CriterionGroup2;
import uz.sicnt.app.domain.sc.SuspiciousCompany;

import java.util.List;

@Repository
public interface CriterionGroup2Repository extends JpaRepository<CriterionGroup2, Integer> {
}
