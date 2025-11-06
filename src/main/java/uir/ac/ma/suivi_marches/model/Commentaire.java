package uir.ac.ma.suivi_marches.model;
import java.time.LocalDateTime;

public class Commentaire {
    private int id_commentaire;
    private int id_tache;         // clé étrangère vers la table tache
    private int id_auteur;        // clé étrangère vers la table employe
    private String contenu;       // texte du commentaire
    private String priorite;      // Urgent / Quotidien / Informatif
    private LocalDateTime created_at;

    public Commentaire() {}

    public Commentaire(int id_commentaire, int id_tache, int id_auteur,
                       String contenu, String priorite, LocalDateTime created_at) {
        this.id_commentaire = id_commentaire;
        this.id_tache = id_tache;
        this.id_auteur = id_auteur;
        this.contenu = contenu;
        this.priorite = priorite;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_commentaire() { return id_commentaire; }
    public void setId_commentaire(int id_commentaire) { this.id_commentaire = id_commentaire; }

    public int getId_tache() { return id_tache; }
    public void setId_tache(int id_tache) { this.id_tache = id_tache; }

    public int getId_auteur() { return id_auteur; }
    public void setId_auteur(int id_auteur) { this.id_auteur = id_auteur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
