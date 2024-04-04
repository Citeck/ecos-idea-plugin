package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.Dashboard;
import ru.citeck.ecos.files.types.Form;

import java.util.List;

public class CreateDashboardAction extends AbstractCreateEcosArtifactAction {
    public CreateDashboardAction() {
        super("Dashboard", List.of("json"), "ecos-dashboard", Dashboard.PATH);
    }
}
