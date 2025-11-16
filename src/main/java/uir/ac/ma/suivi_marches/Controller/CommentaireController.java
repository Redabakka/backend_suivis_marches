package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.CommentaireService;
import uir.ac.ma.suivi_marches.Service.TacheService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Commentaire;
import uir.ac.ma.suivi_marches.model.Employe;
import uir.ac.ma.suivi_marches.model.Tache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin
public class CommentaireController {

    private final CommentaireService commentaireService;
    private final TacheService tacheService;
    private final EmployeService employeService;

    public CommentaireController(CommentaireService commentaireService,
                                 TacheService tacheService,
                                 EmployeService employeService) {
        this.commentaireService = commentaireService;
        this.tacheService = tacheService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer tous les commentaires
    @GetMapping
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        return ResponseEntity.ok(commentaireService.getAllCommentaires());
    }

    // 🔹 Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentaireById(@PathVariable("id") int idCommentaire) {
        return commentaireService.getCommentaireById(idCommentaire)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Commentaire introuvable")));
    }

    // 🔹 Ajouter un commentaire
    @PostMapping
    public ResponseEntity<?> addCommentaire(@RequestBody Map<String, Object> request) {
        try {
            Integer idTache = Integer.valueOf(request.get("idTache").toString());
            Integer idAuteur = Integer.valueOf(request.get("idAuteur").toString());
            String contenu = request.get("contenu").toString();
            String prioriteStr = request.get("priorite").toString();

            // Vérifier Tâche
            Tache tache = tacheService.getTacheById(idTache)
                    .orElseThrow(() -> new IllegalArgumentException("Tâche introuvable : " + idTache));

            // Vérifier Auteur
            Employe auteur = employeService.getEmployeById(idAuteur)
                    .orElseThrow(() -> new IllegalArgumentException("Auteur introuvable : " + idAuteur));

            // Mapper priorité
            Commentaire.Priorite priorite;
            try {
                priorite = Commentaire.Priorite.valueOf(prioriteStr.toUpperCase());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message",
                        "Priorité invalide (valeurs possibles : URGENT, QUOTIDIEN, INFORMATIF)"
                ));
            }

            // Création du commentaire
            Commentaire commentaire = new Commentaire();
            commentaire.setTache(tache);
            commentaire.setAuteur(auteur);
            commentaire.setContenu(contenu);
            commentaire.setPriorite(priorite);
            commentaire.setCreated_at(LocalDateTime.now());

            Commentaire saved = commentaireService.addCommentaire(commentaire);

            return ResponseEntity.ok(Map.of(
                    "message", "Commentaire ajouté avec succès",
                    "id_commentaire", saved.getId_commentaire()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔹 Modifier un commentaire
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyCommentaire(@PathVariable("id") int idCommentaire,
                                               @RequestBody Map<String, Object> request) {

        Optional<Commentaire> existing = commentaireService.getCommentaireById(idCommentaire);

        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Commentaire introuvable"));
        }

        try {
            Commentaire commentaire = existing.get();

            if (request.containsKey("contenu")) {
                commentaire.setContenu(request.get("contenu").toString());
            }

            if (request.containsKey("priorite")) {
                String p = request.get("priorite").toString();
                try {
                    commentaire.setPriorite(Commentaire.Priorite.valueOf(p.toUpperCase()));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Priorité invalide"));
                }
            }

            Commentaire updated = commentaireService.modifyCommentaire(commentaire);

            return ResponseEntity.ok(Map.of(
                    "message", "Commentaire modifié avec succès",
                    "commentaire", updated
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔹 Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentaire(@PathVariable("id") int idCommentaire) {
        Optional<Commentaire> commentaire = commentaireService.getCommentaireById(idCommentaire);

        if (commentaire.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Commentaire introuvable"));
        }

        try {
            commentaireService.deleteCommentaire(idCommentaire);
            return ResponseEntity.ok(Map.of("message", "Commentaire supprimé avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
