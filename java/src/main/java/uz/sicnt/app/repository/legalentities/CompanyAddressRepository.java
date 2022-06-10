package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.legalentities.CompanyAddress;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, String> {
    List<CompanyAddress> findByCompanyIn(Collection<String> companyIds);
}
