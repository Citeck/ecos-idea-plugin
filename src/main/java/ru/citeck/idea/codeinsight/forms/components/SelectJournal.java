package ru.citeck.idea.codeinsight.forms.components;

import lombok.Getter;

@Getter
public class SelectJournal extends AssocComponent {

    public static final String TYPE = "selectJournal";

    private final String modalTitle = "";
    private final String journalId = "";
    private final String queryData = "";


    @Override
    public String getType() {
        return TYPE;
    }

}
