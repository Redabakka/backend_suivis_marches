package uir.ac.ma.suivi_marches.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class tache {
    private int id_tache;
    private int id_marche;          // clé étrangère vers la table marche
    private String titre;
    private String description;
    private LocalDate date_debut;
    private LocalDate date_fin;
    private int duree_estimee;
    private int responsable;        // clé étrangère vers employe
    private String etat;            // En attente / En cours / Validée / Non validée
    private String priorite;        // Urgent / Quotidien / Informatif
    private boolean critique;
    private String pertinence;      // Pertinente / Non pertinente / À revoir
    private LocalDateTime created_at;

    public tache() {}

    public tache(int id_tache, int id_marche, String titre, String description,
                 LocalDate date_debut, LocalDate date_fin, int duree_estimee,
                 int responsable, String etat, String priorite,
                 boolean critique, String pertinence, LocalDateTime created_at) {
        this.id_tache = id_tache;
        this.id_marche = id_marche;
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.duree_estimee = duree_estimee;
        this.responsable = responsable;
        this.etat = etat;
        this.priorite = priorite;
        this.critique = critique;
        this.pertinence = pertinence;
        this.created_at = created_at;
    }

    // Getters et Setters
    public int getId_tache() { return id_tache; }
    public void setId_tache(int id_tache) { this.id_tache = id_tache; }

    public int getId_marche() { return id_marche; }
    public void setId_marche(int id_marche) { this.id_marche = id_marche; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate_debut() { return date_debut; }
    public void setDate_debut(LocalDate date_debut) { this.date_debut = date_debut; }

    public LocalDate getDate_fin() { return date_fin; }
    public void setDate_fin(LocalDate date_fin) { this.date_fin = date_fin; }

    public int getDuree_estimee() { return duree_estimee; }
    public void setDuree_estimee(int duree_estimee) { this.duree_estimee = duree_estimee; }

    public int getResponsable() { return responsable; }
    public void setResponsable(int responsable) { this.responsable = responsable; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public boolean isCritique() { return critique; }
    public void setCritique(boolean critique) { this.critique = critique; }

    public String getPertinence() { return pertinence; }
    public void setPertinence(String pertinence) { this.pertinence = pertinence; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
