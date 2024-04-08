package ru.citeck.ecos.rest;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.citeck.ecos.settings.EcosServer;
import ru.citeck.ecos.ui.PasswordInputDialog;

import java.net.HttpURLConnection;

@RequiredArgsConstructor
public abstract class Authenticator {

    @Getter(AccessLevel.PROTECTED)
    private final EcosServer ecosServer;

    @Getter(AccessLevel.PROTECTED)
    private final Project project;

    private String password;

    String getPassword(boolean updateCredentials) {

        if (Strings.isEmpty(password) || updateCredentials) {
            PasswordInputDialog passwordInputDialog = new PasswordInputDialog(project, ecosServer.getUserName(), password);
            passwordInputDialog.setTitle("Please Provide Password:");
            if (passwordInputDialog.showAndGet()) {
                password = passwordInputDialog.getPassword();
            } else {
                throw new RuntimeException("Cancelled");
            }
        }
        return password;
    }

    public abstract void authenticate(HttpURLConnection connection, boolean updateCredentials) throws Exception;

}
