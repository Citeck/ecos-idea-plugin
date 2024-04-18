package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.model.PermissionSettings;

import java.util.List;

public class CreatePermissionSettings extends AbstractCreateEcosArtifactAction {
    public CreatePermissionSettings() {
        super("Permission settings", List.of("yml", "json"), "ecos-permission-settings", PermissionSettings.PATH);
    }
}
