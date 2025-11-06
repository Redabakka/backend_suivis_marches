package uir.ac.ma.suivi_marches.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uir.ac.ma.suivi_marches.model.Approbation;

@Repository
public interface ApprobationRepo extends JpaRepository<Approbation, Integer> {

}