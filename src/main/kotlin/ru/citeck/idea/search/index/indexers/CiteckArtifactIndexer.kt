package ru.citeck.idea.search.index.indexers

import com.intellij.util.indexing.FileContent
import ru.citeck.idea.artifacts.ArtifactInfo
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.Indexes.FileIndexes

class CiteckArtifactIndexer : CiteckFileIndexer {

    override fun accept(artifactInfo: ArtifactInfo): Boolean {
        return true
    }

    override fun map(inputData: FileContent, artifactInfo: ArtifactInfo, indexes: FileIndexes) {

        val idPsiElement = artifactInfo.getArtifactIdPsiElement() ?: return
        val artifactId = artifactInfo.getArtifactId()

        if (artifactId.isBlank()) {
            return
        }

        val publicSrcId = artifactInfo.getMeta().publicSourceId
        val publicRef = "$publicSrcId@$artifactId"

        indexes.add(IndexKey(publicSrcId), indexes.createIndex(artifactId, idPsiElement))
        indexes.add(
            IndexKey(IndexKey.ARTIFACT_TYPE, artifactInfo.getTypeId()),
            indexes.createIndex(publicRef, idPsiElement)
        )

        indexes.addReference(publicRef, idPsiElement)
        indexes.addReference(artifactId, idPsiElement)

        indexes.addSearchEverywhere(publicRef, idPsiElement)
    }
}
