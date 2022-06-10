package uz.sicnt.app.repository.individuals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.sicnt.app.domain.individuals.Individual;

import java.util.Optional;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long>, JpaSpecificationExecutor<uz.sicnt.app.repository.individuals.IndividualRepository> {
    Optional<Individual> findFirstByPinfl(Long pinfl);
}
