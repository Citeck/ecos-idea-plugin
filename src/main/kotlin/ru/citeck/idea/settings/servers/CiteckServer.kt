package ru.citeck.idea.settings.servers

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import ecos.com.fasterxml.jackson210.annotation.JsonIgnore
import ecos.com.fasterxml.jackson210.databind.annotation.JsonDeserialize
import java.util.*

@JsonDeserialize(builder = CiteckServer.Builder::class)
class CiteckServer(
    val id: String,
    val host: String,
    val name: String,
    val clientId: String
) : Cloneable {

    private val clientSecretCredentialsAttribute by lazy {
        CredentialAttributes("citeck-$id", "oauth2-client-secret")
    }

    @JsonIgnore
    fun getClientSecret(): String {
        return PasswordSafe.instance.getPassword(clientSecretCredentialsAttribute) ?: ""
    }

    fun copy(): Builder {
        return Builder(this)
    }

    class Builder() {

        companion object {
            const val DEFAULT_NON_EMPTY_PASSWORD = "******"
        }

        var id: String = UUID.randomUUID().toString()
        var host: String = ""
        var name: String = ""
        var clientId: String = ""
        var clientSecret: String = ""

        private var clientSecretChanged = false

        constructor(base: CiteckServer): this() {
            this.id = base.id
            this.host = base.host
            this.name = base.name
            this.clientId = base.clientId
            if (base.getClientSecret().isNotBlank()) {
                 clientSecret = DEFAULT_NON_EMPTY_PASSWORD
            }
        }

        fun withId(id: String): Builder {
            this.id = id
            return this
        }

        fun withName(name: String): Builder {
            this.name = name
            return this
        }

        fun withHost(host: String): Builder {
            this.host = host
            return this
        }

        fun withClientId(clientId: String): Builder {
            this.clientId = clientId
            return this
        }

        fun withClientSecret(clientSecret: String): Builder {
            if (this.clientSecret == clientSecret) {
                return this
            }
            clientSecretChanged = true
            this.clientSecret = clientSecret
            return this
        }

        fun build(): CiteckServer {
            var fixedHost = host

            if (fixedHost.endsWith("/")) {
                fixedHost = fixedHost.substring(0, fixedHost.length - 1)
            }
            if (!fixedHost.contains("://")) {
                val hostWithoutPort = fixedHost.substringBefore(":")
                val protocol = if (hostWithoutPort == "localhost" || hostWithoutPort == "127.0.0.1") {
                    "http"
                } else {
                    "https"
                }
                fixedHost = "$protocol://$fixedHost"
            }
            val fixedName = name.ifBlank {
                fixedHost.substringAfter("://")
            }
            val server = CiteckServer(
                id = id,
                host = fixedHost,
                name = fixedName,
                clientId = clientId
            )
            if (clientSecretChanged) {
                PasswordSafe.instance.setPassword(server.clientSecretCredentialsAttribute, clientSecret)
            }
            return server
        }
    }
}
