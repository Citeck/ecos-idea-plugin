package ru.citeck.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import icons.EcosIcons

class EcosNotification {
    companion object {

        fun Information(title: String, content: String, project: Project) {
            notify(title, content, project, NotificationType.INFORMATION)
        }

        fun Error(title: String, content: String, project: Project) {
            notify(title, content, project, NotificationType.ERROR)
        }

        fun Warning(title: String, content: String, project: Project) {
            notify(title, content, project, NotificationType.WARNING)
        }

        private fun notify(title: String, content: String, project: Project, notificationType: NotificationType) {
            val notification = Notification("Ecos", title, content, notificationType)
            notification.icon = EcosIcons.CiteckLogo
            Notifications.Bus.notify(notification, project)
        }


    }
}