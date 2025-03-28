package ru.citeck.idea.search.index.indexers

import com.intellij.openapi.util.text.Strings
import com.intellij.util.indexing.FileContent
import ru.citeck.idea.files.FileType
import ru.citeck.idea.files.types.citeck.EcosArtifact
import ru.citeck.idea.search.index.EcosFileIndexer
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.Indexes.FileIndexes
import java.util.function.Consumer

class EcosArtifactIndexer : EcosFileIndexer {

    override fun accept(fileType: FileType): Boolean {
        return fileType is EcosArtifact
    }

    override fun map(inputData: FileContent, indexes: FileIndexes) {

        val psiFile = inputData.psiFile
        val fileType = getFileType(inputData) as EcosArtifact

        if (!fileType.isIndexable(psiFile)) {
            return
        }

        val idPsiElement = fileType.getIdPsiElement(psiFile) ?: return
        val id = fileType.getId(psiFile)
        if (Strings.isEmpty(id)) {
            return
        }

        val fullId = fileType.sourceId + "@" + id

        indexes
            .add(IndexKey(fileType.sourceId), indexes.createIndex(id, idPsiElement))
            .addReference(fullId, idPsiElement)
            .addReference(id, idPsiElement)
            .addSearchEverywhere(fullId, idPsiElement)

        fileType
            .getAdditionalReferences(id)
            .forEach(Consumer { reference: String -> indexes.addReference(reference, idPsiElement) })
    }
}
