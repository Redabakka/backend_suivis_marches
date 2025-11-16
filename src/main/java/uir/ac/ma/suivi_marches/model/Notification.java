package uir.ac.ma.suivi_marches.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer id_notification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_employe",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_notification_employe")
    )
    private Employe employe;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private Type type;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "lu", nullable = false)
    private boolean lu = false;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime date_envoi = LocalDateTime.now();

    public enum Type {
        INFO,
        AVERTISSEMENT,
        TACHE,
        APPROBATION,
        AUTRE
    }
    public Notification() {
    }

    public Notification(Integer id_notification, Employe employe, Type type,
                        String message, boolean lu, LocalDateTime date_envoi) {
        this.id_notification = id_notification;
        this.employe = employe;
        this.type = type;
        this.message = message;
        this.lu = lu;
        this.date_envoi = date_envoi;
    }

    // --- Getters & Setters ---
    public Integer getId_notification() {
        return id_notification;
    }

    public void setId_notification(Integer id_notification) {
        this.id_notification = id_notification;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDate_envoi() {
        return date_envoi;
    }

    public void setDate_envoi(LocalDateTime date_envoi) {
        this.date_envoi = date_envoi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification n)) return false;
        return id_notification != null && id_notification.equals(n.id_notification);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_notification);
    }
}
