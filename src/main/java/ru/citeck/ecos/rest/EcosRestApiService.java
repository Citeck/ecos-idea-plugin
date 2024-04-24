package ru.citeck.ecos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.settings.EcosServer;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//TODO Refactoring
public class EcosRestApiService {

    private final static String JSESSIONID = "JSESSIONID";
    private final static String AUTHENTICATION_PROXY_HEADER = "X-ECOS-User";
    private final static String MUTATE_RECORD_URL = "/gateway/api/records/mutate?k=recs_count_1_";
    private final static String QUERY_RECORD_URL = "/gateway/api/records/query?k=recs_count_1_";
    private final static String JS_CONSOLE_URL = "/share/proxy/alfresco/de/fme/jsconsole/execute";
    private final static String RESET_SHARE_INDEX = "/share/page/index?reset=on";
    private final static String SHARE_INDEX = "/share/page/index";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String jSessionId = "";
    private final EcosServer ecosServer;
    private final Authenticator authenticator;

    public EcosRestApiService(EcosServer ecosServer, Project project) {
        this.ecosServer = ecosServer;
        this.authenticator = ServiceRegistry.getAuthenticationService().getAuthenticator(ecosServer, project);
    }

    public JsonNode execute(String url, byte[] body, Integer timeout) throws Exception {
        return execute(url, body, timeout, JsonNode.class, false);
    }

    public <T> T execute(String url, byte[] body, Integer timeout, Class<T> clazz, boolean updateCredentials) throws Exception {

        String host = ecosServer.getHost();

        HttpURLConnection connection = (HttpURLConnection) new URL(host + url).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", host);
        connection.setRequestProperty(AUTHENTICATION_PROXY_HEADER, ecosServer.getUserName());
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Origin", host);
        authenticator.authenticate(connection, updateCredentials);
        connection.setConnectTimeout(3000);

        if (!StringUtil.isEmpty(jSessionId)) {
            connection.setRequestProperty("Cookie", jSessionId);
        }

        if (timeout != null) {
            connection.setReadTimeout(timeout);
        }

        if (body != null) {
            connection.setDoOutput(true);
            OutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(body);
            outputStream.flush();
            outputStream.close();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == 401) {
            return execute(url, body, timeout, clazz, true);
        }

        updateJSessionId(connection);

        InputStream errorStream = connection.getErrorStream();

        if (errorStream != null) {
            String error = new BufferedReader(new InputStreamReader(errorStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try {
                error = objectMapper.readValue(error, JsonNode.class).get("detail").asText();
            } catch (Exception ignored) {
            }
            throw new RuntimeException(error);

        } else {
            String response = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")
            );
            if (String.class.equals(clazz)) {
                return clazz.cast(response);
            }
            return objectMapper.readValue(response, clazz);
        }

    }

    private void updateJSessionId(HttpURLConnection connection) {
        connection
                .getHeaderFields()
                .getOrDefault("Set-Cookie", Collections.emptyList())
                .stream()
                .map(HttpCookie::parse)
                .flatMap(Collection::stream)
                .filter(httpCookie -> JSESSIONID.equals(httpCookie.getName()))
                .map(HttpCookie::toString)
                .findFirst()
                .ifPresent(cookie -> jSessionId = cookie);
    }

    public void mutateRecord(String sourceId, String id, String mimeType, String name, byte[] content, String mutationAttribute, Map<String, Object> customAttributes) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(".att(n:\"" + mutationAttribute + "\"){as(n:\"content-data\"){json}}", List.of(
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
        ));

        if (customAttributes != null) {
            attributes.putAll(customAttributes);
        }

        Map<String, Object> request = Map.of("records", List.of(
                Map.of(
                        "id", sourceId + "@" + id,
                        "attributes", attributes
                )
        ));

        String json = objectMapper.writeValueAsString(request);

        try {
            execute(MUTATE_RECORD_URL, json.getBytes(StandardCharsets.UTF_8), 5000);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to mutate record " + sourceId + "@" + id + "<br>" + ex.getMessage());
        }

    }

    public void mutateRecord(String sourceId, String id, String mimeType, String name, byte[] content, String mutationAttribute) throws Exception {
        mutateRecord(sourceId, id, mimeType, name, content, mutationAttribute, null);
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

    public boolean recordExists(String sourceId, String id) throws Exception {
        return !(queryRecord(sourceId, id, List.of("_created?str")).get("attributes").get("_created?str") instanceof NullNode);
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

    public void resetShareIndex() {

        //Do not remove, used for authentication on Alfresco Share
        try {
            execute(SHARE_INDEX, "{}".getBytes(), 60000, String.class, false);
        } catch (Exception ignored) {
        }

        try {
            execute(RESET_SHARE_INDEX, "{}".getBytes(), 60000, String.class, false);
        } catch (Exception ex) {
            try {
                objectMapper.readValue(ex.getMessage(), String.class);
            } catch (Exception parseException) {
                throw new RuntimeException("Unable to reset Share index<br>" + ex.getMessage());
            }
        }
    }

}
