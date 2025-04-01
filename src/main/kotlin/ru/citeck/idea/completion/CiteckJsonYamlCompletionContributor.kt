package ru.citeck.idea.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import icons.Icons
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.idea.artifacts.ArtifactInfo
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.artifacts.ArtifactsService

class CiteckJsonYamlCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.completionType != CompletionType.BASIC) {
            return
        }
        val file = parameters.originalFile
        if (file !is JsonFile && file !is YAMLFile) {
            return
        }
        val artifactInfo = ArtifactsService.getInstance().getArtifactInfo(file) ?: return
        val position = parameters.position
        val propertyName = PsiTreeUtil.getParentOfType(position, PsiNamedElement::class.java)?.name
        if (propertyName.isNullOrBlank()) {
            return
        }
        val project = file.project
        val completions = getTypeRefCompletions(project, propertyName, artifactInfo)
            ?: getJournalRefCompletions(project, propertyName)
            ?: getFormRefCompletions(project, propertyName)
            ?: return

        for (completion in completions) {
            result.addElement(
                LookupElementBuilder
                    .create(completion)
                    .withIcon(Icons.CiteckLogo)
            )
        }
    }

    private fun getFormRefCompletions(project: Project, property: String): List<String>? {
        if (property != "formRef") {
            return null
        }
        return ArtifactTypes.getArtifactRefsByType(ArtifactTypes.TYPE_FORM, project)
    }

    private fun getJournalRefCompletions(project: Project, property: String): List<String>? {
        if (property != "journalRef") {
            return null
        }
        return ArtifactTypes.getArtifactRefsByType(ArtifactTypes.TYPE_JOURNAL, project)
    }

    private fun getTypeRefCompletions(project: Project, property: String, artifactInfo: ArtifactInfo): List<String>? {
        val typeRefPropName = if (artifactInfo.getTypeId() == ArtifactTypes.TYPE_TYPE) {
            "parentRef"
        } else {
            "typeRef"
        }
        if (property != typeRefPropName) {
            return null
        }
        return ArtifactTypes.getArtifactRefsByType(ArtifactTypes.TYPE_TYPE, project)
    }
}
