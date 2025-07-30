package ru.citeck.idea.records

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import lombok.Getter
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.commons.json.Json.mapper
import ru.citeck.ecos.commons.promise.Promises
import ru.citeck.ecos.webapp.api.entity.EntityRef
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.authentication.AuthenticationService
import ru.citeck.idea.authentication.Authenticator
import ru.citeck.idea.http.CiteckHttpClient
import ru.citeck.idea.http.UnauthorizedException
import ru.citeck.idea.settings.servers.CiteckServer
import ru.citeck.idea.settings.servers.CiteckServerSelector.getServer
import java.time.Duration

@Service(Service.Level.PROJECT)
class ServerRecordsApi(private val project: Project) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ServerRecordsApi {
            return project.getService(ServerRecordsApi::class.java)
        }
    }

    private val httpClient = CiteckHttpClient()

    fun isExists(ref: EntityRef): Promise<Boolean> {
        return loadAtt(ref, "_notExists?bool")
            .then { (it.isBoolean() && it.asBoolean()).not() }
    }

    fun mutate(ref: EntityRef, attributes: Any?) {
        val atts = RecordAtts(ref, ObjectData.create(attributes))
        val body = MutateRecordsBody()
        body.records = listOf(atts)
        exchangeRequest(RecordsEndpoint.MUTATE, body, MutateRecordsResp::class.java).get()
    }

    fun mutateAtt(ref: EntityRef, att: String, value: Any?) {
        val atts = RecordAtts(ref)
        atts.setAtt(att, value)
        val body = MutateRecordsBody()
        body.records = listOf(atts)
        exchangeRequest(RecordsEndpoint.MUTATE, body, MutateRecordsResp::class.java).get()
    }

    fun loadAtt(ref: EntityRef, att: String): Promise<DataValue> {
        return getRecordAtts(
            listOf(ref),
            mapOf(att to att)
        ).then { it[0].getAtt(att) }
    }

    fun loadAtts(ref: EntityRef, atts: Any): Promise<ObjectData> {
        return getRecordAtts(
            listOf(ref),
            atts
        ).then { it[0].attributes }
    }

    fun loadStrAtt(ref: EntityRef, att: String): Promise<String> {
        return loadAtt(ref, att).then { it.asText() }
    }

    private fun getRecordAtts(
        records: List<EntityRef>,
        attributes: Any
    ): Promise<List<RecordAtts>> {
        val body = GetRecordsAttsBody()
        body.records = records
        body.attributes = mapper.convert(
            attributes,
            mapper.getMapType(String::class.java, String::class.java)
        )
        return Promises.withTimeout(
            exchangeRequest(
                RecordsEndpoint.QUERY,
                body,
                GetRecordsAttsResp::class.java
            ).then { it.records },
            Duration.ofSeconds(15)
        )
    }

    private fun <T : Any> exchangeRequest(endpoint: RecordsEndpoint, body: Any, respType: Class<T>): Promise<T> {

        return getServer(project).thenPromise { server: CiteckServer ->

            val authenticator = AuthenticationService.getInstance().getAuthenticator(server)

            doWithAuthHeader(authenticator, false) { authHeader ->
                httpClient.newRequest()
                    .uri(server.host + endpoint.urlPath)
                    .jsonBody(body)
                    .authHeader(authHeader)
                    .postForJson(respType).get()
            }
        }
    }

    private fun <T : Any> doWithAuthHeader(
        authenticator: Authenticator,
        reset: Boolean,
        action: (String) -> T
    ): Promise<T> {
        return authenticator.getAuthHeader(project, reset).then { authHeader: String ->
            action(authHeader)
        }.catchPromise(UnauthorizedException::class.java) {
            doWithAuthHeader(authenticator, true, action)
        }
    }

    private enum class RecordsEndpoint(subPath: String) {
        QUERY("query"),
        MUTATE("mutate"),
        DELETE("delete");

        @Getter
        val urlPath: String = "/gateway/api/records/$subPath"
    }

    private class MutateRecordsBody {
        var records: List<RecordAtts>? = null
        val version = 1
    }

    private class MutateRecordsResp {
        val records: List<RecordAtts>? = null
    }

    private class GetRecordsAttsBody(
        var records: List<EntityRef>? = null,
        var attributes: Map<String, String>? = null,
        val version: Int = 1
    )

    private class GetRecordsAttsResp(
        val records: List<RecordAtts>
    )
}
