package ru.citeck.idea.artifacts.type

import com.intellij.json.psi.JsonPsiUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.webapp.api.entity.EntityRef

interface ArtifactTypeController {

    //fun getArtifacts(directory: PsiFile):

    fun getArtifactUrl(ref: EntityRef): String {
        return "/v2/dashboard?recordRef=$ref&ws=admin\$workspace"
    }

    fun prepareDeployAtts(file: PsiFile): ObjectData

    fun getArtifactId(file: PsiFile): String {
        return JsonPsiUtil.stripQuotes(getArtifactIdPsiElement(file)?.text ?: "")
    }

    fun getArtifactIdPsiElement(file: PsiFile): PsiElement?

    fun getFetchAtts(file: PsiFile): Map<String, String>

    fun writeFetchedData(file: PsiFile, value: ObjectData)

    fun isIndexable(file: PsiFile): Boolean
}
