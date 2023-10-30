package ru.citeck.ecos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//TODO Refactoring
public class EcosRestApiService {

    private final static Set<String> AUTH_COOKIES = Set.of("JSESSIONID", "alfLogin", "alfUsername3");
    private final static String AUTH_URL = "/share/page/dologin";
    private final static String MUTATE_RECORD_URL = "/share/api/records/mutate?k=recs_count_1_";
    private final static String QUERY_RECORD_URL = "/share/api/records/query?k=recs_count_1_";
    private final static String JS_CONSOLE_URL = "/share/proxy/alfresco/de/fme/jsconsole/execute";
    private final static String DEVTOOLS_INVOKE = "/alfresco/service/devtools/invoke";

    private final String host = "http://localhost";
    private final String userName = "admin";
    private final String password = "admin";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getHost() {
        return host;
    }

    public void authenticate(HttpURLConnection connection) throws Exception {

        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));

        HttpURLConnection authConnection = (HttpURLConnection) new URL(host + AUTH_URL).openConnection();
        authConnection.setRequestMethod("POST");
        authConnection.setInstanceFollowRedirects(false);
        authConnection.setRequestProperty("Host", host);
        authConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        authConnection.setConnectTimeout(3000);
        authConnection.setReadTimeout(3000);


        authConnection.setDoOutput(true);
        OutputStream outputStream = new DataOutputStream(authConnection.getOutputStream());
        outputStream.write(String.format("username=%s&password=%s", userName, password).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        authConnection.getResponseCode();

        String cookies = authConnection
            .getHeaderFields()
            .get("Set-Cookie")
            .stream()
            .map(HttpCookie::parse)
            .flatMap(Collection::stream)
            .filter(httpCookie -> AUTH_COOKIES.contains(httpCookie.getName()))
            .map(HttpCookie::toString)
            .collect(Collectors.joining(";"));

        connection.setRequestProperty("Cookie", cookies);

    }

    public JsonNode execute(String url, byte[] body, Integer timeout) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(host + url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", host);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Origin", host);
        connection.setConnectTimeout(3000);
        if (timeout != null) {
            connection.setReadTimeout(timeout);
        }

        authenticate(connection);

        if (body != null) {
            connection.setDoOutput(true);
            OutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(body);
            outputStream.flush();
            outputStream.close();
        }
        connection.getResponseCode();

        InputStream errorStream = connection.getErrorStream();

        if (errorStream != null) {
            String error = new BufferedReader(new InputStreamReader(errorStream))
                .lines()
                .collect(Collectors.joining("\n"));
            System.out.println(error);
            throw new RuntimeException(error);
        } else {
            String response = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")
            );
            return objectMapper.readValue(response, JsonNode.class);
        }

    }

    public void mutateRecord(String sourceId, String id, String mimeType, String name, byte[] content) throws Exception {

        Map<String, Object> request = Map.of("records", List.of(
            Map.of(
                "id", sourceId + "@",
                "attributes", Map.of(
                    ".att(n:\"_content\"){as(n:\"content-data\"){json}}", List.of(
                        Map.of(
                            "storage", "base64",
                            "type", mimeType,
                            "name", name,
                            "originalName", name,
                            "url", String.format(
                                "data:%s;base64,%s",
                                mimeType,
                                Base64.getEncoder().encodeToString(content)
                            )
                        )
                    )
                )
            )
        ));

        String json = objectMapper.writeValueAsString(request);

        try {
            execute(MUTATE_RECORD_URL, json.getBytes(StandardCharsets.UTF_8), 5000);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to mutate record " + sourceId + "@" + id + "<br>" + ex.getMessage());
        }

    }


    public JsonNode queryRecord(String sourceId, String id, List<String> attributes) throws Exception {

        Map<Object, Object> request = Map.of(
            "records", List.of(sourceId != null ? sourceId + "@" + id : id),
            "attributes", attributes
        );
        String json = objectMapper.writeValueAsString(request);


        try {
            return execute(QUERY_RECORD_URL + (sourceId != null ? sourceId.replace("@", "%2F") : ""), json.getBytes(StandardCharsets.UTF_8), 5000)
                .get("records")
                .get(0);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to get record " + sourceId + "@" + id + "<br>" + ex.getMessage());
        }

    }


    public JsonNode executeJS(String script) throws Exception {

        String json = objectMapper.writeValueAsString(Map.of(
            "script", script,
            "runas", "admin",
            "template", "",
            "spaceNodeRef", "",
            "transaction", "readwrite",
            "urlargs", "",
            "documentNodeRef", ""
        ));

        try {
            return execute(JS_CONSOLE_URL, json.getBytes(StandardCharsets.UTF_8), 60000);
        } catch (Exception ex) {
            try {
                return objectMapper.readValue(ex.getMessage(), JsonNode.class);
            } catch (Exception parseException) {
                throw new RuntimeException("Unable to execute JS<br>" + parseException.getMessage());
            }
        }

    }

    public JsonNode invoke(String className, String method, byte[] content) throws Exception {
        Map<String, String> request = Map.of(
            "className", className,
            "method", method,
            "content", Base64.getEncoder().encodeToString(content)
        );
        String json = objectMapper.writeValueAsString(request);
        try {
            return execute(DEVTOOLS_INVOKE, json.getBytes(StandardCharsets.UTF_8), 60000);
        } catch (Exception ex) {
            try {
                return objectMapper.readValue(ex.getMessage(), JsonNode.class);
            } catch (Exception parseException) {
                throw new RuntimeException("Unable to invoke method<br>" + parseException.getMessage());
            }
        }
    }

}