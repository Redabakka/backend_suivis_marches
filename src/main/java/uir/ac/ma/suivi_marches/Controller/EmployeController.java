package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.Service.ServiceService;
import uir.ac.ma.suivi_marches.model.Employe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/employes")
@CrossOrigin
public class EmployeController {

    private final EmployeService employeService;
    private final ServiceService serviceService;

    // Validation email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public EmployeController(EmployeService employeService, ServiceService serviceService) {
        this.employeService = employeService;
        this.serviceService = serviceService;
    }

    // 🔹 Récupérer tous les employés
    @GetMapping
    public ResponseEntity<List<Employe>> getAllEmployes() {
        return ResponseEntity.ok(employeService.getAllEmployes());
    }

    // 🔹 Récupérer un employé par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeById(@PathVariable("id") int idEmploye) {
        return employeService.getEmployeById(idEmploye)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Employé introuvable")));
    }

    // 🔹 Ajouter un employé
    @PostMapping
    public ResponseEntity<?> addEmploye(@RequestBody Map<String, Object> request) {
        try {
            String nom = request.get("nom").toString();
            String prenom = request.get("prenom").toString();
            String email = request.get("email").toString();
            String roleStr = request.get("role").toString();
            int idService = Integer.parseInt(request.get("idService").toString());
            boolean actif = !request.containsKey("actif") || Boolean.parseBoolean(request.get("actif").toString());

            // validations simples
            if (nom.trim().isEmpty() || prenom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Nom/prénom obligatoires"));
            }
            if (nom.length() > 100 || prenom.length() > 100) {
                return ResponseEntity.badRequest().body(Map.of("message", "Nom/prénom trop long"));
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email invalide"));
            }

            // Vérification service
            var service = serviceService.getServiceById(idService)
                    .orElseThrow(() -> new IllegalArgumentException("Service introuvable : " + idService));

            // Conversion rôle
            Employe.Role role;
            try {
                role = Employe.Role.valueOf(roleStr.toUpperCase());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (ADMIN, CHEF, EMPLOYE)"));
            }

            // Création employé
            Employe employe = new Employe();
            employe.setNom(nom.trim());
            employe.setPrenom(prenom.trim());
            employe.setEmail(email.trim().toLowerCase());
            employe.setRole(role);
            employe.setService(service);
            employe.setActif(actif);
            employe.setCreated_at(LocalDateTime.now());

            Employe saved = employeService.addEmploye(employe);

            return ResponseEntity.ok(Map.of(
                    "message", "Employé créé avec succès",
                    "id_employe", saved.getId_employe()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur : " + e.getMessage()));
        }
    }

    // 🔹 Modifier un employé
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyEmploye(@PathVariable("id") int idEmploye,
                                           @RequestBody Map<String, Object> request) {
        Optional<Employe> existing = employeService.getEmployeById(idEmploye);

        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            Employe employe = existing.get();

            // nom
            if (request.containsKey("nom")) {
                String nom = request.get("nom").toString();
                if (nom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne peut pas être vide"));
                }
                employe.setNom(nom.trim());
            }

            // prenom
            if (request.containsKey("prenom")) {
                String prenom = request.get("prenom").toString();
                if (prenom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le prénom ne peut pas être vide"));
                }
                employe.setPrenom(prenom.trim());
            }

            // email
            if (request.containsKey("email")) {
                String email = request.get("email").toString();
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Email invalide"));
                }
                employe.setEmail(email.trim().toLowerCase());
            }

            // role
            if (request.containsKey("role")) {
                try {
                    Employe.Role role = Employe.Role.valueOf(request.get("role").toString().toUpperCase());
                    employe.setRole(role);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (ADMIN, CHEF, EMPLOYE)"));
                }
            }

            // service
            if (request.containsKey("idService")) {
                int idService = Integer.parseInt(request.get("idService").toString());
                var service = serviceService.getServiceById(idService)
                        .orElseThrow(() -> new IllegalArgumentException("Service introuvable : " + idService));
                employe.setService(service);
            }

            // actif
            if (request.containsKey("actif")) {
                employe.setActif(Boolean.parseBoolean(request.get("actif").toString()));
            }

            Employe updated = employeService.modifyEmploye(employe);

            return ResponseEntity.ok(Map.of(
                    "message", "Employé modifié avec succès",
                    "employe", updated
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔹 Désactiver employé (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmploye(@PathVariable("id") int idEmploye) {
        return employeService.getEmployeById(idEmploye)
                .map(e -> {
                    e.setActif(false);
                    employeService.modifyEmploye(e);
                    return ResponseEntity.ok(Map.of("message", "Employé désactivé"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Employé introuvable")));
    }

    // 🔹 Suppression définitive
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDeleteEmploye(@PathVariable("id") int idEmploye) {
        return employeService.getEmployeById(idEmploye)
                .map(e -> {
                    employeService.deleteEmploye(idEmploye);
                    return ResponseEntity.ok(Map.of("message", "Employé supprimé définitivement"));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Employé introuvable")));
    }

    // 🔹 Employés actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<Employe>> getActiveEmployes() {
        List<Employe> employes = employeService.getAllEmployes()
                .stream()
                .filter(Employe::isActif)
                .toList();

        return ResponseEntity.ok(employes);
    }

    // 🔹 Employés par service
    @GetMapping("/service/{idService}")
    public ResponseEntity<?> getEmployesByService(@PathVariable("idService") int idService) {
        if (serviceService.getServiceById(idService).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        List<Employe> list = employeService.getAllEmployes()
                .stream()
                .filter(e -> e.getService().getId_service() == idService)
                .toList();

        return ResponseEntity.ok(list);
    }

    // 🔹 Employés par rôle
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getEmployesByRole(@PathVariable("role") String roleStr) {
        try {
            Employe.Role role = Employe.Role.valueOf(roleStr.toUpperCase());

            List<Employe> list = employeService.getAllEmployes()
                    .stream()
                    .filter(e -> e.getRole() == role)
                    .toList();

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Rôle invalide (ADMIN, CHEF, EMPLOYE)"
            ));
        }
    }
}
