package uir.ac.ma.suivi_marches.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uir.ac.ma.suivi_marches.model.Service;


public interface ServiceRepo extends JpaRepository<Service, Integer> {
}