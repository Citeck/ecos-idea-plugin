package ru.citeck.ecos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import ru.citeck.ecos.settings.EcosServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class OAuth2Authenticator extends Authenticator {

    private String token;

    public OAuth2Authenticator(EcosServer ecosServer, Project project) {
        super(ecosServer, project);
    }

    @Override
    public void authenticate(HttpURLConnection connection, boolean updateCredentials) throws Exception {

        getNewToken(updateCredentials);
        connection.setRequestProperty("Authorization", token);
    }

    private void getNewToken(boolean updateCredentials) throws IOException {
        if (token == null || updateCredentials) {
            EcosServer ecosServer = getEcosServer();
            String host = ecosServer.getAccessTokenUrl();
            String grantType = ecosServer.getGrantType();

            HttpURLConnection connection = (HttpURLConnection) new URL(host).openConnection();


            String body;
            if (AuthenticationService.GRANT_TYPE_PASSWORD.equals(grantType)) {
                body = String.format(
                        "grant_type=%s&client_id=%s&client_secret=%s&username=%s&password=%s&credentialId=",
                        grantType,
                        ecosServer.getClientId(),
                        ecosServer.getClientSecret(),
                        ecosServer.getUserName(),
                        getPassword(updateCredentials)
                );
            } else if (AuthenticationService.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
                body = String.format(
                        "grant_type=%s&client_id=%s&client_secret=%s",
                        grantType,
                        ecosServer.getClientId(),
                        ecosServer.getClientSecret()
                );
            } else {
                throw new RuntimeException("Unknown grant_type");
            }

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", host);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json; charset=utf-8");
            connection.setRequestProperty("Origin", host);
            connection.setConnectTimeout(3000);

            connection.setDoOutput(true);
            OutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                if (AuthenticationService.GRANT_TYPE_PASSWORD.equals(grantType)) {
                    getNewToken(true);
                    return;
                }
                throw new RuntimeException("Invalid credentials");
            }

            String response = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")
            );

            JsonNode jsonNode = new ObjectMapper().readValue(response, JsonNode.class);

            token = "Bearer " + jsonNode.get("access_token").asText();

        }
    }

}
