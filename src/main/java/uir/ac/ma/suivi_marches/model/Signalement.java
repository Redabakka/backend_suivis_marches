package uir.ac.ma.suivi_marches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "signalement")
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signalement")
    private Integer id_signalement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_tache",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_signalement_tache")
    )
    private Tache tache;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_employe",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_signalement_employe")
    )
    private Employe employe;

    // DOIT matcher exactement le CHECK Postgres : 'Validée' / 'Non pertinente'
    @Column(name = "type", nullable = false, length = 20)
    private String type; // "Validée" ou "Non pertinente"

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public Signalement() {
    }

    public Signalement(Integer id_signalement, Tache tache, Employe employe,
                       String type, String commentaire, LocalDateTime created_at) {
        this.id_signalement = id_signalement;
        this.tache = tache;
        this.employe = employe;
        this.type = type;
        this.commentaire = commentaire;
        this.created_at = created_at;
    }

    // --- Getters & Setters ---
    public Integer getId_signalement() {
        return id_signalement;
    }

    public void setId_signalement(Integer id_signalement) {
        this.id_signalement = id_signalement;
    }

    public Tache getTache() {
        return tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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
        if (!(o instanceof Signalement s)) return false;
        return id_signalement != null && id_signalement.equals(s.id_signalement);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_signalement);
    }
}
