package ru.citeck.idea.authentication.basic

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.project.Project
import ru.citeck.ecos.commons.promise.Promises.resolve
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.authentication.Authenticator
import ru.citeck.idea.exceptions.GracefulAbortException
import ru.citeck.idea.settings.servers.CiteckServer
import java.util.*

class BasicAuthenticator(private val server: CiteckServer) : Authenticator {

    private val credentialAttributes = CredentialAttributes("citeck-basic-${server.id}")

    private var username: String = ""
    private var password: String = ""

    override fun getAuthHeader(project: Project, reset: Boolean): Promise<String> {
        updatePassword(project, reset)
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encode(auth.toByteArray())
        return resolve("Basic " + String(encodedAuth))
    }

    private fun updatePassword(project: Project, updateCredentials: Boolean) {
        if (!updateCredentials && username.isBlank() && password.isBlank()) {
            loadFromPasswordSafe()
        }
        if (username.isBlank() || password.isBlank() || updateCredentials) {
            val title = if (updateCredentials) "Invalid credentials. Please try again:" else "Please Provide Password:"
            val passwordInputDialog = PasswordInputDialog(project, username)
            passwordInputDialog.title = title
            if (passwordInputDialog.showAndGet()) {
                username = passwordInputDialog.getUserName()
                password = passwordInputDialog.getPassword()
                saveToPasswordSafe()
            } else {
                throw GracefulAbortException("Cancelled")
            }
        }
    }

    private fun loadFromPasswordSafe() {
        try {
            val credentials = PasswordSafe.instance.get(credentialAttributes) ?: return
            username = credentials.userName ?: ""
            password = credentials.getPasswordAsString() ?: ""
        } catch (e: Exception) {
            // Ignore - will prompt via dialog
        }
    }

    private fun saveToPasswordSafe() {
        PasswordSafe.instance.set(credentialAttributes, Credentials(username, password))
    }
}
