package ru.citeck.ecos.codeinsight.forms.components;

public class SelectJournal extends AssocComponent {

    public static final String TYPE = "selectJournal";

    private String modalTitle = "";
    private String journalId = "";
    private String queryData = "";


    @Override
    public String getType() {
        return TYPE;
    }

    public String getModalTitle() {
        return modalTitle;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getQueryData() {
        return queryData;
    }

}
