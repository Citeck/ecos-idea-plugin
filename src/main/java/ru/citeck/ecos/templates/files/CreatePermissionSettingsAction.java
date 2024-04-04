package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.PermissionSettings;

import java.util.List;

public class CreatePermissionSettingsAction extends AbstractCreateEcosArtifactAction {
    public CreatePermissionSettingsAction() {
        super("Permission settings", List.of("yaml", "json"), "ecos-permission-settings", PermissionSettings.PATH);
    }
}
