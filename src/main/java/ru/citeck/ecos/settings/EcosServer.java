package ru.citeck.ecos.settings;

import lombok.Data;

@Data
public class EcosServer implements Cloneable{

    private String name = "";
    private String host = "";
    private String login = "";
    private String authMethod = "BASIC";

    public EcosServer() {
    }

    public EcosServer(String name, String host, String login, String authMethod) {
        this.name = name;
        this.host = host;
        this.login = login;
        this.authMethod = authMethod;
    }

    @Override
    public EcosServer clone() {
        return new EcosServer(name, host, login, authMethod);
    }

}
