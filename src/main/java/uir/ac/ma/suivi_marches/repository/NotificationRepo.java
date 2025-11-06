package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Notification;

import java.util.List;

public interface NotificationRepo {
    List<Notification> getAllNotification();

    Notification getNotificationById(int idNotification);

    void addNotification(Notification notification);

    void modifyNotification(Notification notification);

    void deleteNotification(int idNotification);
}