package uir.ac.ma.suivi_marches.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Notification;
import uir.ac.ma.suivi_marches.repository.NotificationRepo;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepo.findAll();
    }

    public Optional<Notification> getNotificationById(int idNotification) {
        return notificationRepo.findById(idNotification);
    }

    @Transactional
    public Notification addNotification(Notification notification) {
        return notificationRepo.save(notification);
    }

    @Transactional
    public Notification modifyNotification(Notification notification) {
        return notificationRepo.save(notification);
    }

    @Transactional
    public void deleteNotification(int idNotification) {
        notificationRepo.deleteById(idNotification);
    }
}