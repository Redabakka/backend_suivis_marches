package uir.ac.ma.suivi_marches.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "utilisateur",
        uniqueConstraints = @UniqueConstraint(name = "uk_utilisateur_username", columnNames = "username"))
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id_user;

    // Relation optionnelle vers Employe (clé étrangère id_employe)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_employe", foreignKey = @ForeignKey(name = "fk_utilisateur_employe"))
    private Employe employe;  // <-- remplace l'ancien Integer id_employe

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    // Hash BCRYPT ~60 caractères
    @Column(name = "password_hash", nullable = false, length = 100)
    private String password_hash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "role_type")
    private Role role; // ADMIN | CHEF | EMPLOYE

    public enum Role {ADMIN, CHEF, EMPLOYE}

    public Utilisateur() {
    }

    public Utilisateur(Integer id_user, Employe employe, String username,
                       String password_hash, Role role) {
        this.id_user = id_user;
        this.employe = employe;
        this.username = username;
        this.password_hash = password_hash;
        this.role = role;
    }

    // Getters / Setters
    public Integer getId_user() {
        return id_user;
    }

    public void setId_user(Integer id_user) {
        this.id_user = id_user;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Utilisateur{id_user=" + id_user +
                ", id_employe=" + (employe != null ? employe.getId_employe() : null) +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur u)) return false;
        return id_user != null && id_user.equals(u.id_user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_user);
    }
}
