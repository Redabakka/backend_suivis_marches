package uir.ac.ma.suivi_marches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tache")
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tache")
    private Integer id_tache;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_marche",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tache_marche")
    )
    private Marche marche;

    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut", nullable = false)
    private LocalDate date_debut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate date_fin;

    @Column(name = "duree_estimee")
    private Integer duree_estimee; // peut être null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "responsable",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tache_employe")
    )
    private Employe responsable;

    @Column(name = "etat", nullable = false, length = 30)
    private String etat;      // 'En attente' / 'En cours' / 'Validée' / 'Non validée'

    @Column(name = "priorite", nullable = false, length = 20)
    private String priorite;  // 'Urgent' / 'Quotidien' / 'Informatif'

    @Column(name = "critique", nullable = false)
    private boolean critique = false;

    @Column(name = "pertinence", length = 20)
    private String pertinence; // 'Pertinente' / 'Non pertinente' / 'À revoir' / null

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public Tache() {
    }

    public Tache(Integer id_tache,
                 Marche marche,
                 String titre,
                 String description,
                 LocalDate date_debut,
                 LocalDate date_fin,
                 Integer duree_estimee,
                 Employe responsable,
                 String etat,
                 String priorite,
                 boolean critique,
                 String pertinence,
                 LocalDateTime created_at) {
        this.id_tache = id_tache;
        this.marche = marche;
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

    public Integer getId_tache() {
        return id_tache;
    }

    public void setId_tache(Integer id_tache) {
        this.id_tache = id_tache;
    }

    public Marche getMarche() {
        return marche;
    }

    public void setMarche(Marche marche) {
        this.marche = marche;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDate getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
        this.date_fin = date_fin;
    }

    public Integer getDuree_estimee() {
        return duree_estimee;
    }

    public void setDuree_estimee(Integer duree_estimee) {
        this.duree_estimee = duree_estimee;
    }

    public Employe getResponsable() {
        return responsable;
    }

    public void setResponsable(Employe responsable) {
        this.responsable = responsable;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public boolean isCritique() {
        return critique;
    }

    public void setCritique(boolean critique) {
        this.critique = critique;
    }

    public String getPertinence() {
        return pertinence;
    }

    public void setPertinence(String pertinence) {
        this.pertinence = pertinence;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tache t)) return false;
        return id_tache != null && id_tache.equals(t.id_tache);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_tache);
    }
}
