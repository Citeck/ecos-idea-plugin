package ru.citeck.idea.artifacts.action.navigate.providers

import com.intellij.json.psi.JsonFile
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.artifacts.ArtifactsService.Companion.getInstance
import ru.citeck.idea.json.PsiJPath

class DataTypeFileItemsProvider : NavigateInFileItemsProvider {

    companion object {
        private val ROLES_PATH = PsiJPath.parse("$.model.roles[:].id")
        private val STATUSES_PATH = PsiJPath.parse("$.model.statuses[:].id")
        private val ATTRIBUTES_PATH = PsiJPath.parse("$.model.attributes[:].id")
    }

    override fun getItems(psiFile: PsiFile?): List<NavigateInFileItem>? {

        if (psiFile !is YAMLFile && psiFile !is JsonFile) {
            return null
        }

        val artifactInfo = getInstance().getArtifactInfo(psiFile)
        if (artifactInfo == null || ArtifactTypes.TYPE_TYPE != artifactInfo.getMeta().typeId) {
            return null
        }

        val variants = mapOf(
            "role" to ROLES_PATH.getStrListWithElements(psiFile),
            "status" to STATUSES_PATH.getStrListWithElements(psiFile),
            "attribute" to ATTRIBUTES_PATH.getStrListWithElements(psiFile)
        )

        return variants.flatMap { (key, variants) ->
            variants.map { NavigateInFileItem(key + "@" + it.first, it.second) }
        }
    }
}
