package uir.ac.ma.suivi_marches.Model;
import java.time.LocalDateTime;

public class notification {
    private int id_notification;
    private int id_employe;      // clé étrangère vers employe
    private String type;         // type de notification
    private String message;      // contenu du message
    private boolean lu;          // statut de lecture
    private LocalDateTime date_envoi;

    public notification() {}

    public notification(int id_notification, int id_employe, String type,
                        String message, boolean lu, LocalDateTime date_envoi) {
        this.id_notification = id_notification;
        this.id_employe = id_employe;
        this.type = type;
        this.message = message;
        this.lu = lu;
        this.date_envoi = date_envoi;
    }

    // Getters et Setters
    public int getId_notification() { return id_notification; }
    public void setId_notification(int id_notification) { this.id_notification = id_notification; }

    public int getId_employe() { return id_employe; }
    public void setId_employe(int id_employe) { this.id_employe = id_employe; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public LocalDateTime getDate_envoi() { return date_envoi; }
    public void setDate_envoi(LocalDateTime date_envoi) { this.date_envoi = date_envoi; }

}
