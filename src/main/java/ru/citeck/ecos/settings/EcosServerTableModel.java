package ru.citeck.ecos.settings;

import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.table.TableModelEditor;
import ru.citeck.ecos.rest.AuthenticationService;
import ru.citeck.ecos.ui.PasswordCellRenderer;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

class EcosServerTableModel extends TableModelEditor<EcosServer> {

    private final static EcosServerColumnInfo[] COLUMNS = new EcosServerColumnInfo[]{
            new EcosServerColumnInfo("Name", EcosServer::getName, EcosServer::setName),
            new EcosServerColumnInfo("Host", EcosServer::getHost, EcosServer::setHost),
            new EcosServerColumnInfo("User Name", EcosServer::getUserName, EcosServer::setUserName)
                    .withCellEditableEvaluator(ecosServer -> !AuthenticationService.GRANT_TYPE_CLIENT_CREDENTIALS.equals(ecosServer.getGrantType())),
            new EcosServerColumnInfo("Auth Method", EcosServer::getAuthMethod, EcosServer::setAuthMethod)
                    .withEditor(new DefaultCellEditor(new JComboBox<>(AuthenticationService.AUTH_METHODS.toArray(new String[0])))),
            new EcosServerColumnInfo("Grant Type", EcosServer::getGrantType, EcosServer::setGrantType)
                    .withEditor(new DefaultCellEditor(new JComboBox<>(AuthenticationService.GRANT_TYPES.toArray(new String[0]))))
                    .withCellEditableEvaluator(ecosServer -> AuthenticationService.OAUTH2_METHOD.equals(ecosServer.getAuthMethod())),
            new EcosServerColumnInfo("Client ID", EcosServer::getClientId, EcosServer::setClientId)
                    .withCellEditableEvaluator(ecosServer -> AuthenticationService.OAUTH2_METHOD.equals(ecosServer.getAuthMethod())),
            new EcosServerColumnInfo("Client Secret", EcosServer::getClientSecret, EcosServer::setClientSecret)
                    .withRenderer(new PasswordCellRenderer())
                    .withEditor(new DefaultCellEditor(new JBPasswordField()))
                    .withCellEditableEvaluator(ecosServer -> AuthenticationService.OAUTH2_METHOD.equals(ecosServer.getAuthMethod())),
            new EcosServerColumnInfo("OAuth Provider URL", EcosServer::getOauthProviderUrl, EcosServer::setOauthProviderUrl)
                    .withCellEditableEvaluator(ecosServer -> AuthenticationService.OAUTH2_METHOD.equals(ecosServer.getAuthMethod()))
    };

    public EcosServerTableModel(List<EcosServer> servers) {
        super(servers, COLUMNS, new EcosServerCollectionItemEditor(), "No servers defined");
        helper.reset(servers.stream().map(EcosServer::clone).collect(Collectors.toList()));
    }

}
