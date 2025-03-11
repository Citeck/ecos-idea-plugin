package ru.citeck.idea.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Attribute
import ru.citeck.ecos.commons.json.Json
import ru.citeck.idea.settings.servers.CiteckServer

@Service
@State(name = "Citeck Settings", storages = [Storage(value = "ecosSettings.xml")])
class CiteckSettingsService : PersistentStateComponent<CiteckSettingsService.SettingsState> {

    companion object {
        const val DEFAULT_OIDC_CALLBACK_PORT = 23312

        @JvmStatic
        fun getInstance(): CiteckSettingsService {
            return ApplicationManager.getApplication().getService(CiteckSettingsService::class.java)
        }
    }

    @Volatile
    private var state: SettingsState = SettingsState()
    @Volatile
    private var parsedServers: List<CiteckServer> = emptyList()

    @Synchronized
    fun getServers(): List<CiteckServer> {
        return parsedServers
    }

    fun setServers(servers: List<CiteckServer>) {
        state.servers = Json.mapper.toStringNotNull(servers)
        this.parsedServers = servers
    }

    fun getOidcCallbackPort(): Int {
        return state.oidcCallbackPort ?: DEFAULT_OIDC_CALLBACK_PORT
    }

    fun setOidcCallbackPort(port: Int) {
        state.oidcCallbackPort = port
    }

    override fun getState(): SettingsState {
        return state
    }

    override fun loadState(state: SettingsState) {
        this.state = state
        if (!state.servers.isNullOrBlank()) {
            this.parsedServers = Json.mapper.readList(state.servers, CiteckServer::class.java)
        }
    }

    data class SettingsState(
        @Attribute("servers")
        var servers: String? = null,
        @Attribute("oidcCallbackPort")
        var oidcCallbackPort: Int? = null
    )
}
