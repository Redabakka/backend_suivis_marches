package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.CommentaireService;
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

    public CommentaireController(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

    // 🔹 Récupérer tous les commentaires
    @GetMapping
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        List<Commentaire> commentaires = commentaireService.getAllCommentaires();
        return ResponseEntity.ok(commentaires);
    }

    // 🔹 Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentaireById(@PathVariable("id") int idCommentaire) {
        Optional<Commentaire> commentaire = commentaireService.getCommentaireById(idCommentaire);

        if (commentaire.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Commentaire introuvable"));
        }

        return ResponseEntity.ok(commentaire.get());
    }

    // 🔹 Ajouter un nouveau commentaire
    @PostMapping
    public ResponseEntity<?> addCommentaire(@RequestBody Map<String, Object> request) {
        try {
            Commentaire commentaire = new Commentaire();

            // Récupérer les IDs depuis la requête
            Integer idTache = Integer.valueOf(request.get("idTache").toString());
            Integer idAuteur = Integer.valueOf(request.get("idAuteur").toString());
            String contenu = request.get("contenu").toString();
            String prioriteStr = request.get("priorite").toString();

            // Créer les entités liées (vous devrez injecter les services appropriés si nécessaire)
            Tache tache = new Tache();
            tache.setId_tache(idTache);

            Employe auteur = new Employe();
            auteur.setId_employe(idAuteur);

            // Mapper la priorité
            Commentaire.Priorite priorite;
            try {
                priorite = Commentaire.Priorite.valueOf(prioriteStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Priorité invalide (attendu: URGENT, QUOTIDIEN, INFORMATIF)"));
            }

            commentaire.setTache(tache);
            commentaire.setAuteur(auteur);
            commentaire.setContenu(contenu);
            commentaire.setPriorite(priorite);
            commentaire.setCreated_at(LocalDateTime.now());

            Commentaire savedCommentaire = commentaireService.addCommentaire(commentaire);
            return ResponseEntity.ok(Map.of(
                    "message", "Commentaire ajouté avec succès",
                    "id_commentaire", savedCommentaire.getId_commentaire()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'ajout: " + e.getMessage()));
        }
    }

    // 🔹 Modifier un commentaire existant
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyCommentaire(@PathVariable("id") int idCommentaire,
                                               @RequestBody Map<String, Object> request) {
        Optional<Commentaire> existingCommentaire = commentaireService.getCommentaireById(idCommentaire);

        if (existingCommentaire.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Commentaire introuvable"));
        }

        try {
            Commentaire commentaire = existingCommentaire.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("contenu")) {
                commentaire.setContenu(request.get("contenu").toString());
            }

            if (request.containsKey("priorite")) {
                String prioriteStr = request.get("priorite").toString();
                try {
                    Commentaire.Priorite priorite = Commentaire.Priorite.valueOf(prioriteStr.toUpperCase());
                    commentaire.setPriorite(priorite);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Priorité invalide"));
                }
            }

            Commentaire updatedCommentaire = commentaireService.modifyCommentaire(commentaire);
            return ResponseEntity.ok(Map.of(
                    "message", "Commentaire modifié avec succès",
                    "commentaire", updatedCommentaire
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la modification: " + e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}