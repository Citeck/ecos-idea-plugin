package ru.citeck.ecos.settings;

import com.intellij.util.ui.table.TableModelEditor;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

class EcosServerTableModel extends TableModelEditor<EcosServer> {

    private final static EcosServerColumnInfo[] COLUMNS = new EcosServerColumnInfo[]{
            new EcosServerColumnInfo("Name", EcosServer::getName, EcosServer::setName),
            new EcosServerColumnInfo("Host", EcosServer::getHost, EcosServer::setHost),
            new EcosServerColumnInfo("Login", EcosServer::getLogin, EcosServer::setLogin),
            new EcosServerColumnInfo("Auth Method", EcosServer::getAuthMethod, EcosServer::setAuthMethod,
                    () -> new DefaultCellEditor(new JComboBox<>(new Object[]{"BASIC", "OAUTH"}))
            )
    };

    public EcosServerTableModel(List<EcosServer> servers) {
        super(servers, COLUMNS, new EcosServerCollectionItemEditor(), "No servers defined");
        helper.reset(servers.stream().map(EcosServer::clone).collect(Collectors.toList()));
    }

}
