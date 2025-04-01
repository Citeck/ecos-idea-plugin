package ru.citeck.idea.artifacts.action.navigate.providers

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonValue
import com.intellij.psi.PsiFile
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.utils.CiteckPsiUtils
import java.util.stream.Collectors

class JsonJournalItemsProvider : NavigateInFileItemsProvider {

    override fun getItems(psiFile: PsiFile?): List<NavigateInFileItem>? {

        if (psiFile !is JsonFile) {
            return null
        }

        if (!ArtifactTypes.isJournal(psiFile)) {
            return null
        }

        return getColumns(psiFile).map { column ->
            NavigateInFileItem(CiteckPsiUtils.getProperty(column, "id"), column)
        }
    }

    private fun getColumns(psiFile: PsiFile): List<JsonObject> {

        if (psiFile !is JsonFile) {
            return emptyList()
        }

        val root = psiFile.topLevelValue as? JsonObject ?: return listOf()

        val columns = root.findProperty("columns")
        if (columns == null || columns.value !is JsonArray) {
            return listOf()
        }

        return (columns.value as JsonArray)
            .valueList
            .stream()
            .filter { column: JsonValue? -> column is JsonObject }
            .map { column: JsonValue -> column as JsonObject }
            .collect(Collectors.toList())
    }
}
