package ru.citeck.idea.utils;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vcs.VcsShowConfirmationOption;
import com.intellij.util.ui.ConfirmationDialog;
import icons.Icons;

import javax.swing.*;

public class EcosMessages {

    public static boolean confirm(
        @NlsContexts.DialogTitle String title,
        @NlsContexts.DialogMessage String message,
        Project project
    ) {
        return new ConfirmationDialog(
            project,
            message,
            title,
            Icons.CiteckLogo,
            VcsShowConfirmationOption.STATIC_SHOW_CONFIRMATION
        ).showAndGet();
    }

    public static void info(
        @NlsContexts.NotificationTitle String title,
        @NlsContexts.NotificationContent String content,
        Project project
    ) {
        notify(title, content, project, NotificationType.INFORMATION, AllIcons.General.Information);
    }

    public static void error(
        @NlsContexts.NotificationTitle String title,
        @NlsContexts.NotificationContent String content,
        Project project
    ) {
        notify(title, content, project, NotificationType.ERROR, AllIcons.General.Error);
    }

    public static void warning(
        @NlsContexts.NotificationTitle String title,
        @NlsContexts.NotificationContent String content,
        Project project
    ) {
        notify(title, content, project, NotificationType.WARNING, AllIcons.General.Warning);
    }

    private static void notify(
        @NlsContexts.NotificationTitle String title,
        @NlsContexts.NotificationContent String content,
        Project project,
        NotificationType type,
        Icon icon
    ) {
        Notification notification = new Notification("Citeck", title, content == null ? "" : content, type);
        if (icon != null) {
            notification.setIcon(icon);
        }
        ApplicationManager.getApplication().invokeLater(() ->
            Notifications.Bus.notify(notification, project)
        );
    }
}
