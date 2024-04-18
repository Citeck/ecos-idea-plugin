package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ui.Journal;

import java.util.List;

public class CreateJournal extends AbstractCreateEcosArtifactAction {
    public CreateJournal() {
        super("Journal", List.of("yml", "json"), "ecos-journal", Journal.PATH);
    }
}
