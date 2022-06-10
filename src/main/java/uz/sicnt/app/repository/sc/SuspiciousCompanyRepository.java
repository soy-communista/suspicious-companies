package uz.sicnt.app.repository.sc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.sc.SuspiciousCompany;

import java.util.List;

@Repository
public interface SuspiciousCompanyRepository extends JpaRepository<SuspiciousCompany, Integer> {
    List<SuspiciousCompany> findAllByTinIn(List<String> tins);
}
