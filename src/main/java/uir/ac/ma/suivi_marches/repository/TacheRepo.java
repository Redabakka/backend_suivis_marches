package uir.ac.ma.suivi_marches.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uir.ac.ma.suivi_marches.model.Tache;


public interface TacheRepo extends JpaRepository<Tache, Integer> {

}