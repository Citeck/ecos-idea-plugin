package ru.citeck.ecos.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class EcosServersConfiguration implements Configurable {

    private EcosServerTableModel ecosServerTableModel;
    private List<EcosServer> originalItems;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Servers";
    }

    @Override
    public @Nullable JComponent createComponent() {

        this.originalItems = ServiceRegistry.getEcosSettingsService().getServers();
        this.ecosServerTableModel = new EcosServerTableModel(originalItems);

        return FormBuilder
                .createFormBuilder()
                .addLabeledComponentFillVertically("Servers", ecosServerTableModel.createComponent())
                .getPanel();
    }

    @Override
    public boolean isModified() {
        return ecosServerTableModel.isModified();
    }

    @Override
    public void reset() {
        ecosServerTableModel.reset(originalItems.stream().map(EcosServer::clone).collect(Collectors.toList()));
    }

    @Override
    public void apply() throws ConfigurationException {
        try {
            ServiceRegistry.getEcosSettingsService().setServers(ecosServerTableModel.apply());
        } catch (JsonProcessingException e) {
            throw new ConfigurationException("Unable to save servers", e, "Error");
        }
    }

}
