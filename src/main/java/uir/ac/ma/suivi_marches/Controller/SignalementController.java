package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.SignalementService;
import uir.ac.ma.suivi_marches.Service.TacheService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Signalement;
import uir.ac.ma.suivi_marches.model.Tache;
import uir.ac.ma.suivi_marches.model.Employe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/signalements")
@CrossOrigin
public class SignalementController {

    private final SignalementService signalementService;
    private final TacheService tacheService;
    private final EmployeService employeService;

    public SignalementController(SignalementService signalementService,
                                 TacheService tacheService,
                                 EmployeService employeService) {
        this.signalementService = signalementService;
        this.tacheService = tacheService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer tous les signalements
    @GetMapping
    public ResponseEntity<List<Signalement>> getAllSignalements() {
        List<Signalement> signalements = signalementService.getAllSignalements();
        return ResponseEntity.ok(signalements);
    }

    // 🔹 Récupérer un signalement par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSignalementById(@PathVariable("id") int idSignalement) {
        Optional<Signalement> signalement = signalementService.getSignalementById(idSignalement);

        if (signalement.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Signalement introuvable"));
        }

        return ResponseEntity.ok(signalement.get());
    }

    // 🔹 Ajouter un nouveau signalement
    @PostMapping
    public ResponseEntity<?> addSignalement(@RequestBody Map<String, Object> request) {
        try {
            // Récupération des données
            int idTache = Integer.parseInt(request.get("idTache").toString());
            int idEmploye = Integer.parseInt(request.get("idEmploye").toString());
            String typeStr = request.get("type").toString();
            String commentaire = request.containsKey("commentaire") ?
                    request.get("commentaire").toString() : null;

            // Validations
            if (commentaire != null && commentaire.length() > 2000) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le commentaire ne doit pas dépasser 2000 caractères"));
            }

            // Vérifier que la tâche existe
            Tache tache = tacheService.getTacheById(idTache)
                    .orElseThrow(() -> new IllegalArgumentException("Tâche introuvable: " + idTache));

            // Vérifier que l'employé existe
            Employe employe = employeService.getEmployeById(idEmploye)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable: " + idEmploye));

            // Mapper le type
            Signalement.Type type;
            try {
                type = Signalement.Type.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: VALIDEE, NON_PERTINENTE)"));
            }

            // Créer le signalement
            Signalement signalement = new Signalement();
            signalement.setTache(tache);
            signalement.setEmploye(employe);
            signalement.setType(type);
            signalement.setCommentaire(commentaire);
            signalement.setCreated_at(LocalDateTime.now());

            Signalement savedSignalement = signalementService.addSignalement(signalement);

            return ResponseEntity.ok(Map.of(
                    "message", "Signalement créé avec succès",
                    "id_signalement", savedSignalement.getId_signalement()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier un signalement existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifySignalement(@PathVariable("id") int idSignalement,
                                               @RequestBody Map<String, Object> request) {
        Optional<Signalement> existingSignalement = signalementService.getSignalementById(idSignalement);

        if (existingSignalement.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Signalement introuvable"));
        }

        try {
            Signalement signalement = existingSignalement.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("type")) {
                String typeStr = request.get("type").toString();
                try {
                    Signalement.Type type = Signalement.Type.valueOf(typeStr.toUpperCase());
                    signalement.setType(type);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: VALIDEE, NON_PERTINENTE)"));
                }
            }

            if (request.containsKey("commentaire")) {
                String commentaire = request.get("commentaire").toString();
                if (commentaire != null && commentaire.length() > 2000) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le commentaire ne doit pas dépasser 2000 caractères"));
                }
                signalement.setCommentaire(commentaire);
            }

            Signalement updatedSignalement = signalementService.modifySignalement(signalement);

            return ResponseEntity.ok(Map.of(
                    "message", "Signalement modifié avec succès",
                    "signalement", updatedSignalement
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer un signalement
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSignalement(@PathVariable("id") int idSignalement) {
        Optional<Signalement> signalement = signalementService.getSignalementById(idSignalement);

        if (signalement.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Signalement introuvable"));
        }

        try {
            signalementService.deleteSignalement(idSignalement);
            return ResponseEntity.ok(Map.of("message", "Signalement supprimé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer les signalements par tâche
    @GetMapping("/tache/{idTache}")
    public ResponseEntity<?> getSignalementsByTache(@PathVariable("idTache") int idTache) {
        // Vérifier que la tâche existe
        if (tacheService.getTacheById(idTache).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getTache().getId_tache().equals(idTache))
                .toList();

        return ResponseEntity.ok(signalements);
    }

    // 🔹 Récupérer les signalements par employé
    @GetMapping("/employe/{idEmploye}")
    public ResponseEntity<?> getSignalementsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getEmploye().getId_employe().equals(idEmploye))
                .toList();

        return ResponseEntity.ok(signalements);
    }

    // 🔹 Récupérer les signalements par type
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getSignalementsByType(@PathVariable("type") String typeStr) {
        try {
            Signalement.Type type = Signalement.Type.valueOf(typeStr.toUpperCase());

            List<Signalement> signalements = signalementService.getAllSignalements()
                    .stream()
                    .filter(s -> s.getType() == type)
                    .toList();

            return ResponseEntity.ok(signalements);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: VALIDEE, NON_PERTINENTE)"));
        }
    }

    // 🔹 Récupérer les signalements validés
    @GetMapping("/validees")
    public ResponseEntity<List<Signalement>> getSignalementsValidees() {
        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getType() == Signalement.Type.VALIDEE)
                .toList();

        return ResponseEntity.ok(signalements);
    }

    // 🔹 Récupérer les signalements non pertinents
    @GetMapping("/non-pertinentes")
    public ResponseEntity<List<Signalement>> getSignalementsNonPertinentes() {
        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getType() == Signalement.Type.NON_PERTINENTE)
                .toList();

        return ResponseEntity.ok(signalements);
    }

    // 🔹 Récupérer les statistiques de signalement pour une tâche
    @GetMapping("/tache/{idTache}/statistiques")
    public ResponseEntity<?> getSignalementStatistiques(@PathVariable("idTache") int idTache) {
        // Vérifier que la tâche existe
        if (tacheService.getTacheById(idTache).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getTache().getId_tache().equals(idTache))
                .toList();

        long validees = signalements.stream()
                .filter(s -> s.getType() == Signalement.Type.VALIDEE)
                .count();

        long nonPertinentes = signalements.stream()
                .filter(s -> s.getType() == Signalement.Type.NON_PERTINENTE)
                .count();

        return ResponseEntity.ok(Map.of(
                "id_tache", idTache,
                "total", signalements.size(),
                "validees", validees,
                "non_pertinentes", nonPertinentes,
                "taux_validation", signalements.isEmpty() ? 0 : (validees * 100.0 / signalements.size())
        ));
    }

    // 🔹 Récupérer les signalements par marché (via les tâches)
    @GetMapping("/marche/{idMarche}")
    public ResponseEntity<?> getSignalementsByMarche(@PathVariable("idMarche") int idMarche) {
        // Récupérer toutes les tâches du marché
        List<Integer> tacheIds = tacheService.getAllTaches()
                .stream()
                .filter(t -> t.getId_marche().equals(idMarche))
                .map(Tache::getId_tache)
                .toList();

        if (tacheIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Récupérer tous les signalements pour ces tâches
        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> tacheIds.contains(s.getTache().getId_tache()))
                .toList();

        return ResponseEntity.ok(signalements);
    }

    // 🔹 Vérifier si un employé a déjà signalé une tâche
    @GetMapping("/tache/{idTache}/employe/{idEmploye}/existe")
    public ResponseEntity<?> checkSignalementExists(@PathVariable("idTache") int idTache,
                                                    @PathVariable("idEmploye") int idEmploye) {
        // Vérifier que la tâche existe
        if (tacheService.getTacheById(idTache).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        Optional<Signalement> signalement = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getTache().getId_tache().equals(idTache) &&
                        s.getEmploye().getId_employe().equals(idEmploye))
                .findFirst();

        if (signalement.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "existe", true,
                    "signalement", signalement.get()
            ));
        } else {
            return ResponseEntity.ok(Map.of("existe", false));
        }
    }

    // 🔹 Récupérer les signalements récents (dernières 24h)
    @GetMapping("/recents")
    public ResponseEntity<List<Signalement>> getSignalementsRecents() {
        LocalDateTime dernieres24h = LocalDateTime.now().minusHours(24);

        List<Signalement> signalements = signalementService.getAllSignalements()
                .stream()
                .filter(s -> s.getCreated_at().isAfter(dernieres24h))
                .toList();

        return ResponseEntity.ok(signalements);
    }
}