package uz.sicnt.app.repository.legalentities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.legalentities.Founder;

import java.util.List;
import java.util.Optional;

@Repository
public interface FounderRepository extends JpaRepository<Founder, String>, JpaSpecificationExecutor<Founder> {
    List<Founder> findAllByPersonId_In(List<String> personalIds);
    Optional<Founder> findFirstByPersonId(String personId);
}
