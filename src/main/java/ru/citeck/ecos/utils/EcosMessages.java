package ru.citeck.ecos.utils;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.util.ui.ConfirmationDialog;
import icons.Icons;

import javax.swing.*;

public class EcosMessages {

    public static boolean confirm(String title, String message, Project project) {
        return new ConfirmationDialog(
                project,
                message,
                title,
                Icons.CiteckLogo,
                VcsShowConfirmationOption.STATIC_SHOW_CONFIRMATION
        ).showAndGet();
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
        Notification notification = new Notification("ECOS", title, content == null ? "" : content, type);
        if (icon != null) {
            notification.setIcon(icon);
        }
        Notifications.Bus.notify(notification, project);
    }

}
