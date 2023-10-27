package ru.citeck.ecos.utils;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class EcosMessages {

    public static boolean confirm(String title, String message) {
        return (JOptionPane.YES_OPTION ==
            JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION)
        );
    }

    public static void info(String title, String content, Project project) {
        notify(title, content, project, NotificationType.INFORMATION, AllIcons.General.Information);
    }

    public static void error(String title, String content, Project project) {
        notify(title, content, project, NotificationType.ERROR, AllIcons.General.Error);
    }

    public static void warning(String title, String content, Project project) {
        notify(title, content, project, NotificationType.WARNING, AllIcons.General.Warning);
    }

    private static void notify(String title, String content, Project project, NotificationType type, Icon icon) {
        Notification notification = new Notification("Ecos", title, content == null ? "" : content, type);
        if (icon != null) {
            notification.setIcon(icon);
        }
        Notifications.Bus.notify(notification, project);
    }

}
