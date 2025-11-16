package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.ApprobationService;
import uir.ac.ma.suivi_marches.Service.MarcheService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Approbation;
import uir.ac.ma.suivi_marches.model.Marche;
import uir.ac.ma.suivi_marches.model.Employe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/approbations")
@CrossOrigin
public class ApprobationController {

    private final ApprobationService approbationService;
    private final MarcheService marcheService;
    private final EmployeService employeService;

    public ApprobationController(ApprobationService approbationService,
                                 MarcheService marcheService,
                                 EmployeService employeService) {
        this.approbationService = approbationService;
        this.marcheService = marcheService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer toutes les approbations
    @GetMapping
    public ResponseEntity<List<Approbation>> getAllApprobations() {
        return ResponseEntity.ok(approbationService.getAllApprobations());
    }

    // 🔹 Récupérer une approbation par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getApprobationById(@PathVariable("id") int idApprobation) {
        return approbationService.getApprobationById(idApprobation)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Approbation introuvable")));
    }

    // 🔹 Ajouter une nouvelle approbation
    @PostMapping
    public ResponseEntity<?> addApprobation(@RequestBody Map<String, Object> request) {
        try {
            // Champs obligatoires
            if (!request.containsKey("idMarche") ||
                    !request.containsKey("idEmploye") ||
                    !request.containsKey("statut")) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Champs obligatoires : idMarche, idEmploye, statut")
                );
            }

            int idMarche = Integer.parseInt(request.get("idMarche").toString());
            int idEmploye = Integer.parseInt(request.get("idEmploye").toString());
            String statutStr = request.get("statut").toString();
            String motif = request.containsKey("motif") && request.get("motif") != null
                    ? request.get("motif").toString()
                    : null;

            // Validation motif
            if (motif != null && motif.length() > 1000) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Le motif ne doit pas dépasser 1000 caractères")
                );
            }

            // Vérifier que le marché existe
            Marche marche = marcheService.getMarcheById(idMarche)
                    .orElseThrow(() -> new IllegalArgumentException("Marché introuvable: " + idMarche));

            // Vérifier que l'employé existe
            Employe employe = employeService.getEmployeById(idEmploye)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable: " + idEmploye));

            // Normaliser / valider statut -> "Approuvé" ou "Refusé"
            String statutDb = normalizeStatut(statutStr);
            if (statutDb == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Statut invalide (attendu: Approuvé, Refusé)")
                );
            }

            // Vérifier unicité marché + employé
            boolean exists = approbationService.getAllApprobations()
                    .stream()
                    .anyMatch(a -> a.getMarche().getId_marche().equals(idMarche) &&
                            a.getEmploye().getId_employe().equals(idEmploye));

            if (exists) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Une approbation existe déjà pour cet employé et ce marché")
                );
            }

            // Création
            Approbation approbation = new Approbation();
            approbation.setMarche(marche);
            approbation.setEmploye(employe);
            approbation.setStatut(statutDb); // String : "Approuvé" / "Refusé"
            approbation.setMotif(motif);
            approbation.setCreated_at(LocalDateTime.now());

            Approbation saved = approbationService.addApprobation(approbation);

            return ResponseEntity.ok(Map.of(
                    "message", "Approbation créée avec succès",
                    "id_approbation", saved.getId_approbation()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la création: " + e.getMessage())
            );
        }
    }

    // 🔹 Modifier une approbation existante
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyApprobation(@PathVariable("id") int idApprobation,
                                               @RequestBody Map<String, Object> request) {

        Optional<Approbation> existing = approbationService.getApprobationById(idApprobation);

        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Approbation introuvable"));
        }

        try {
            Approbation approbation = existing.get();

            if (request.containsKey("statut")) {
                String statutStr = request.get("statut").toString();
                String statutDb = normalizeStatut(statutStr);
                if (statutDb == null) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Statut invalide (attendu: Approuvé, Refusé)")
                    );
                }
                approbation.setStatut(statutDb);
            }

            if (request.containsKey("motif")) {
                String motif = request.get("motif") != null ? request.get("motif").toString() : null;
                if (motif != null && motif.length() > 1000) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Le motif ne doit pas dépasser 1000 caractères")
                    );
                }
                approbation.setMotif(motif);
            }

            Approbation updated = approbationService.modifyApprobation(approbation);

            return ResponseEntity.ok(Map.of(
                    "message", "Approbation modifiée avec succès",
                    "approbation", updated
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la modification: " + e.getMessage())
            );
        }
    }

    // 🔹 Supprimer une approbation
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApprobation(@PathVariable("id") int idApprobation) {
        Optional<Approbation> approbation = approbationService.getApprobationById(idApprobation);

        if (approbation.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Approbation introuvable"));
        }

        try {
            approbationService.deleteApprobation(idApprobation);
            return ResponseEntity.ok(Map.of("message", "Approbation supprimée avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la suppression: " + e.getMessage())
            );
        }
    }

    // 🔹 Récupérer les approbations par marché
    @GetMapping("/marche/{idMarche}")
    public ResponseEntity<?> getApprobationsByMarche(@PathVariable("idMarche") int idMarche) {
        if (marcheService.getMarcheById(idMarche).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> a.getMarche().getId_marche().equals(idMarche))
                .toList();

        return ResponseEntity.ok(approbations);
    }

    // 🔹 Récupérer les approbations par employé
    @GetMapping("/employe/{idEmploye}")
    public ResponseEntity<?> getApprobationsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> a.getEmploye().getId_employe().equals(idEmploye))
                .toList();

        return ResponseEntity.ok(approbations);
    }

    // 🔹 Récupérer les approbations par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> getApprobationsByStatut(@PathVariable("statut") String statutStr) {
        String statutDb = normalizeStatut(statutStr);
        if (statutDb == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Statut invalide (attendu: Approuvé, Refusé)")
            );
        }

        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> statutDb.equals(a.getStatut()))
                .toList();

        return ResponseEntity.ok(approbations);
    }

    // 🔹 Récupérer les approbations approuvées
    @GetMapping("/approuvees")
    public ResponseEntity<List<Approbation>> getApprobationsApprouvees() {
        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> "Approuvé".equals(a.getStatut()))
                .toList();

        return ResponseEntity.ok(approbations);
    }

    // 🔹 Récupérer les approbations refusées
    @GetMapping("/refusees")
    public ResponseEntity<List<Approbation>> getApprobationsRefusees() {
        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> "Refusé".equals(a.getStatut()))
                .toList();

        return ResponseEntity.ok(approbations);
    }

    // 🔹 Statistiques d'approbation pour un marché
    @GetMapping("/marche/{idMarche}/statistiques")
    public ResponseEntity<?> getApprobationStatistiques(@PathVariable("idMarche") int idMarche) {
        if (marcheService.getMarcheById(idMarche).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        List<Approbation> approbations = approbationService.getAllApprobations()
                .stream()
                .filter(a -> a.getMarche().getId_marche().equals(idMarche))
                .toList();

        long approuvees = approbations.stream()
                .filter(a -> "Approuvé".equals(a.getStatut()))
                .count();

        long refusees = approbations.stream()
                .filter(a -> "Refusé".equals(a.getStatut()))
                .count();

        double tauxApprobation = approbations.isEmpty()
                ? 0
                : (approuvees * 100.0 / approbations.size());

        return ResponseEntity.ok(Map.of(
                "id_marche", idMarche,
                "total", approbations.size(),
                "approuvees", approuvees,
                "refusees", refusees,
                "taux_approbation", tauxApprobation
        ));
    }

    // 🔹 Vérifier si un employé a déjà voté pour un marché
    @GetMapping("/marche/{idMarche}/employe/{idEmploye}/existe")
    public ResponseEntity<?> checkApprobationExists(@PathVariable("idMarche") int idMarche,
                                                    @PathVariable("idEmploye") int idEmploye) {

        if (marcheService.getMarcheById(idMarche).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        Optional<Approbation> approbation = approbationService.getAllApprobations()
                .stream()
                .filter(a -> a.getMarche().getId_marche().equals(idMarche) &&
                        a.getEmploye().getId_employe().equals(idEmploye))
                .findFirst();

        if (approbation.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "existe", true,
                    "approbation", approbation.get()
            ));
        } else {
            return ResponseEntity.ok(Map.of("existe", false));
        }
    }

    // 🔸 Utilitaire privé pour normaliser le statut
    //   - accepte "Approuvé", "APPROUVE", "approuve", etc.
    //   - retourne exactement "Approuvé" ou "Refusé" comme en BDD
    private String normalizeStatut(String input) {
        if (input == null) return null;
        String s = input.trim().toLowerCase();

        if (s.equals("approuve") || s.equals("approuvé")) {
            return "Approuvé";
        }
        if (s.equals("refuse") || s.equals("refusé")) {
            return "Refusé";
        }
        return null;
    }
}
