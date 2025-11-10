package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "marche")
public class Marche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marche")
    private Integer id_marche;

    @Column(name = "intitule", nullable = false, length = 255)
    private String intitule;

    @Column(name = "objectif", columnDefinition = "TEXT")
    private String objectif;


    @Column(name = "budget_estime", precision = 19, scale = 2, nullable = false)
    private BigDecimal budget_estime;


    @Column(name = "date_debut")
    private LocalDate date_debut;

    @Column(name = "date_fin")
    private LocalDate date_fin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    private Statut statut;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_service", nullable = false, foreignKey = @ForeignKey(name = "fk_marche_service"))
    private Service service;

    @Column(name = "fichier_cps_path", length = 500)
    private String fichier_cps_path;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_marche_employe"))
    private Employe created_by;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public enum Statut {
        EN_PREPARATION,
        EN_COURS,
        TERMINE,
        ANNULE
    }

    public Marche() {
    }

    public Marche(Integer id_marche, String intitule, String objectif, BigDecimal budget_estime,
                  LocalDate date_debut, LocalDate date_fin, Statut statut,
                  Service service, String fichier_cps_path, Employe created_by,
                  LocalDateTime created_at) {
        this.id_marche = id_marche;
        this.intitule = intitule;
        this.objectif = objectif;
        this.budget_estime = budget_estime;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.statut = statut;
        this.service = service;
        this.fichier_cps_path = fichier_cps_path;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    // --- Getters & Setters ---
    public Integer getId_marche() {
        return id_marche;
    }

    public void setId_marche(Integer id_marche) {
        this.id_marche = id_marche;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public BigDecimal getBudget_estime() {
        return budget_estime;
    }

    public void setBudget_estime(BigDecimal budget_estime) {
        this.budget_estime = budget_estime;
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

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getFichier_cps_path() {
        return fichier_cps_path;
    }

    public void setFichier_cps_path(String fichier_cps_path) {
        this.fichier_cps_path = fichier_cps_path;
    }

    public Employe getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Employe created_by) {
        this.created_by = created_by;
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
        if (!(o instanceof Marche m)) return false;
        return id_marche != null && id_marche.equals(m.id_marche);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_marche);
    }
}
