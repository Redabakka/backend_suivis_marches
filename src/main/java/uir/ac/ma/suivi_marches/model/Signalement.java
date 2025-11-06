package uir.ac.ma.suivi_marches.model;
import java.time.LocalDateTime;


public class Signalement {
    private int id_signalement;
    private int id_tache;        // clé étrangère vers tache
    private int id_employe;      // clé étrangère vers employe
    private String type;         // Validée / Non pertinente
    private String commentaire;  // justification
    private LocalDateTime created_at;

    public Signalement() {}

    public Signalement(int id_signalement, int id_tache, int id_employe,
                       String type, String commentaire, LocalDateTime created_at) {
        this.id_signalement = id_signalement;
        this.id_tache = id_tache;
        this.id_employe = id_employe;
        this.type = type;
        this.commentaire = commentaire;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_signalement() { return id_signalement; }
    public void setId_signalement(int id_signalement) { this.id_signalement = id_signalement; }

    public int getId_tache() { return id_tache; }
    public void setId_tache(int id_tache) { this.id_tache = id_tache; }

    public int getId_employe() { return id_employe; }
    public void setId_employe(int id_employe) { this.id_employe = id_employe; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
