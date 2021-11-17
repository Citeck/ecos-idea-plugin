package ru.citeck

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL

class EcosServer(var name: String, var url: String, var userName: String, var password: String) {

    companion object {

        private val localhost = EcosServer("localhost", "http://localhost", "admin", "admin")

        fun current(): EcosServer {
            return localhost
        }

    }

    private val cookies = mutableMapOf("JSESSIONID" to "", "alfLogin" to "", "alfUsername3" to "")

    private val objectMapper = ObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private fun auth() {
        val connection: HttpURLConnection = URL("${url}/share/page/dologin").openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.instanceFollowRedirects = false
        connection.setRequestProperty("Host", url)
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        connection.doOutput = true
        val out = DataOutputStream(connection.outputStream)
        out.write("username=${userName}&password=${password}".toByteArray(Charsets.UTF_8))
        out.flush()
        out.close()
        for (header in connection.headerFields) {
            if (header.key == null) {
                continue
            }
            if (header.key.equals("Set-Cookie")) {
                for(value in header.value) {
                    HttpCookie.parse(value).forEach { if (cookies.containsKey(it.name)) cookies[it.name] = it.value }
                }
            }
        }
    }


    fun execute(path: String, request: Any) : JsonNode {
        return execute(path, request, JsonNode::class.java)
    }


    fun <T> execute(path: String, request: Any, clazz: Class<T>) : T {

        auth()

        val connection = URL("${url}/${path}").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Host", url)
        connection.setRequestProperty("Content-Type", "application/javascript")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Origin", url)
        connection.setRequestProperty(
            "Cookie",
            cookies.map { "${it.key}=${it.value}"}.joinToString(";")
        )
        connection.doOutput = true
        val out = DataOutputStream(connection.outputStream)
        out.write(objectMapper.writeValueAsString(request).toByteArray(Charsets.UTF_8))
        out.flush()
        out.close()

        connection.responseCode
        return if (connection.errorStream != null) {
            objectMapper.readValue(connection.errorStream, clazz)
        } else {
            objectMapper.readValue(connection.inputStream, clazz)
        }

    }

}