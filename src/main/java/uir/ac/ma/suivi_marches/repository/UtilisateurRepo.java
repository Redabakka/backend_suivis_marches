package uir.ac.ma.suivi_marches.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uir.ac.ma.suivi_marches.model.Utilisateur;
import java.util.Optional;

public interface UtilisateurRepo extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByUsername(String username);
}

