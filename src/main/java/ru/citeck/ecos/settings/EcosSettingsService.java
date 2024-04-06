package ru.citeck.ecos.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Service
@State(name = "ECOS Settings", storages = @Storage(value = "ecosSettings.xml"))
public final class EcosSettingsService implements PersistentStateComponent<EcosSettingsService> {

    private static class EcosServerList extends ArrayList<EcosServer> {
    }

    @Attribute("servers")
    private String servers;

    @Override
    public @NotNull EcosSettingsService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull EcosSettingsService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<EcosServer> getServers() {
        if (Strings.isNullOrEmpty(servers)) {
            return new ArrayList<>();
        }
        try {
            return new ObjectMapper().readValue(servers, EcosServerList.class);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setServers(List<EcosServer> servers) throws JsonProcessingException {
        this.servers = new ObjectMapper().writeValueAsString(servers);
    }

}
