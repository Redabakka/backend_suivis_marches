package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "approbation")
public class Approbation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_approbation")
    private Integer id_approbation;

    // --- Relations vers Marche et Employe ---
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_marche", nullable = false, foreignKey = @ForeignKey(name = "fk_approbation_marche"))
    private Marche marche;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false, foreignKey = @ForeignKey(name = "fk_approbation_employe"))
    private Employe employe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private Statut statut; // Approuvé / Refusé

    @Column(name = "motif", length = 1000)
    private String motif;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public enum Statut {
        APPROUVE, REFUSE
    }

    public Approbation() {
    }

    public Approbation(Integer id_approbation, Marche marche, Employe employe,
                       Statut statut, String motif, LocalDateTime created_at) {
        this.id_approbation = id_approbation;
        this.marche = marche;
        this.employe = employe;
        this.statut = statut;
        this.motif = motif;
        this.created_at = created_at;
    }

    // --- Getters & Setters ---
    public Integer getId_approbation() {
        return id_approbation;
    }

    public void setId_approbation(Integer id_approbation) {
        this.id_approbation = id_approbation;
    }

    public Marche getMarche() {
        return marche;
    }

    public void setMarche(Marche marche) {
        this.marche = marche;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
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
        if (!(o instanceof Approbation a)) return false;
        return id_approbation != null && id_approbation.equals(a.id_approbation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_approbation);
    }
}
