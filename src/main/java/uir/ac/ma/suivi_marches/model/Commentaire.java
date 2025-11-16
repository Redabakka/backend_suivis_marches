package uir.ac.ma.suivi_marches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "commentaire")
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentaire")
    private Integer id_commentaire;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_tache",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_commentaire_tache")
    )
    private Tache tache;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_auteur",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_commentaire_employe")
    )
    private Employe auteur;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", length = 20)
    private Priorite priorite; // peut être null en DB

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public enum Priorite {
        URGENT,
        QUOTIDIEN,
        INFORMATIF
    }


    public Commentaire() {
    }

    public Commentaire(Integer id_commentaire, Tache tache, Employe auteur,
                       String contenu, Priorite priorite, LocalDateTime created_at) {
        this.id_commentaire = id_commentaire;
        this.tache = tache;
        this.auteur = auteur;
        this.contenu = contenu;
        this.priorite = priorite;
        this.created_at = created_at;
    }

    // --- Getters & Setters ---
    public Integer getId_commentaire() {
        return id_commentaire;
    }

    public void setId_commentaire(Integer id_commentaire) {
        this.id_commentaire = id_commentaire;
    }

    public Tache getTache() {
        return tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }

    public Employe getAuteur() {
        return auteur;
    }

    public void setAuteur(Employe auteur) {
        this.auteur = auteur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
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
        if (!(o instanceof Commentaire c)) return false;
        return id_commentaire != null && id_commentaire.equals(c.id_commentaire);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_commentaire);
    }
}
