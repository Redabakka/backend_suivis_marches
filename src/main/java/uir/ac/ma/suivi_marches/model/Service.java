package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Integer id_service;

    @Column(name = "nom", nullable = false, unique = true, length = 150)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    public Service() {
    }

    public Service(Integer id_service, String nom, String description, boolean actif) {
        this.id_service = id_service;
        this.nom = nom;
        this.description = description;
        this.actif = actif;
    }

    // --- Getters & Setters ---
    public Integer getId_service() {
        return id_service;
    }

    public void setId_service(Integer id_service) {
        this.id_service = id_service;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service s)) return false;
        return id_service != null && id_service.equals(s.id_service);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_service);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id_service=" + id_service +
                ", nom='" + nom + '\'' +
                ", actif=" + actif +
                '}';
    }
}
