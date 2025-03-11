package ru.citeck.idea.authentication.oidc.callback

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import lombok.SneakyThrows
import lombok.Synchronized
import java.io.Closeable
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.*

@Service(Service.Level.APP)
class OidcCallbackServer : Disposable {

    companion object {
        private val log = Logger.getInstance(OidcCallbackServer::class.java)

        private const val CALLBACK_CONTENT_PATH = "/citeck/oidc/callback.html"
        private var callbackHtmlContent = "<html><body><h1>Error</h1></body></html>".toByteArray(StandardCharsets.UTF_8)
    }

    private val timeoutScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    @Volatile
    private var server: HttpServer? = null

    init {
        try {
            OidcCallbackServer::class.java.getResourceAsStream(CALLBACK_CONTENT_PATH).use { htmlIn ->
                if (htmlIn != null) {
                    callbackHtmlContent = htmlIn.readAllBytes()
                } else {
                    log.error("OIDC Callback content is not found")
                }
            }
        } catch (e: IOException) {
            log.error("OIDC Callback content reading failed", e)
        }
    }

    @Synchronized
    fun startServer(port: Int, result: CompletableFuture<OidcCallbackData>, timeout: Duration): Closeable {
        if (server != null) {
            result.completeExceptionally(IllegalStateException("Server is already started"))
            return Closeable {}
        }
        log.info("Starting OIDC callback server on port $port")
        try {
            val server = HttpServer.create(InetSocketAddress(port), 0)

            val timeoutCheckFuture = timeoutScheduler.schedule({
                if (this.server === server) {
                    stopServer()
                    result.completeExceptionally(RuntimeException(
                        "Callback doesn't called after " + timeout.toSeconds() + " seconds.")
                    )
                }
            }, timeout.toMillis(), TimeUnit.MILLISECONDS)

            server.createContext("/callback", OAuthCallbackHandler(result, timeoutCheckFuture))
            server.executor = null
            server.start()

            this.server = server

            return Closeable {
                timeoutCheckFuture.cancel(true)
                stopServer()
            }
        } catch (e: Throwable) {
            log.error("Callback server start failed on port $port", e)
            try {
                result.completeExceptionally(e)
            } catch (callbackError: Exception) {
                log.error("Callback accept error", callbackError)
            }
            stopServer()
            return Closeable {}
        }
    }

    @Synchronized
    private fun stopServer() {
        if (server != null) {
            server!!.stop(0)
            server = null
            log.info("OIDC Callback server stopped")
        }
    }

    override fun dispose() {
        timeoutScheduler.shutdownNow()
        stopServer()
    }

    private inner class OAuthCallbackHandler(
        private val result: CompletableFuture<OidcCallbackData>,
        private val timeoutCheckFuture: ScheduledFuture<*>
    ) : HttpHandler {

        @SneakyThrows
        override fun handle(exchange: HttpExchange) {
            val queryParams = readQueryParams(exchange.requestURI.query)

            exchange.sendResponseHeaders(200, callbackHtmlContent.size.toLong())
            val headers = exchange.responseHeaders
            headers["Content-Type"] = "text/html"
            headers["Cache-Control"] = "no-cache"

            exchange.responseBody.use { os ->
                os.write(callbackHtmlContent)
            }
            timeoutCheckFuture.cancel(true)
            stopServer()

            val data = OidcCallbackData(
                queryParams.getOrDefault("code", ""),
                queryParams.getOrDefault("state", "")
            )
            result.complete(data)
        }

        fun readQueryParams(query: String): Map<String, String> {
            val queryPairs: MutableMap<String, String> = LinkedHashMap()
            val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (pair in pairs) {
                val idx = pair.indexOf("=")
                queryPairs[URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8)] =
                    URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
            }
            return queryPairs
        }
    }
}
