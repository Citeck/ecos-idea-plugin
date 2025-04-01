package ru.citeck.idea.artifacts

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import ru.citeck.ecos.webapp.api.entity.EntityRef
import ru.citeck.idea.artifacts.type.ArtifactTypeController

interface ArtifactInfo {

    fun getTypeId(): String

    fun getArtifactId(): String

    fun getArtifactIdPsiElement(): PsiElement?

    fun getRef(file: PsiFile): EntityRef

    fun getController(): ArtifactTypeController

    fun getMeta(): ArtifactTypeMeta
}
