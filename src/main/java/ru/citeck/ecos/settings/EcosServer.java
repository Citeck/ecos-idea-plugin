package ru.citeck.ecos.settings;

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

    private String name = "";
    private String host = "";
    private String userName = "";
    private String authMethod = "Basic";
    private String grantType = "";
    private String clientId = "";
    private String clientSecret = "";
    private String accessTokenUrl = "";

    public EcosServer() {
    }

    public EcosServer(String name,
                      String host,
                      String userName,
                      String authMethod,
                      String grantType,
                      String clientId,
                      String clientSecret,
                      String accessTokenUrl
    ) {
        this.name = name;
        this.host = host;
        this.userName = userName;
        this.authMethod = authMethod;
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessTokenUrl = accessTokenUrl;
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
            this.accessTokenUrl = "";
        }
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
        if (AuthenticationService.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            this.userName = "";
        }
    }

    @Override
    public EcosServer clone() {
        return new EcosServer(name, host, userName, authMethod, grantType, clientId, clientSecret, accessTokenUrl);
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
