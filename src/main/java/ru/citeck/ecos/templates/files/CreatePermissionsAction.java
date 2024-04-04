package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Permissions;

import java.util.List;

public class CreatePermissionsAction extends AbstractCreateEcosArtifactAction {
    public CreatePermissionsAction() {
        super("Permissions", List.of("yaml", "json"), "ecos-permission", Permissions.PATH);
    }
}
