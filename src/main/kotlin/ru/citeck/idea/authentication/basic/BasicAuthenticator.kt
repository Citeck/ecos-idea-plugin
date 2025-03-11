package ru.citeck.idea.authentication.basic

import com.intellij.openapi.project.Project
import ru.citeck.ecos.commons.promise.Promises.resolve
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.authentication.Authenticator
import ru.citeck.idea.exceptions.GracefulAbortException
import ru.citeck.idea.settings.servers.CiteckServer
import java.util.*

class BasicAuthenticator : Authenticator {

    private var username: String = ""
    private var password: String = ""

    override fun getAuthHeader(project: Project, reset: Boolean): Promise<String> {
        updatePassword(project, reset)
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encode(auth.toByteArray())
        return resolve("Basic " + String(encodedAuth))
    }

    private fun updatePassword(project: Project, updateCredentials: Boolean) {
        if (username.isBlank() || password.isBlank() || updateCredentials) {
            val passwordInputDialog = PasswordInputDialog(project)
            passwordInputDialog.title = "Please Provide Password:"
            if (passwordInputDialog.showAndGet()) {
                username = passwordInputDialog.getUserName()
                password = passwordInputDialog.getPassword()
            } else {
                throw GracefulAbortException("Cancelled")
            }
        }
    }
}
