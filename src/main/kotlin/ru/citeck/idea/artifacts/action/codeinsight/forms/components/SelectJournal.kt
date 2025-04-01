package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class SelectJournal : AssocComponent() {

    companion object {
        const val TYPE = "selectJournal"
    }

    val modalTitle = ""
    val journalId = ""
    val queryData = ""

    override fun getType(): String {
        return TYPE
    }
}
