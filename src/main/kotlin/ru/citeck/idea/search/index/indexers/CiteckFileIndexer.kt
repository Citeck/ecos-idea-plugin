package ru.citeck.idea.search.index.indexers

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.indexing.FileContent
import ru.citeck.idea.artifacts.ArtifactInfo
import ru.citeck.idea.search.index.Indexes.FileIndexes

interface CiteckFileIndexer {

    companion object {
        val EP_NAME: ExtensionPointName<CiteckFileIndexer> = ExtensionPointName.create("ru.citeck.idea.fileIndexer")
    }

    fun accept(artifactInfo: ArtifactInfo): Boolean

    fun map(inputData: FileContent, artifactInfo: ArtifactInfo, indexes: FileIndexes)
}
