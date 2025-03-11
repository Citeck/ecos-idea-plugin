package ru.citeck.idea.artifacts.type

import com.intellij.psi.PsiFile
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.webapp.api.entity.EntityRef

interface ArtifactTypeController {

    fun getArtifactUrl(ref: EntityRef): String {
        return "/v2/dashboard?recordRef=$ref&ws=admin\$workspace"
    }

    fun prepareDeployAtts(file: PsiFile): ObjectData

    fun getArtifactId(file: PsiFile): String

    fun getFetchAtts(file: PsiFile): Map<String, String>

    fun writeFetchedData(file: PsiFile, value: ObjectData)
}
