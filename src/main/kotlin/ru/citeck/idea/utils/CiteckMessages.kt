package ru.citeck.idea.utils

import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.*
import com.intellij.openapi.vcs.VcsShowConfirmationOption
import com.intellij.util.ui.ConfirmationDialog
import icons.Icons
import javax.swing.Icon

object CiteckMessages {

    fun confirm(
        title: @DialogTitle String?,
        message: @DialogMessage String?,
        project: Project?
    ): Boolean {
        return ConfirmationDialog(
            project,
            message,
            title,
            Icons.CiteckLogo,
            VcsShowConfirmationOption.STATIC_SHOW_CONFIRMATION
        ).showAndGet()
    }

    fun info(
        title: @NotificationTitle String,
        content: @NotificationContent String?,
        project: Project?
    ) {
        notify(title, content, project, NotificationType.INFORMATION, AllIcons.General.Information)
    }

    fun error(
        title: @NotificationTitle String,
        content: @NotificationContent String?,
        project: Project?
    ) {
        notify(title, content, project, NotificationType.ERROR, AllIcons.General.Error)
    }

    fun warning(
        title: @NotificationTitle String,
        content: @NotificationContent String?,
        project: Project?
    ) {
        notify(title, content, project, NotificationType.WARNING, AllIcons.General.Warning)
    }

    private fun notify(
        title: @NotificationTitle String,
        content: @NotificationContent String?,
        project: Project?,
        type: NotificationType,
        icon: Icon?
    ) {
        val notification = Notification("Citeck", title, content ?: "", type)
        if (icon != null) {
            notification.setIcon(icon)
        }
        ApplicationManager.getApplication().invokeLater {
            Notifications.Bus.notify(
                notification,
                project
            )
        }
    }
}
