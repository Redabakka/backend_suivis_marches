package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.TacheService;
import uir.ac.ma.suivi_marches.Service.MarcheService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Employe;
import uir.ac.ma.suivi_marches.model.Marche;
import uir.ac.ma.suivi_marches.model.Tache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/taches")
@CrossOrigin
public class TacheController {

    private final TacheService tacheService;
    private final MarcheService marcheService;
    private final EmployeService employeService;

    // Valeurs autorisées (doivent matcher le CHECK Postgres)
    private static final List<String> VALID_ETATS =
            Arrays.asList("En attente", "En cours", "Validée", "Non validée");
    private static final List<String> VALID_PRIORITES =
            Arrays.asList("Urgent", "Quotidien", "Informatif");
    private static final List<String> VALID_PERTINENCES =
            Arrays.asList("Pertinente", "Non pertinente", "À revoir");

    public TacheController(TacheService tacheService,
                           MarcheService marcheService,
                           EmployeService employeService) {
        this.tacheService = tacheService;
        this.marcheService = marcheService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer toutes les tâches
    @GetMapping
    public ResponseEntity<List<Tache>> getAllTaches() {
        return ResponseEntity.ok(tacheService.getAllTaches());
    }

    // 🔹 Récupérer une tâche par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTacheById(@PathVariable("id") int idTache) {
        Optional<Tache> tache = tacheService.getTacheById(idTache);

        if (tache.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        return ResponseEntity.ok(tache.get());
    }

    // 🔹 Ajouter une nouvelle tâche
    @PostMapping
    public ResponseEntity<?> addTache(@RequestBody Map<String, Object> request) {
        try {
            // Champs obligatoires
            if (!request.containsKey("idMarche") ||
                    !request.containsKey("titre") ||
                    !request.containsKey("dateDebut") ||
                    !request.containsKey("dateFin") ||
                    !request.containsKey("responsable") ||
                    !request.containsKey("etat") ||
                    !request.containsKey("priorite")) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Champs obligatoires : idMarche, titre, dateDebut, dateFin, responsable, etat, priorite")
                );
            }

            int idMarche = Integer.parseInt(request.get("idMarche").toString());
            String titre = request.get("titre").toString();
            String description = request.containsKey("description")
                    ? String.valueOf(request.get("description"))
                    : null;

            LocalDate dateDebut = LocalDate.parse(request.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(request.get("dateFin").toString());

            // dureeEstimee peut être null
            Integer dureeEstimee = null;
            if (request.containsKey("dureeEstimee") && request.get("dureeEstimee") != null) {
                String val = request.get("dureeEstimee").toString();
                if (!val.isBlank()) {
                    dureeEstimee = Integer.parseInt(val);
                }
            }

            int idResponsable = Integer.parseInt(request.get("responsable").toString());
            String etat = request.get("etat").toString();
            String priorite = request.get("priorite").toString();

            boolean critique = request.containsKey("critique") &&
                    Boolean.parseBoolean(request.get("critique").toString());

            String pertinence = null;
            if (request.containsKey("pertinence") && request.get("pertinence") != null) {
                pertinence = request.get("pertinence").toString();
            }

            // 🔹 Validations
            if (titre == null || titre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le titre est obligatoire"));
            }
            if (titre.length() > 200) { // DB = VARCHAR(200)
                return ResponseEntity.badRequest().body(Map.of("message", "Le titre ne doit pas dépasser 200 caractères"));
            }
            if (dureeEstimee != null && dureeEstimee < 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "La durée estimée ne peut pas être négative"));
            }
            if (dateFin.isBefore(dateDebut)) {
                return ResponseEntity.badRequest().body(Map.of("message", "La date de fin doit être après la date de début"));
            }

            // Vérifier que le marché existe
            Marche marche = marcheService.getMarcheById(idMarche)
                    .orElseThrow(() -> new IllegalArgumentException("Marché introuvable: " + idMarche));

            // Vérifier que l'employé responsable existe
            Employe responsable = employeService.getEmployeById(idResponsable)
                    .orElseThrow(() -> new IllegalArgumentException("Employé responsable introuvable: " + idResponsable));

            // Valider l'état
            if (!VALID_ETATS.contains(etat)) {
                return ResponseEntity.badRequest().body(Map.of("message",
                        "État invalide (attendu: En attente, En cours, Validée, Non validée)"));
            }

            // Valider la priorité
            if (!VALID_PRIORITES.contains(priorite)) {
                return ResponseEntity.badRequest().body(Map.of("message",
                        "Priorité invalide (attendu: Urgent, Quotidien, Informatif)"));
            }

            // Valider la pertinence (si non null)
            if (pertinence != null && !VALID_PERTINENCES.contains(pertinence)) {
                return ResponseEntity.badRequest().body(Map.of("message",
                        "Pertinence invalide (attendu: Pertinente, Non pertinente, À revoir)"));
            }

            // Créer la tâche
            Tache tache = new Tache();
            tache.setMarche(marche);
            tache.setTitre(titre.trim());
            tache.setDescription(description);
            tache.setDate_debut(dateDebut);
            tache.setDate_fin(dateFin);
            tache.setDuree_estimee(dureeEstimee);
            tache.setResponsable(responsable);
            tache.setEtat(etat);
            tache.setPriorite(priorite);
            tache.setCritique(critique);
            tache.setPertinence(pertinence);
            tache.setCreated_at(LocalDateTime.now());

            Tache savedTache = tacheService.addTache(tache);

            return ResponseEntity.ok(Map.of(
                    "message", "Tâche créée avec succès",
                    "id_tache", savedTache.getId_tache()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier une tâche existante
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyTache(@PathVariable("id") int idTache,
                                         @RequestBody Map<String, Object> request) {
        Optional<Tache> existingTacheOpt = tacheService.getTacheById(idTache);

        if (existingTacheOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        try {
            Tache tache = existingTacheOpt.get();

            if (request.containsKey("titre")) {
                String titre = request.get("titre").toString();
                if (titre == null || titre.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le titre ne peut pas être vide"));
                }
                if (titre.length() > 200) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le titre ne doit pas dépasser 200 caractères"));
                }
                tache.setTitre(titre.trim());
            }

            if (request.containsKey("description")) {
                String description = request.get("description") != null
                        ? request.get("description").toString()
                        : null;
                tache.setDescription(description);
            }

            if (request.containsKey("dateDebut")) {
                LocalDate dateDebut = LocalDate.parse(request.get("dateDebut").toString());
                if (tache.getDate_fin() != null && dateDebut.isAfter(tache.getDate_fin())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La date de début doit être avant la date de fin"));
                }
                tache.setDate_debut(dateDebut);
            }

            if (request.containsKey("dateFin")) {
                LocalDate dateFin = LocalDate.parse(request.get("dateFin").toString());
                if (tache.getDate_debut() != null && dateFin.isBefore(tache.getDate_debut())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La date de fin doit être après la date de début"));
                }
                tache.setDate_fin(dateFin);
            }

            if (request.containsKey("dureeEstimee")) {
                Integer dureeEstimee = null;
                if (request.get("dureeEstimee") != null) {
                    String val = request.get("dureeEstimee").toString();
                    if (!val.isBlank()) {
                        dureeEstimee = Integer.parseInt(val);
                    }
                }
                if (dureeEstimee != null && dureeEstimee < 0) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La durée estimée ne peut pas être négative"));
                }
                tache.setDuree_estimee(dureeEstimee);
            }

            if (request.containsKey("responsable")) {
                int idResponsable = Integer.parseInt(request.get("responsable").toString());
                Employe responsable = employeService.getEmployeById(idResponsable)
                        .orElseThrow(() -> new IllegalArgumentException("Employé responsable introuvable: " + idResponsable));
                tache.setResponsable(responsable);
            }

            if (request.containsKey("etat")) {
                String etat = request.get("etat").toString();
                if (!VALID_ETATS.contains(etat)) {
                    return ResponseEntity.badRequest().body(Map.of("message",
                            "État invalide (attendu: En attente, En cours, Validée, Non validée)"));
                }
                tache.setEtat(etat);
            }

            if (request.containsKey("priorite")) {
                String priorite = request.get("priorite").toString();
                if (!VALID_PRIORITES.contains(priorite)) {
                    return ResponseEntity.badRequest().body(Map.of("message",
                            "Priorité invalide (attendu: Urgent, Quotidien, Informatif)"));
                }
                tache.setPriorite(priorite);
            }

            if (request.containsKey("critique")) {
                boolean critique = Boolean.parseBoolean(request.get("critique").toString());
                tache.setCritique(critique);
            }

            if (request.containsKey("pertinence")) {
                String pertinence = request.get("pertinence") != null
                        ? request.get("pertinence").toString()
                        : null;
                if (pertinence != null && !VALID_PERTINENCES.contains(pertinence)) {
                    return ResponseEntity.badRequest().body(Map.of("message",
                            "Pertinence invalide (attendu: Pertinente, Non pertinente, À revoir)"));
                }
                tache.setPertinence(pertinence);
            }

            Tache updatedTache = tacheService.modifyTache(tache);

            return ResponseEntity.ok(Map.of(
                    "message", "Tâche modifiée avec succès",
                    "tache", updatedTache
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer une tâche
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTache(@PathVariable("id") int idTache) {
        Optional<Tache> tache = tacheService.getTacheById(idTache);

        if (tache.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Tâche introuvable"));
        }

        try {
            tacheService.deleteTache(idTache);
            return ResponseEntity.ok(Map.of("message", "Tâche supprimée avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer les tâches par marché
    @GetMapping("/marche/{idMarche}")
    public ResponseEntity<?> getTachesByMarche(@PathVariable("idMarche") int idMarche) {
        if (marcheService.getMarcheById(idMarche).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> t.getMarche() != null &&
                        t.getMarche().getId_marche() != null &&
                        t.getMarche().getId_marche().equals(idMarche))
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches par responsable
    @GetMapping("/responsable/{idEmploye}")
    public ResponseEntity<?> getTachesByResponsable(@PathVariable("idEmploye") int idEmploye) {
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> t.getResponsable() != null &&
                        t.getResponsable().getId_employe() != null &&
                        t.getResponsable().getId_employe().equals(idEmploye))
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches par état
    @GetMapping("/etat/{etat}")
    public ResponseEntity<?> getTachesByEtat(@PathVariable("etat") String etat) {
        if (!VALID_ETATS.contains(etat)) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "État invalide (attendu: En attente, En cours, Validée, Non validée)"));
        }

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> etat.equals(t.getEtat()))
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches par priorité
    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<?> getTachesByPriorite(@PathVariable("priorite") String priorite) {
        if (!VALID_PRIORITES.contains(priorite)) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Priorité invalide (attendu: Urgent, Quotidien, Informatif)"));
        }

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> priorite.equals(t.getPriorite()))
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches critiques
    @GetMapping("/critiques")
    public ResponseEntity<List<Tache>> getTachesCritiques() {
        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(Tache::isCritique)
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches par pertinence
    @GetMapping("/pertinence/{pertinence}")
    public ResponseEntity<?> getTachesByPertinence(@PathVariable("pertinence") String pertinence) {
        if (!VALID_PERTINENCES.contains(pertinence)) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Pertinence invalide (attendu: Pertinente, Non pertinente, À revoir)"));
        }

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> pertinence.equals(t.getPertinence()))
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Récupérer les tâches par plage de dates
    @GetMapping("/dates")
    public ResponseEntity<?> getTachesByDateRange(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {

        try {
            Stream<Tache> taches = tacheService.getAllTaches().stream();

            if (dateDebut != null) {
                LocalDate debut = LocalDate.parse(dateDebut);
                taches = taches.filter(t -> t.getDate_debut() != null &&
                        !t.getDate_debut().isBefore(debut));
            }

            if (dateFin != null) {
                LocalDate fin = LocalDate.parse(dateFin);
                taches = taches.filter(t -> t.getDate_fin() != null &&
                        !t.getDate_fin().isAfter(fin));
            }

            return ResponseEntity.ok(taches.toList());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Format de date invalide. Utilisez: YYYY-MM-DD"
            ));
        }
    }

    // 🔹 Tâches urgentes et critiques
    @GetMapping("/urgentes-critiques")
    public ResponseEntity<List<Tache>> getTachesUrgentesCritiques() {
        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> "Urgent".equals(t.getPriorite()) && t.isCritique())
                .toList();

        return ResponseEntity.ok(taches);
    }

    // 🔹 Tâches en retard (date_fin passée et non validée)
    @GetMapping("/en-retard")
    public ResponseEntity<List<Tache>> getTachesEnRetard() {
        LocalDate today = LocalDate.now();

        List<Tache> taches = tacheService.getAllTaches()
                .stream()
                .filter(t -> t.getDate_fin() != null &&
                        t.getDate_fin().isBefore(today) &&
                        !"Validée".equals(t.getEtat()))
                .toList();

        return ResponseEntity.ok(taches);
    }
}
