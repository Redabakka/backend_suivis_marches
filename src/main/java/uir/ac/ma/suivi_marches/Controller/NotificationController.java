package uir.ac.ma.suivi_marches.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uir.ac.ma.suivi_marches.Service.NotificationService;
import uir.ac.ma.suivi_marches.Service.EmployeService;
import uir.ac.ma.suivi_marches.model.Notification;
import uir.ac.ma.suivi_marches.model.Employe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {

    private final NotificationService notificationService;
    private final EmployeService employeService;

    public NotificationController(NotificationService notificationService,
                                  EmployeService employeService) {
        this.notificationService = notificationService;
        this.employeService = employeService;
    }

    // 🔹 Récupérer toutes les notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // 🔹 Récupérer une notification par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable("id") int idNotification) {
        Optional<Notification> notification = notificationService.getNotificationById(idNotification);

        if (notification.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Notification introuvable"));
        }

        return ResponseEntity.ok(notification.get());
    }

    // 🔹 Ajouter une nouvelle notification
    @PostMapping
    public ResponseEntity<?> addNotification(@RequestBody Map<String, Object> request) {
        try {
            // Récupération des données
            int idEmploye = Integer.parseInt(request.get("idEmploye").toString());
            String typeStr = request.get("type").toString();
            String message = request.get("message").toString();
            boolean lu = request.containsKey("lu") && Boolean.parseBoolean(request.get("lu").toString());

            // Validations
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le message est obligatoire"));
            }
            if (message.length() > 1000) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le message ne doit pas dépasser 1000 caractères"));
            }

            // Vérifier que l'employé existe
            Employe employe = employeService.getEmployeById(idEmploye)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable: " + idEmploye));

            // Mapper le type
            Notification.Type type;
            try {
                type = Notification.Type.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: INFO, AVERTISSEMENT, TACHE, APPROBATION, AUTRE)"));
            }

            // Créer la notification
            Notification notification = new Notification();
            notification.setEmploye(employe);
            notification.setType(type);
            notification.setMessage(message.trim());
            notification.setLu(lu);
            notification.setDate_envoi(LocalDateTime.now());

            Notification savedNotification = notificationService.addNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "message", "Notification créée avec succès",
                    "id_notification", savedNotification.getId_notification()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la création: " + e.getMessage()
            ));
        }
    }

    // 🔹 Modifier une notification existante
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyNotification(@PathVariable("id") int idNotification,
                                                @RequestBody Map<String, Object> request) {
        Optional<Notification> existingNotification = notificationService.getNotificationById(idNotification);

        if (existingNotification.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Notification introuvable"));
        }

        try {
            Notification notification = existingNotification.get();

            // Mettre à jour les champs si présents
            if (request.containsKey("type")) {
                String typeStr = request.get("type").toString();
                try {
                    Notification.Type type = Notification.Type.valueOf(typeStr.toUpperCase());
                    notification.setType(type);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: INFO, AVERTISSEMENT, TACHE, APPROBATION, AUTRE)"));
                }
            }

            if (request.containsKey("message")) {
                String message = request.get("message").toString();
                if (message == null || message.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le message ne peut pas être vide"));
                }
                if (message.length() > 1000) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le message ne doit pas dépasser 1000 caractères"));
                }
                notification.setMessage(message.trim());
            }

            if (request.containsKey("lu")) {
                boolean lu = Boolean.parseBoolean(request.get("lu").toString());
                notification.setLu(lu);
            }

            Notification updatedNotification = notificationService.modifyNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "message", "Notification modifiée avec succès",
                    "notification", updatedNotification
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la modification: " + e.getMessage()
            ));
        }
    }

    // 🔹 Marquer une notification comme lue
    @PatchMapping("/{id}/marquer-lu")
    public ResponseEntity<?> markAsRead(@PathVariable("id") int idNotification) {
        Optional<Notification> existingNotification = notificationService.getNotificationById(idNotification);

        if (existingNotification.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Notification introuvable"));
        }

        try {
            Notification notification = existingNotification.get();
            notification.setLu(true);
            notificationService.modifyNotification(notification);

            return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la mise à jour: " + e.getMessage()
            ));
        }
    }

    // 🔹 Marquer une notification comme non lue
    @PatchMapping("/{id}/marquer-non-lu")
    public ResponseEntity<?> markAsUnread(@PathVariable("id") int idNotification) {
        Optional<Notification> existingNotification = notificationService.getNotificationById(idNotification);

        if (existingNotification.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Notification introuvable"));
        }

        try {
            Notification notification = existingNotification.get();
            notification.setLu(false);
            notificationService.modifyNotification(notification);

            return ResponseEntity.ok(Map.of("message", "Notification marquée comme non lue"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la mise à jour: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer une notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") int idNotification) {
        Optional<Notification> notification = notificationService.getNotificationById(idNotification);

        if (notification.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Notification introuvable"));
        }

        try {
            notificationService.deleteNotification(idNotification);
            return ResponseEntity.ok(Map.of("message", "Notification supprimée avec succès"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer les notifications par employé
    @GetMapping("/employe/{idEmploye}")
    public ResponseEntity<?> getNotificationsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Notification> notifications = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getEmploye().getId_employe().equals(idEmploye))
                .toList();

        return ResponseEntity.ok(notifications);
    }

    // 🔹 Récupérer les notifications non lues par employé
    @GetMapping("/employe/{idEmploye}/non-lues")
    public ResponseEntity<?> getUnreadNotificationsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Notification> notifications = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) && !n.isLu())
                .toList();

        return ResponseEntity.ok(notifications);
    }

    // 🔹 Récupérer les notifications lues par employé
    @GetMapping("/employe/{idEmploye}/lues")
    public ResponseEntity<?> getReadNotificationsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        List<Notification> notifications = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) && n.isLu())
                .toList();

        return ResponseEntity.ok(notifications);
    }

    // 🔹 Récupérer les notifications par type
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getNotificationsByType(@PathVariable("type") String typeStr) {
        try {
            Notification.Type type = Notification.Type.valueOf(typeStr.toUpperCase());

            List<Notification> notifications = notificationService.getAllNotifications()
                    .stream()
                    .filter(n -> n.getType() == type)
                    .toList();

            return ResponseEntity.ok(notifications);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Type invalide (attendu: INFO, AVERTISSEMENT, TACHE, APPROBATION, AUTRE)"));
        }
    }

    // 🔹 Compter les notifications non lues par employé
    @GetMapping("/employe/{idEmploye}/non-lues/count")
    public ResponseEntity<?> countUnreadNotifications(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        long count = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) && !n.isLu())
                .count();

        return ResponseEntity.ok(Map.of(
                "id_employe", idEmploye,
                "non_lues", count
        ));
    }

    // 🔹 Marquer toutes les notifications comme lues pour un employé
    @PatchMapping("/employe/{idEmploye}/marquer-toutes-lues")
    public ResponseEntity<?> markAllAsReadForEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            List<Notification> unreadNotifications = notificationService.getAllNotifications()
                    .stream()
                    .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) && !n.isLu())
                    .toList();

            for (Notification notification : unreadNotifications) {
                notification.setLu(true);
                notificationService.modifyNotification(notification);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Toutes les notifications marquées comme lues",
                    "count", unreadNotifications.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la mise à jour: " + e.getMessage()
            ));
        }
    }

    // 🔹 Supprimer toutes les notifications lues pour un employé
    @DeleteMapping("/employe/{idEmploye}/supprimer-lues")
    public ResponseEntity<?> deleteReadNotificationsForEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        try {
            List<Notification> readNotifications = notificationService.getAllNotifications()
                    .stream()
                    .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) && n.isLu())
                    .toList();

            for (Notification notification : readNotifications) {
                notificationService.deleteNotification(notification.getId_notification());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Notifications lues supprimées avec succès",
                    "count", readNotifications.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }

    // 🔹 Récupérer les notifications récentes (dernières 7 jours)
    @GetMapping("/recentes")
    public ResponseEntity<List<Notification>> getRecentNotifications() {
        LocalDateTime derniersSeptJours = LocalDateTime.now().minusDays(7);

        List<Notification> notifications = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getDate_envoi().isAfter(derniersSeptJours))
                .toList();

        return ResponseEntity.ok(notifications);
    }

    // 🔹 Récupérer les notifications récentes par employé (dernières 7 jours)
    @GetMapping("/employe/{idEmploye}/recentes")
    public ResponseEntity<?> getRecentNotificationsByEmploye(@PathVariable("idEmploye") int idEmploye) {
        // Vérifier que l'employé existe
        if (employeService.getEmployeById(idEmploye).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Employé introuvable"));
        }

        LocalDateTime derniersSeptJours = LocalDateTime.now().minusDays(7);

        List<Notification> notifications = notificationService.getAllNotifications()
                .stream()
                .filter(n -> n.getEmploye().getId_employe().equals(idEmploye) &&
                        n.getDate_envoi().isAfter(derniersSeptJours))
                .toList();

        return ResponseEntity.ok(notifications);
    }
}