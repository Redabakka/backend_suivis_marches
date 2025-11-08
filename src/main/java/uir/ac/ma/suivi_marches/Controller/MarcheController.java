package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.MarcheService;
import uir.ac.ma.suivi_marches.Service.ServiceService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Marche;
import uir.ac.ma.suivi_marches.model.Employe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/marches")
@CrossOrigin
public class MarcheController {

    private final MarcheService marcheService;
    private final ServiceService serviceService;
    private final EmployeService employeService;

    public MarcheController(MarcheService marcheService,
                            ServiceService serviceService,
                            EmployeService employeService) {
        this.marcheService = marcheService;
        this.serviceService = serviceService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer tous les marchés
    @GetMapping
    public ResponseEntity<List<Marche>> getAllMarches() {
        List<Marche> marches = marcheService.getAllMarches();
        return ResponseEntity.ok(marches);
    }

    // 🔹 Récupérer un marché par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMarcheById(@PathVariable("id") int idMarche) {
        Optional<Marche> marche = marcheService.getMarcheById(idMarche);

        if (marche.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        return ResponseEntity.ok(marche.get());
    }

    // 🔹 Ajouter un nouveau marché
    @PostMapping
    public ResponseEntity<?> addMarche(@RequestBody Map<String, Object> request) {
        try {
            // Récupération des données
            String intitule = request.get("intitule").toString();
            String objectif = request.containsKey("objectif") ? request.get("objectif").toString() : null;
            BigDecimal budgetEstime = new BigDecimal(request.get("budgetEstime").toString());
            LocalDate dateDebut = LocalDate.parse(request.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(request.get("dateFin").toString());
            String statutStr = request.get("statut").toString();
            int idService = Integer.parseInt(request.get("idService").toString());
            String fichierCpsPath = request.containsKey("fichierCpsPath") ?
                    request.get("fichierCpsPath").toString() : null;
            int idCreatedBy = Integer.parseInt(request.get("idCreatedBy").toString());

            // Validations
            if (intitule == null || intitule.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "L'intitulé est obligatoire"));
            }
            if (intitule.length() > 255) {
                return ResponseEntity.badRequest().body(Map.of("message", "L'intitulé ne doit pas dépasser 255 caractères"));
            }
            if (budgetEstime.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le budget estimé ne peut pas être négatif"));
            }
            if (dateFin.isBefore(dateDebut)) {
                return ResponseEntity.badRequest().body(Map.of("message", "La date de fin doit être après la date de début"));
            }
            if (fichierCpsPath != null && fichierCpsPath.length() > 500) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le chemin du fichier ne doit pas dépasser 500 caractères"));
            }

            // Vérifier que le service existe
            uir.ac.ma.suivi_marches.model.Service service = serviceService.getServiceById(idService)
                    .orElseThrow(() -> new IllegalArgumentException("Service introuvable: " + idService));

            // Vérifier que l'employé créateur existe
            Employe createdBy = employeService.getEmployeById(idCreatedBy)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable: " + idCreatedBy));

            // Mapper le statut
            Marche.Statut statut;
            try {
                statut = Marche.Statut.valueOf(statutStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Statut invalide (attendu: EN_PREPARATION, EN_COURS, TERMINE, ANNULE)"));
            }

            // Créer le marché
            Marche marche = new Marche();
            marche.setIntitule(intitule.trim());
            marche.setObjectif(objectif);
            marche.setBudget_estime(budgetEstime);
            marche.setDate_debut(dateDebut);
            marche.setDate_fin(dateFin);
            marche.setStatut(statut);
            marche.setService(service);
            marche.setFichier_cps_path(fichierCpsPath);
            marche.setCreated_by(createdBy);
            marche.setCreated_at(LocalDateTime.now());

            Marche savedMarche = marcheService.addMarche(marche);

            return ResponseEntity.ok(Map.of(
                    "message", "Marché créé avec succès",
                    "id_marche", savedMarche.getId_marche()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier un marché existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyMarche(@PathVariable("id") int idMarche,
                                          @RequestBody Map<String, Object> request) {
        Optional<Marche> existingMarche = marcheService.getMarcheById(idMarche);

        if (existingMarche.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        try {
            Marche marche = existingMarche.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("intitule")) {
                String intitule = request.get("intitule").toString();
                if (intitule == null || intitule.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "L'intitulé ne peut pas être vide"));
                }
                if (intitule.length() > 255) {
                    return ResponseEntity.badRequest().body(Map.of("message", "L'intitulé ne doit pas dépasser 255 caractères"));
                }
                marche.setIntitule(intitule.trim());
            }

            if (request.containsKey("objectif")) {
                String objectif = request.get("objectif").toString();
                marche.setObjectif(objectif);
            }

            if (request.containsKey("budgetEstime")) {
                BigDecimal budgetEstime = new BigDecimal(request.get("budgetEstime").toString());
                if (budgetEstime.compareTo(BigDecimal.ZERO) < 0) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le budget estimé ne peut pas être négatif"));
                }
                marche.setBudget_estime(budgetEstime);
            }

            if (request.containsKey("dateDebut")) {
                LocalDate dateDebut = LocalDate.parse(request.get("dateDebut").toString());
                if (marche.getDate_fin() != null && dateDebut.isAfter(marche.getDate_fin())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La date de début doit être avant la date de fin"));
                }
                marche.setDate_debut(dateDebut);
            }

            if (request.containsKey("dateFin")) {
                LocalDate dateFin = LocalDate.parse(request.get("dateFin").toString());
                if (marche.getDate_debut() != null && dateFin.isBefore(marche.getDate_debut())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "La date de fin doit être après la date de début"));
                }
                marche.setDate_fin(dateFin);
            }

            if (request.containsKey("statut")) {
                String statutStr = request.get("statut").toString();
                try {
                    Marche.Statut statut = Marche.Statut.valueOf(statutStr.toUpperCase());
                    marche.setStatut(statut);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Statut invalide (attendu: EN_PREPARATION, EN_COURS, TERMINE, ANNULE)"));
                }
            }

            if (request.containsKey("idService")) {
                int idService = Integer.parseInt(request.get("idService").toString());
                uir.ac.ma.suivi_marches.model.Service service = serviceService.getServiceById(idService)
                        .orElseThrow(() -> new IllegalArgumentException("Service introuvable: " + idService));
                marche.setService(service);
            }

            if (request.containsKey("fichierCpsPath")) {
                String fichierCpsPath = request.get("fichierCpsPath").toString();
                if (fichierCpsPath != null && fichierCpsPath.length() > 500) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le chemin du fichier ne doit pas dépasser 500 caractères"));
                }
                marche.setFichier_cps_path(fichierCpsPath);
            }

            Marche updatedMarche = marcheService.modifyMarche(marche);

            return ResponseEntity.ok(Map.of(
                    "message", "Marché modifié avec succès",
                    "marche", updatedMarche
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer un marché
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarche(@PathVariable("id") int idMarche) {
        Optional<Marche> marche = marcheService.getMarcheById(idMarche);

        if (marche.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Marché introuvable"));
        }

        try {
            marcheService.deleteMarche(idMarche);
            return ResponseEntity.ok(Map.of("message", "Marché supprimé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer les marchés par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> getMarchesByStatut(@PathVariable("statut") String statutStr) {
        try {
            Marche.Statut statut = Marche.Statut.valueOf(statutStr.toUpperCase());

            List<Marche> marches = marcheService.getAllMarches()
                    .stream()
                    .filter(m -> m.getStatut() == statut)
                    .toList();

            return ResponseEntity.ok(marches);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Statut invalide (attendu: EN_PREPARATION, EN_COURS, TERMINE, ANNULE)"));
        }
    }

    // 🔹 Récupérer les marchés par service
    @GetMapping("/service/{idService}")
    public ResponseEntity<?> getMarchesByService(@PathVariable("idService") int idService) {
        // Vérifier que le service existe
        if (serviceService.getServiceById(idService).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Service introuvable"));
        }

        List<Marche> marches = marcheService.getAllMarches()
                .stream()
                .filter(m -> m.getService().getId_service().equals(idService))
                .toList();

        return ResponseEntity.ok(marches);
    }

    // 🔹 Récupérer les marchés créés par un employé
    @GetMapping("/created-by/{idEmploye}")
    public ResponseEntity<?> getMarchesByCreator(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Marche> marches = marcheService.getAllMarches()
                .stream()
                .filter(m -> m.getCreated_by().getId_employe().equals(idEmploye))
                .toList();

        return ResponseEntity.ok(marches);
    }

    // 🔹 Récupérer les marchés actifs (EN_PREPARATION ou EN_COURS)
    @GetMapping("/actifs")
    public ResponseEntity<List<Marche>> getActiveMarches() {
        List<Marche> marches = marcheService.getAllMarches()
                .stream()
                .filter(m -> m.getStatut() == Marche.Statut.EN_PREPARATION ||
                        m.getStatut() == Marche.Statut.EN_COURS)
                .toList();

        return ResponseEntity.ok(marches);
    }

    // 🔹 Récupérer les marchés par plage de dates
    @GetMapping("/dates")
    public ResponseEntity<?> getMarchesByDateRange(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {

        try {
            Stream<Marche> marches = marcheService.getAllMarches().stream();

            if (dateDebut != null) {
                LocalDate debut = LocalDate.parse(dateDebut);
                marches = marches.filter(m -> m.getDate_debut() != null &&
                        !m.getDate_debut().isBefore(debut));
            }

            if (dateFin != null) {
                LocalDate fin = LocalDate.parse(dateFin);
                marches = marches.filter(m -> m.getDate_fin() != null &&
                        !m.getDate_fin().isAfter(fin));
            }

            return ResponseEntity.ok(marches.toList());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Format de date invalide. Utilisez: YYYY-MM-DD"
            ));
        }
    }

    // 🔹 Récupérer les marchés par budget
    @GetMapping("/budget")
    public ResponseEntity<?> getMarchesByBudgetRange(
            @RequestParam(required = false) String budgetMin,
            @RequestParam(required = false) String budgetMax) {

        try {
            Stream<Marche> marches = marcheService.getAllMarches().stream();

            if (budgetMin != null) {
                BigDecimal min = new BigDecimal(budgetMin);
                marches = marches.filter(m -> m.getBudget_estime() != null &&
                        m.getBudget_estime().compareTo(min) >= 0);
            }

            if (budgetMax != null) {
                BigDecimal max = new BigDecimal(budgetMax);
                marches = marches.filter(m -> m.getBudget_estime() != null &&
                        m.getBudget_estime().compareTo(max) <= 0);
            }

            return ResponseEntity.ok(marches.toList());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Format de budget invalide"
            ));
        }
    }
}