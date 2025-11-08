package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.UtilisateurService;
import uir.ac.ma.suivi_marches.model.Utilisateur;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
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

        return ResponseEntity.ok(Map.of(
                "message", "Connexion réussie",
                "role", user.get().getRole(),
                "username", user.get().getUsername()
        ));
    }

    // 🔹 Inscription
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String role = request.get("role");
        Integer idEmploye = request.containsKey("idEmploye") ? Integer.valueOf(request.get("idEmploye")) : null;

        if (utilisateurService.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nom d’utilisateur déjà utilisé"));
        }

        Utilisateur newUser = utilisateurService.register(idEmploye, username, password, role);
        return ResponseEntity.ok(Map.of("message", "Utilisateur créé", "id_user", newUser.getId_user()));
    }
}
