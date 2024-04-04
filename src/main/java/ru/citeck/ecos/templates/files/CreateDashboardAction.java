package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Dashboard;

import java.util.List;

public class CreateDashboardAction extends AbstractCreateEcosArtifactAction {
    public CreateDashboardAction() {
        super("Dashboard", List.of("json"), "ecos-dashboard", Dashboard.PATH);
    }
}
