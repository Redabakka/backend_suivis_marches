package uir.ac.ma.suivi_marches.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uir.ac.ma.suivi_marches.Repository.EmployeRepo;
import uir.ac.ma.suivi_marches.Repository.UtilisateurRepo;
import uir.ac.ma.suivi_marches.model.Employe;
import uir.ac.ma.suivi_marches.model.Utilisateur;

import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepo utilisateurRepo;
    private final EmployeRepo employeRepo;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepo utilisateurRepo,
                              EmployeRepo employeRepo,
                              PasswordEncoder passwordEncoder) {
        this.utilisateurRepo = utilisateurRepo;
        this.employeRepo = employeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Utilisateur> login(String username, String rawPassword) {
        return utilisateurRepo.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword_hash()));
    }

    // 🔥 VERSION CORRIGÉE – RÔLE EN ENUM, PLUS DE STRING
    public Utilisateur register(Integer idEmploye, String username, String password, Utilisateur.Role roleEnum) {

        // Vérifier doublon username
        utilisateurRepo.findByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("Username déjà pris");
        });

        // Vérifier employé si fourni
        Employe employe = null;
        if (idEmploye != null) {
            employe = employeRepo.findById(idEmploye)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable: " + idEmploye));
        }

        // Créer utilisateur
        Utilisateur u = new Utilisateur();
        u.setEmploye(employe);
        u.setUsername(username);
        u.setPassword_hash(passwordEncoder.encode(password));
        u.setRole(roleEnum);   // ⬅️ ENUM, et c’est OK avec ta DB

        return utilisateurRepo.save(u);
    }

    public boolean existsByUsername(String username) {
        return utilisateurRepo.findByUsername(username).isPresent();
    }
}

