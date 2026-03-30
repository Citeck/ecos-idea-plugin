package ru.citeck.idea.authentication

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import ru.citeck.idea.authentication.basic.BasicAuthenticator
import ru.citeck.idea.authentication.oidc.OidcAuthenticator
import ru.citeck.idea.http.CiteckHttpClient
import ru.citeck.idea.settings.servers.CiteckServer
import java.util.concurrent.ConcurrentHashMap

@Service
class AuthenticationService {

    companion object {
        private val log = Logger.getInstance(AuthenticationService::class.java)

        @JvmStatic
        fun getInstance(): AuthenticationService {
            return ApplicationManager.getApplication().getService(AuthenticationService::class.java)
        }
    }

    private val authenticators: MutableMap<CiteckServer, Authenticator> = ConcurrentHashMap()

    private val httpClient = CiteckHttpClient()

    fun getAuthenticator(citeckServer: CiteckServer): Authenticator {
        var authenticator = authenticators[citeckServer]
        if (authenticator == null) {
            val eisConfig = getServerEisConfig(citeckServer.host)
            authenticator = if (eisConfig == null || eisConfig.eisId == "EIS_ID") {
                BasicAuthenticator(citeckServer)
            } else {
                OidcAuthenticator(citeckServer, eisConfig)
            }
            authenticators[citeckServer] = authenticator
        }
        return authenticator
    }

    private fun getServerEisConfig(url: String): ServerEisConfig? {
        return try {
            httpClient.newRequest()
                .uri("$url/eis.json")
                .getForJson(ServerEisConfig::class.java)
                .get()
        } catch (e: Exception) {
            log.warn("Failed to load EIS config from $url/eis.json, falling back to BasicAuthenticator", e)
            null
        }
    }
}
