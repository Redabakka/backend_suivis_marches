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

    // Pattern pour validation email
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
        List<Employe> employes = employeService.getAllEmployes();
        return ResponseEntity.ok(employes);
    }

    // 🔹 Récupérer un employé par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeById(@PathVariable("id") int idEmploye) {
        Optional<Employe> employe = employeService.getEmployeById(idEmploye);

        if (employe.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        return ResponseEntity.ok(employe.get());
    }

    // 🔹 Ajouter un nouveau employé
    @PostMapping
    public ResponseEntity<?> addEmploye(@RequestBody Map<String, Object> request) {
        try {
            // Récupération des données
            String nom = request.get("nom").toString();
            String prenom = request.get("prenom").toString();
            String email = request.get("email").toString();
            String roleStr = request.get("role").toString();
            int idService = Integer.parseInt(request.get("idService").toString());
            boolean actif = !request.containsKey("actif") || Boolean.parseBoolean(request.get("actif").toString());

            // Validations
            if (nom == null || nom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le nom est obligatoire"));
            }
            if (prenom == null || prenom.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le prénom est obligatoire"));
            }
            if (nom.length() > 100) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne doit pas dépasser 100 caractères"));
            }
            if (prenom.length() > 100) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le prénom ne doit pas dépasser 100 caractères"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "L'email est obligatoire"));
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Format d'email invalide"));
            }
            if (email.length() > 150) {
                return ResponseEntity.badRequest().body(Map.of("message", "L'email ne doit pas dépasser 150 caractères"));
            }

            // Vérifier que le service existe
            uir.ac.ma.suivi_marches.model.Service service = serviceService.getServiceById(idService)
                    .orElseThrow(() -> new IllegalArgumentException("Service introuvable: " + idService));

            // Mapper le rôle
            Employe.Role role;
            try {
                role = Employe.Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (attendu: ADMIN, CHEF, EMPLOYE)"));
            }

            // Créer l'employé
            Employe employe = new Employe();
            employe.setNom(nom.trim());
            employe.setPrenom(prenom.trim());
            employe.setEmail(email.trim().toLowerCase());
            employe.setRole(role);
            employe.setService(service);
            employe.setActif(actif);
            employe.setCreated_at(LocalDateTime.now());

            Employe savedEmploye = employeService.addEmploye(employe);

            return ResponseEntity.ok(Map.of(
                    "message", "Employé créé avec succès",
                    "id_employe", savedEmploye.getId_employe()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier un employé existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyEmploye(@PathVariable("id") int idEmploye,
                                           @RequestBody Map<String, Object> request) {
        Optional<Employe> existingEmploye = employeService.getEmployeById(idEmploye);

        if (existingEmploye.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            Employe employe = existingEmploye.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("nom")) {
                String nom = request.get("nom").toString();
                if (nom == null || nom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne peut pas être vide"));
                }
                if (nom.length() > 100) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le nom ne doit pas dépasser 100 caractères"));
                }
                employe.setNom(nom.trim());
            }

            if (request.containsKey("prenom")) {
                String prenom = request.get("prenom").toString();
                if (prenom == null || prenom.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le prénom ne peut pas être vide"));
                }
                if (prenom.length() > 100) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le prénom ne doit pas dépasser 100 caractères"));
                }
                employe.setPrenom(prenom.trim());
            }

            if (request.containsKey("email")) {
                String email = request.get("email").toString();
                if (email == null || email.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "L'email ne peut pas être vide"));
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Format d'email invalide"));
                }
                if (email.length() > 150) {
                    return ResponseEntity.badRequest().body(Map.of("message", "L'email ne doit pas dépasser 150 caractères"));
                }
                employe.setEmail(email.trim().toLowerCase());
            }

            if (request.containsKey("role")) {
                String roleStr = request.get("role").toString().toUpperCase();
                try {
                    Employe.Role role = Employe.Role.valueOf(roleStr.toUpperCase());
                    employe.setRole(role);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (attendu: ADMIN, CHEF, EMPLOYE)"));
                }
            }

            if (request.containsKey("idService")) {
                int idService = Integer.parseInt(request.get("idService").toString());
                uir.ac.ma.suivi_marches.model.Service service = serviceService.getServiceById(idService)
                        .orElseThrow(() -> new IllegalArgumentException("Service introuvable: " + idService));
                employe.setService(service);
            }

            if (request.containsKey("actif")) {
                boolean actif = Boolean.parseBoolean(request.get("actif").toString());
                employe.setActif(actif);
            }

            Employe updatedEmploye = employeService.modifyEmploye(employe);

            return ResponseEntity.ok(Map.of(
                    "message", "Employé modifié avec succès",
                    "employe", updatedEmploye
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer un employé (soft delete - marquer comme inactif)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmploye(@PathVariable("id") int idEmploye) {
        Optional<Employe> employe = employeService.getEmployeById(idEmploye);

        if (employe.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            // Soft delete: marquer comme inactif
            Employe existingEmploye = employe.get();
            existingEmploye.setActif(false);
            employeService.modifyEmploye(existingEmploye);

            return ResponseEntity.ok(Map.of("message", "Employé désactivé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la désactivation: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer définitivement un employé (hard delete)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentDeleteEmploye(@PathVariable("id") int idEmploye) {
        Optional<Employe> employe = employeService.getEmployeById(idEmploye);

        if (employe.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            employeService.deleteEmploye(idEmploye);
            return ResponseEntity.ok(Map.of("message", "Employé supprimé définitivement"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer uniquement les employés actifs
    @GetMapping("/actifs")
    public ResponseEntity<List<Employe>> getActiveEmployes() {
        List<Employe> employes = employeService.getAllEmployes()
                .stream()
                .filter(Employe::isActif)
                .toList();
        return ResponseEntity.ok(employes);
    }

    // 🔹 Récupérer les employés par service
    @GetMapping("/service/{idService}")
    public ResponseEntity<?> getEmployesByService(@PathVariable("idService") int idService) {
        // Vérifier que le service existe
        if (serviceService.getServiceById(idService).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        List<Employe> employes = employeService.getAllEmployes()
                .stream()
                .filter(e -> e.getService().getId_service().equals(idService))
                .toList();

        return ResponseEntity.ok(employes);
    }

    // 🔹 Récupérer les employés par rôle
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getEmployesByRole(@PathVariable("role") String roleStr) {
        try {
            Employe.Role role = Employe.Role.valueOf(roleStr.toUpperCase());

            List<Employe> employes = employeService.getAllEmployes()
                    .stream()
                    .filter(e -> e.getRole() == role)
                    .toList();

            return ResponseEntity.ok(employes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rôle invalide (attendu: ADMIN, CHEF, EMPLOYE)"));
        }
    }
}