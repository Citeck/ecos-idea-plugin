package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Journal;

import java.util.List;

public class CreateJournalAction extends AbstractCreateEcosArtifactAction {
    public CreateJournalAction() {
        super("Journal", List.of("yaml", "json"), "ecos-journal", Journal.PATH);
    }
}
