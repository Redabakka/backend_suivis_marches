package uir.ac.ma.suivi_marches.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uir.ac.ma.suivi_marches.model.Employe;


public interface EmployeRepo extends JpaRepository<Employe, Integer> {

}