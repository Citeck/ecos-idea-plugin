package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Permissions;

import java.util.List;

public class CreatePermissions extends AbstractCreateEcosArtifactAction {
    public CreatePermissions() {
        super("Permissions", List.of("yaml", "json"), "ecos-permission", Permissions.PATH);
    }
}
