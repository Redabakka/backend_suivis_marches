package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.UtilisateurService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Employe;
import uir.ac.ma.suivi_marches.model.Utilisateur;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final EmployeService employeService;

    public AuthController(UtilisateurService utilisateurService, EmployeService employeService) {
        this.utilisateurService = utilisateurService;
        this.employeService = employeService;
    }

    // 🔹 Connexion
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<Utilisateur> user = utilisateurService.login(username, password);

        if (user.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Identifiants incorrects"));
        }

        Utilisateur u = user.get();

        return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie",
                "username", u.getUsername(),
                "role", u.getRole().name()
        ));
    }
    // Inscription
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");
        String roleString = request.get("role");

        // idEmploye optionnel
        Integer idEmploye = request.containsKey("idEmploye")
                ? Integer.valueOf(request.get("idEmploye"))
                : null;

        if (utilisateurService.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nom d’utilisateur déjà utilisé"));
        }

        // Vérification du rôle
        Utilisateur.Role roleEnum;
        try {
            roleEnum = Utilisateur.Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Rôle invalide. Valeurs acceptées : ADMIN, CHEF, EMPLOYE"
            ));
        }

        // 🔥 ICI : on passe idEmploye (Integer), PAS employe
        Utilisateur newUser = utilisateurService.register(idEmploye, username, password, roleEnum);

        return ResponseEntity.ok(Map.of(
                "message", "Utilisateur créé",
                "id_user", newUser.getId_user()
        ));
    }

}
