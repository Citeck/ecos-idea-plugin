package ru.citeck.ecos.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import icons.Icons;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.rest.AuthenticationService;

import javax.swing.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class EcosServer implements Cloneable {

    private static final String SELECT_ECOS_SERVER_TITLE = "Select ECOS Server:";

    private static class EcosServerColoredListCellRenderer extends ColoredListCellRenderer<EcosServer> {
        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends EcosServer> list, EcosServer value, int index, boolean selected, boolean hasFocus) {
            setPaintFocusBorder(false);
            setIcon(Icons.CiteckLogo);
            setFont(list.getFont());
            append(value.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.getForeground()), true);
            append(" " + value.getHost(), SimpleTextAttributes.GRAY_SMALL_ATTRIBUTES);
        }

    }

    private String id = UUID.randomUUID().toString();
    private String name = "";
    private String host = "";
    private String userName = "";
    private String authMethod = "Basic";
    private String grantType = "";
    private String clientId = "";
    private String clientSecret = "";
    private String oauthProviderUrl = "";

    public EcosServer() {
    }

    public EcosServer(
            String id,
            String name,
            String host,
            String userName,
            String authMethod,
            String grantType,
            String clientId,
            String oauthProviderUrl
    ) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.userName = userName;
        this.authMethod = authMethod;
        this.grantType = grantType;
        this.clientId = clientId;
        this.oauthProviderUrl = oauthProviderUrl;
    }

    public void setHost(String host) {
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        this.host = host;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
        if (AuthenticationService.BASIC_METHOD.equals(authMethod)) {
            this.grantType = "";
            this.clientId = "";
            this.clientSecret = "";
            this.oauthProviderUrl = "";
        }
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
        if (AuthenticationService.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            this.userName = "";
        }
    }

    public void setOauthProviderUrl(String oauthProviderUrl) {
        if (oauthProviderUrl.endsWith("/")) {
            oauthProviderUrl = oauthProviderUrl.substring(0, oauthProviderUrl.length() - 1);
        }
        this.oauthProviderUrl = oauthProviderUrl;
    }


    private CredentialAttributes getClientSecretCredentialsAttribute() {
        return new CredentialAttributes("ecos-" + id, "oauth2-client-secret");
    }

    @JsonIgnore
    public String getClientSecret() {
        return PasswordSafe.getInstance().getPassword(getClientSecretCredentialsAttribute());
    }

    public void setClientSecret(String clientSecret) {
        PasswordSafe.getInstance().setPassword(getClientSecretCredentialsAttribute(), clientSecret);
    }

    @Override
    public EcosServer clone() {
        return new EcosServer(id, name, host, userName, authMethod, grantType, clientId, oauthProviderUrl);
    }

    public static void doWithServer(Project project, Consumer<EcosServer> consumer) {
        List<EcosServer> ecosServers = ServiceRegistry.getEcosSettingsService().getServers();
        if (ecosServers.isEmpty()) {
            configureServers(project);
        } else if (ecosServers.size() == 1) {
            consumer.accept(ecosServers.get(0));
        } else {
            JBPopupFactory
                    .getInstance()
                    .createPopupChooserBuilder(ecosServers)
                    .setTitle(SELECT_ECOS_SERVER_TITLE)
                    .setNamerForFiltering(ecosServer -> ecosServer.getName() + "|" + ecosServer.getHost())
                    .setRenderer(new EcosServerColoredListCellRenderer())
                    .setItemChosenCallback(consumer::accept)
                    .setRequestFocus(true)
                    .createPopup()
                    .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());
        }
    }

    private static void configureServers(Project project) {
        JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(List.of("Configure servers..."))
                .setTitle(SELECT_ECOS_SERVER_TITLE)
                .setItemChosenCallback(chosenDeployer -> ShowSettingsUtil.getInstance().showSettingsDialog(project, EcosServersConfiguration.class))
                .setRequestFocus(true)
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());
    }

}
