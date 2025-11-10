package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "employe")
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employe")
    private Integer id_employe;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;  // ADMIN | CHEF | EMPLOYE

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_service", nullable = false, foreignKey = @ForeignKey(name = "fk_employe_service"))
    private Service service;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    public enum Role {
        ADMIN, CHEF, EMPLOYE
    }

    public Employe() {
    }

    public Employe(Integer id_employe, String nom, String prenom, String email, Role role,
                   Service service, boolean actif, LocalDateTime created_at) {
        this.id_employe = id_employe;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.service = service;
        this.actif = actif;
        this.created_at = created_at;
    }

    // --- Getters & Setters ---
    public Integer getId_employe() {
        return id_employe;
    }

    public void setId_employe(Integer id_employe) {
        this.id_employe = id_employe;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
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
        if (!(o instanceof Employe e)) return false;
        return id_employe != null && id_employe.equals(e.id_employe);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_employe);
    }
}
