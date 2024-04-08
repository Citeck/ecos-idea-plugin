package ru.citeck.ecos.rest;

import com.intellij.openapi.project.Project;
import ru.citeck.ecos.settings.EcosServer;

import java.net.HttpURLConnection;
import java.util.Base64;

public class BasicAuthenticator extends Authenticator {

    public BasicAuthenticator(EcosServer ecosServer, Project project) {
        super(ecosServer, project);
    }

    @Override
    public void authenticate(HttpURLConnection connection, boolean updateCredentials) {
        String auth = getEcosServer().getUserName() + ":" + getPassword(updateCredentials);
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));
    }

}
