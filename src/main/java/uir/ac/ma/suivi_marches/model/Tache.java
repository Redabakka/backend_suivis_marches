package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tache")
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tache")
    private Integer id_tache;

    @Column(name = "id_marche", nullable = false)
    private Integer id_marche;

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut")
    private LocalDate date_debut;

    @Column(name = "date_fin")
    private LocalDate date_fin;

    @Column(name = "duree_estimee", nullable = false)
    private Integer duree_estimee;

    @Column(name = "responsable", nullable = false)
    private Integer responsable; // id employe

    @Column(name = "etat", nullable = false, length = 20)
    private String etat;

    @Column(name = "priorite", nullable = false, length = 20)
    private String priorite;

    @Column(name = "critique", nullable = false)
    private boolean critique = false;

    @Column(name = "pertinence", nullable = false, length = 20)
    private String pertinence;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public Tache() {}

    public Tache(Integer id_tache, Integer id_marche, String titre, String description,
                 LocalDate date_debut, LocalDate date_fin, Integer duree_estimee,
                 Integer responsable, String etat, String priorite,
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

    // Getters / Setters
    public Integer getId_tache() { return id_tache; }
    public void setId_tache(Integer id_tache) { this.id_tache = id_tache; }

    public Integer getId_marche() { return id_marche; }
    public void setId_marche(Integer id_marche) { this.id_marche = id_marche; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate_debut() { return date_debut; }
    public void setDate_debut(LocalDate date_debut) { this.date_debut = date_debut; }

    public LocalDate getDate_fin() { return date_fin; }
    public void setDate_fin(LocalDate date_fin) { this.date_fin = date_fin; }

    public Integer getDuree_estimee() { return duree_estimee; }
    public void setDuree_estimee(Integer duree_estimee) { this.duree_estimee = duree_estimee; }

    public Integer getResponsable() { return responsable; }
    public void setResponsable(Integer responsable) { this.responsable = responsable; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tache t)) return false;
        return id_tache != null && id_tache.equals(t.id_tache);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id_tache); }
}
