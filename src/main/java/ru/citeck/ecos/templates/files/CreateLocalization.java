package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Localization;

import java.util.List;

public class CreateLocalization extends AbstractCreateEcosArtifactAction {
    public CreateLocalization() {
        super("Localization", List.of("yaml", "json"), "ecos-i18n", Localization.PATH);
    }
}
