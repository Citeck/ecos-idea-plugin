package ru.citeck.ecos.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import ru.citeck.ecos.settings.EcosServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class OAuth2Authenticator extends Authenticator {

    private static final String DEFAULT_OAUTH_PROVIDER_URL = "/ecos-idp/auth/realms/ecos-app";
    private static final String OPENID_CONFIGURATION_URL = "/.well-known/openid-configuration";

    private String token;
    private String tokenUrl;

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
            String tokenUrl = getTokenUrl(ecosServer);

            String grantType = ecosServer.getGrantType();

            HttpURLConnection connection = (HttpURLConnection) new URL(tokenUrl).openConnection();

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
            connection.setRequestProperty("Host", tokenUrl);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json; charset=utf-8");
            connection.setRequestProperty("Origin", tokenUrl);
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

    private String getTokenUrl(EcosServer ecosServer) throws IOException {

        if (Strings.isNotEmpty(tokenUrl)) {
            return tokenUrl;
        }

        String configUrl = ecosServer.getOauthProviderUrl();
        if (Strings.isEmpty(configUrl)) {
            configUrl = ecosServer.getHost() + DEFAULT_OAUTH_PROVIDER_URL;
        }
        configUrl += OPENID_CONFIGURATION_URL;

        HttpURLConnection connection = (HttpURLConnection) new URL(configUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Host", configUrl);
        connection.setRequestProperty("Accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Origin", tokenUrl);
        connection.setConnectTimeout(3000);

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Unable to get token URL");
        }

        tokenUrl = new ObjectMapper()
                .readValue(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8), JsonNode.class)
                .get("token_endpoint")
                .asText();

        return tokenUrl;

    }


}
