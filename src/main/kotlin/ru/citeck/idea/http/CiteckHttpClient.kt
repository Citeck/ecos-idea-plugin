package ru.citeck.idea.http

import com.intellij.openapi.diagnostic.Logger
import ru.citeck.ecos.commons.json.Json
import ru.citeck.ecos.commons.promise.Promises
import ru.citeck.ecos.webapp.api.promise.Promise
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.stream.Collectors

class CiteckHttpClient {

    companion object {
        private val log = Logger.getInstance(CiteckHttpClient::class.java)
    }

    private val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    fun newRequest(): Request {
        return Request()
    }

    inner class Request {

        private val request: HttpRequest.Builder

        private var uri: URI? = null
        private var method: HttpMethod? = null
        private var bodyPublisher: BodyPublisher? = null

        constructor() {
            this.request = HttpRequest.newBuilder()
            request.timeout(Duration.ofSeconds(30))
        }

        private constructor(builder: HttpRequest.Builder) {
            this.request = builder
        }

        fun uri(uri: String): Request {
            this.uri = URI.create(uri)
            request.uri(this.uri)
            return this
        }

        fun authHeader(value: String?): Request {
            header("Authorization", value)
            return this
        }

        fun header(header: String?, value: String?): Request {
            request.header(header, value)
            return this
        }

        fun jsonBody(body: Any): Request {
            val strBody = if (body is String) {
                body
            } else {
                Json.mapper.toStringNotNull(body)
            }
            bodyPublisher = HttpRequest.BodyPublishers.ofString(strBody)
            request.header("Content-Type", "application/json")
            return this
        }

        fun urlEncodedBody(body: Map<String, String>): Request {
            val bodyStr = body.entries.stream()
                .map { entry: Map.Entry<String?, String?> ->
                    URLEncoder.encode(entry.key, StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.value, StandardCharsets.UTF_8)
                }
                .collect(Collectors.joining("&"))

            bodyPublisher = HttpRequest.BodyPublishers.ofString(bodyStr)
            request.header("Content-Type", "application/x-www-form-urlencoded")

            return this
        }

        fun <T : Any> getForJson(respType: Class<T>): Promise<T> {
            method = HttpMethod.GET
            request.GET()
            request.header("Accept", "application/json; charset=utf-8")
            return exchangeForText().then {
                Json.mapper.readNotNull(it, respType)
            }
        }

        fun postForText(): Promise<String> {
            method = HttpMethod.POST
            request.POST(bodyPublisher)
            return exchangeForText()
        }

        fun <T : Any> postForJson(respType: Class<T>): Promise<T> {
            method = HttpMethod.POST
            request.POST(bodyPublisher)
            request.header("Accept", "application/json; charset=utf-8")
            return exchangeForText().then {
               Json.mapper.readNotNull(it, respType)
            }
        }

        private fun exchangeForText(): Promise<String> {
            if (HttpMethod.GET == method && bodyPublisher != null) {
                return Promises.reject(RuntimeException("GET request can't be executed with body. URI: $uri"))
            }
            val promise = Promises.create(client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString()))
            return promise.then { response ->
                if (response.statusCode() != 200) {
                    log.debug(method.toString() + " " + uri + " request failed with status code " + response.statusCode())
                }

                if (response.statusCode() == 401) {
                    throw UnauthorizedException(uri.toString())
                } else if (response.statusCode() != 200) {
                    throw HttpRequestFailedException(
                        uri.toString(),
                        response.statusCode(),
                        response.body()
                    )
                }
                response.body()
            }
        }

        fun copy(): Request {
            val newReq = Request(request)
            newReq.uri = this.uri
            newReq.method = this.method
            newReq.bodyPublisher = bodyPublisher
            return newReq
        }
    }

    private enum class HttpMethod {
        GET, POST
    }
}
