package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.Dashboard;
import ru.citeck.ecos.files.types.Localization;

import java.util.List;

public class CreateLocalizationAction extends AbstractCreateEcosArtifactAction {
    public CreateLocalizationAction() {
        super("Localization", List.of("yaml", "json"), "ecos-i18n", Localization.PATH);
    }
}
