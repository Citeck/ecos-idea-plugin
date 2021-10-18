package ru.citeck

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.DataOutputStream
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL

class RestApiUtils {

    companion object {

        private val COOKIES = mutableMapOf("JSESSIONID" to "", "alfLogin" to "", "alfUsername3" to "")
        private val objectMapper = ObjectMapper()

        init {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        private fun auth() {
            val url = URL("http://localhost/share/page/dologin")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("Host", "localhost")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            connection.doOutput = true
            val out = DataOutputStream(connection.outputStream)
            out.write("username=admin&password=admin".toByteArray(Charsets.UTF_8))
            out.flush()
            out.close()
            for (header in connection.headerFields) {
                if (header.key == null) {
                    continue
                }
                if (header.key.equals("Set-Cookie")) {
                    for(value in header.value) {
                        HttpCookie.parse(value).forEach { if (COOKIES.containsKey(it.name)) COOKIES[it.name] = it.value }
                    }
                }
            }
        }


        fun <T> convert(jsonStr: String, clazz: Class<T>): T {
            return objectMapper.readValue(jsonStr, clazz)
        }


        fun execute(url: String, request: Any, firstTry: Boolean = true) : JsonNode{
            return execute(url, request, JsonNode::class.java, firstTry)
        }


        fun <T> execute(url: String, request: Any, clazz: Class<T>, firstTry: Boolean = true) : T {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Host", "localhost")
            connection.setRequestProperty("Content-Type", "application/javascript")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Origin", "http://localhost")
            connection.setRequestProperty(
                "Cookie",
                COOKIES.map { "${it.key}=${it.value}"}.joinToString(";")
            )
            connection.doOutput = true
            val out = DataOutputStream(connection.outputStream)
            out.write(objectMapper.writeValueAsString(request).toByteArray(Charsets.UTF_8))
            out.flush()
            out.close()

            return if (connection.responseCode == 401 && firstTry) {
                auth()
                execute(url, request, clazz, firstTry = false)
            } else if (connection.errorStream != null) {
                objectMapper.readValue(connection.errorStream, clazz)
            } else {
                objectMapper.readValue(connection.inputStream, clazz)
            }
        }

    }
}