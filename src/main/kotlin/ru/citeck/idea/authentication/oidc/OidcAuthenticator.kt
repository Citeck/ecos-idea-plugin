package ru.citeck.idea.authentication.oidc

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import ecos.com.fasterxml.jackson210.annotation.JsonProperty
import org.apache.commons.lang3.StringUtils
import ru.citeck.ecos.commons.promise.Promises
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.authentication.Authenticator
import ru.citeck.idea.authentication.ServerEisConfig
import ru.citeck.idea.authentication.oidc.callback.OidcCallbackData
import ru.citeck.idea.authentication.oidc.dialog.OidcCallbackWaitingDialog
import ru.citeck.idea.http.CiteckHttpClient
import ru.citeck.idea.settings.CiteckSettingsService.Companion.getInstance
import ru.citeck.idea.settings.servers.CiteckServer
import java.awt.Frame
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class OidcAuthenticator(
    private val citeckServer: CiteckServer,
    private val serverEisConfig: ServerEisConfig
) : Authenticator {

    companion object {
        private val AUTH_LOCK_TIMEOUT: Duration = Duration.ofSeconds(10)

        private val log = Logger.getInstance(OidcAuthenticator::class.java)
    }

    private val httpClient = CiteckHttpClient()
    private val authLock = ReentrantLock()

    @Volatile
    private var oidcParams: OidcAuthParams? = null

    @Volatile
    private var oidcServerInfo: OidcServerInfo? = null

    @Volatile
    private var tokens: OidcTokens? = null

    override fun getAuthHeader(project: Project, reset: Boolean): Promise<String> {
        return getAccessToken(project, reset).then { it: String -> "Bearer $it" }
    }

    private fun getAccessToken(project: Project, resetTokens: Boolean): Promise<String> {
        try {
            if (!authLock.tryLock(AUTH_LOCK_TIMEOUT.seconds, TimeUnit.SECONDS)) {
                return Promises.reject(
                    RuntimeException("Auth lock can't be acquired in ${AUTH_LOCK_TIMEOUT.seconds} seconds")
                )
            }
        } catch (e: InterruptedException) {
            return Promises.reject(RuntimeException("Auth lock acquisition interrupted"))
        }
        try {
            return getAccessTokenImpl(project, resetTokens)
        } finally {
            authLock.unlock()
        }
    }

    private fun getAccessTokenImpl(project: Project, resetTokens: Boolean): Promise<String> {
        val currentTokens = tokens
        if (currentTokens == null ||
            resetTokens ||
            currentTokens.refreshTokenValidUntilMs < System.currentTimeMillis()
        ) {
            val callbackPort = getInstance().getOidcCallbackPort()

            log.info("Begin oidc authentication for server ${citeckServer.host}. Callback port: $callbackPort")

            val oidcParams = OidcAuthParams(
                redirectUri = "http://127.0.0.1:$callbackPort/callback",
                clientId = citeckServer.clientId.ifBlank { "citeck-idea-plugin" },
                callbackPort = callbackPort,
                clientSecret = citeckServer.getClientSecret()
            )
            this.oidcParams = oidcParams

            return getOidcServerInfo().thenPromise { serverInfo: OidcServerInfo ->
                OidcCallbackWaitingDialog.show(project, oidcParams, serverInfo)
                    .thenPromise { callbackData: OidcCallbackData ->
                        val frame = WindowManager.getInstance().getFrame(project)
                        if (frame != null) {
                            frame.state = Frame.NORMAL
                            frame.toFront()
                            frame.requestFocus()
                        }
                        getInitialTokens(oidcParams, serverInfo, callbackData).then {
                            tokens = it
                            it.accessToken
                        }
                    }
            }
        }

        if (currentTokens.accessTokenValidUntilMs > System.currentTimeMillis()) {
            return Promises.resolve(currentTokens.accessToken)
        }

        val getTokensParams: MutableMap<String, String> = LinkedHashMap()
        getTokensParams["grant_type"] = "refresh_token"
        getTokensParams["client_id"] = oidcParams!!.clientId
        getTokensParams["refresh_token"] = tokens!!.refreshToken
        if (StringUtils.isNotBlank(oidcParams!!.clientSecret)) {
            getTokensParams["client_secret"] = oidcParams!!.clientSecret
        }

        val now = System.currentTimeMillis()

        return getOidcServerInfo()
            .thenPromise { info: OidcServerInfo ->
                httpClient.newRequest()
                    .uri(info.tokenEndpoint)
                    .urlEncodedBody(getTokensParams)
                    .postForJson(OidcTokensResponse::class.java)
                    .continueInCurrentThread()
            }.then { tokensResp: OidcTokensResponse ->
                this.tokens = OidcTokens(
                    tokensResp.accessToken,
                    now + (tokensResp.expiresIn - 1) * 1000L,
                    tokensResp.refreshToken,
                    now + (tokensResp.refreshExpiresIn - 1) * 1000L
                )
                tokensResp.accessToken
            }.catch { getAccessTokenImpl(project, true).get() }
    }

    private fun getOidcServerInfo(): Promise<OidcServerInfo> {

        val currentInfo = oidcServerInfo

        if (currentInfo != null) {
            return Promises.resolve(currentInfo)
        }

        val eisUrl = StringBuilder()
        val eisId = resolveLocalEisId(serverEisConfig.eisId)
        if (!eisId.startsWith("http")) {
            if (eisId.startsWith("localhost") || eisId.startsWith("127.0.0.1")) {
                eisUrl.append("http")
            } else {
                eisUrl.append("https")
            }
            eisUrl.append("://")
        }
        eisUrl.append(eisId)
            .append("/auth/realms/")
            .append(serverEisConfig.realmId)
            .append("/.well-known/openid-configuration")

        return httpClient.newRequest()
            .uri(eisUrl.toString())
            .getForJson(OidcServerInfo::class.java)
            .continueInCurrentThread()
            .then { info: OidcServerInfo ->
                val resolvedInfo = resolveLocalOidcServerInfo(info)
                oidcServerInfo = resolvedInfo
                resolvedInfo
            }
    }

    private fun isLocalServer(): Boolean {
        val hostname = citeckServer.host
            .substringAfter("://")
            .substringBefore(":")
            .substringBefore("/")
        return hostname == "localhost" || hostname == "127.0.0.1"
    }

    private fun resolveLocalEisId(eisId: String): String {
        if (!isLocalServer()) return eisId
        // For local server, Keycloak is accessible through the gateway at /ecos-idp/
        val serverBase = citeckServer.host.substringAfter("://").trimEnd('/')
        return "$serverBase/ecos-idp"
    }

    private fun resolveLocalUrl(url: String): String {
        if (!isLocalServer() || !url.contains("://")) return url
        val protocol = url.substringBefore("://")
        val withoutProtocol = url.substringAfter("://")
        val path = withoutProtocol.substringAfter("/", "")
        if (path.isEmpty()) return url
        val serverBase = citeckServer.host.substringAfter("://").trimEnd('/')
        // Normalize all Keycloak endpoints to /ecos-idp/auth/realms/ format,
        // since ECOS Gateway only exposes the legacy /auth/ path publicly
        val normalizedPath = when {
            path.startsWith("ecos-idp/auth/") -> path
            path.startsWith("ecos-idp/realms/") -> "ecos-idp/auth/" + path.removePrefix("ecos-idp/")
            path.startsWith("ecos-idp/") -> path
            path.startsWith("auth/") -> "ecos-idp/$path"
            path.startsWith("realms/") -> "ecos-idp/auth/$path"
            else -> "ecos-idp/auth/$path"
        }
        return "$protocol://$serverBase/$normalizedPath"
    }

    private fun resolveLocalOidcServerInfo(info: OidcServerInfo): OidcServerInfo {
        if (!isLocalServer()) return info
        return info.copy(
            authEndpoint = resolveLocalUrl(info.authEndpoint),
            tokenEndpoint = resolveLocalUrl(info.tokenEndpoint),
            endSessionEndpoint = resolveLocalUrl(info.endSessionEndpoint),
            userInfoEndpoint = resolveLocalUrl(info.userInfoEndpoint),
            introspectionEndpoint = resolveLocalUrl(info.introspectionEndpoint)
        )
    }

    private fun getInitialTokens(
        params: OidcAuthParams,
        serverInfo: OidcServerInfo,
        callbackData: OidcCallbackData
    ): Promise<OidcTokens> {

        val getTokensParams: MutableMap<String, String> = LinkedHashMap()
        getTokensParams["grant_type"] = "authorization_code"
        getTokensParams["client_id"] = params.clientId
        getTokensParams["code"] = callbackData.code
        getTokensParams["redirect_uri"] = params.redirectUri
        if (StringUtils.isNotBlank(params.clientSecret)) {
            getTokensParams["client_secret"] = params.clientSecret
        }

        val now = System.currentTimeMillis()

        return httpClient.newRequest()
            .uri(serverInfo.tokenEndpoint)
            .urlEncodedBody(getTokensParams)
            .postForJson(OidcTokensResponse::class.java)
            .continueInCurrentThread()
            .then { tokensResp: OidcTokensResponse ->
                OidcTokens(
                    tokensResp.accessToken,
                    now + (tokensResp.expiresIn - 1) * 1000L,
                    tokensResp.refreshToken,
                    now + (tokensResp.refreshExpiresIn - 1) * 1000L
                )
            }
    }

    private fun <T> Promise<T>.continueInCurrentThread(): Promise<T> {
        return try {
            Promises.resolve(this.get())
        } catch (e: Throwable) {
            Promises.reject(e)
        }
    }

    private class OidcTokens(
        val accessToken: String,
        val accessTokenValidUntilMs: Long,
        val refreshToken: String,
        val refreshTokenValidUntilMs: Long
    )

    private class OidcTokensResponse(
        @JsonProperty("access_token")
        val accessToken: String,

        @JsonProperty("expires_in")
        val expiresIn: Int,

        @JsonProperty("refresh_token")
        val refreshToken: String,

        @JsonProperty("refresh_expires_in")
        val refreshExpiresIn: Int
    )
}
