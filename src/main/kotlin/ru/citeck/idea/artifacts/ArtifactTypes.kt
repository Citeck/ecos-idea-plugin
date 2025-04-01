package ru.citeck.idea.artifacts

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.IndexValue
import ru.citeck.idea.search.index.IndexesService
import java.util.*
import java.util.stream.Collectors

object ArtifactTypes {

    const val TYPE_FORM = "ui/form"
    const val TYPE_JOURNAL = "ui/journal"
    const val TYPE_TYPE = "model/type"

    fun getArtifactRefsByType(typeId: String, project: Project): List<String> {
        return IndexesService.getInstance(project)
            .stream(IndexKey(IndexKey.ARTIFACT_TYPE, typeId))
            .map(IndexValue::id)
            .sorted()
            .collect(Collectors.toList())
    }

    fun getFormComponents(psiFile: PsiFile?): List<JsonObject> {
        psiFile ?: return emptyList()
        val result = ArrayList<JsonObject>()
        PsiTreeUtil.processElements(psiFile, object : PsiElementProcessor<PsiElement> {
            override fun execute(element: PsiElement): Boolean {
                if (element is JsonProperty && element.name == "components") {
                    val value = element.value as? JsonArray ?: return true
                    value.children.forEach {
                        if (it is JsonObject) {
                            result.add(it)
                        }
                    }
                }
                return true
            }
        })
        return result
    }

    @JvmStatic
    fun isForm(psiFile: PsiFile?): Boolean {
        return psiFile != null && ArtifactsService.getInstance().getArtifactTypeMeta(psiFile)?.typeId == TYPE_FORM
    }

    @JvmStatic
    fun isJournal(psiFile: PsiFile?): Boolean {
        return psiFile != null && ArtifactsService.getInstance().getArtifactTypeMeta(psiFile)?.typeId == TYPE_JOURNAL
    }

    @JvmStatic
    fun isType(psiFile: PsiFile?): Boolean {
        return psiFile != null && ArtifactsService.getInstance().getArtifactTypeMeta(psiFile)?.typeId == TYPE_TYPE
    }
}
