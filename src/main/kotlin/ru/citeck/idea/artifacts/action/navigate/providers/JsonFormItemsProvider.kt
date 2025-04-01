package ru.citeck.idea.artifacts.action.navigate.providers

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.util.text.Strings
import com.intellij.psi.PsiFile
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.utils.CiteckPsiUtils
import java.util.*
import java.util.stream.Collectors

class JsonFormItemsProvider : NavigateInFileItemsProvider {

    override fun getItems(psiFile: PsiFile?): List<NavigateInFileItem>? {

        if (psiFile !is JsonFile) {
            return null
        }

        return ArtifactTypes.getFormComponents(psiFile)
            .stream()
            .map { component: JsonObject ->
                val type = CiteckPsiUtils.getProperty(component, "type")
                val key = CiteckPsiUtils.getProperty(component, "key")

                if (Strings.isEmpty(type) || "column" == type) {
                    return@map null
                }
                NavigateInFileItem("$type@$key", component)
            }
            .filter { obj: NavigateInFileItem? -> Objects.nonNull(obj) }
            .collect(Collectors.toList()).filterNotNull()
    }
}
