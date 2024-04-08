package ru.citeck.ecos.rest;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import ru.citeck.ecos.settings.EcosServer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public final class AuthenticationService {

    public static String BASIC_METHOD = "Basic";
    public static String OAUTH2_METHOD = "OAuth2";

    public static String GRANT_TYPE_PASSWORD = "password";
    public static String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    public static Collection<String> AUTH_METHODS = List.of(
            BASIC_METHOD, OAUTH2_METHOD
    );

    public static Collection<String> GRANT_TYPES = List.of(
            GRANT_TYPE_PASSWORD, GRANT_TYPE_CLIENT_CREDENTIALS
    );

    private final Map<EcosServer, Authenticator> authenticators = new ConcurrentHashMap<>();

    public Authenticator getAuthenticator(EcosServer ecosServer, Project project) {
        Authenticator authenticator = authenticators.get(ecosServer);
        if (authenticator == null) {
            if (BASIC_METHOD.equals(ecosServer.getAuthMethod())) {
                authenticator = new BasicAuthenticator(ecosServer, project);
            } else if (OAUTH2_METHOD.equals(ecosServer.getAuthMethod())) {
                authenticator = new OAuth2Authenticator(ecosServer, project);
            }
            authenticators.put(ecosServer, authenticator);
        }
        return authenticator;
    }

}
