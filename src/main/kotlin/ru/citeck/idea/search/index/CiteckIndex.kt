package ru.citeck.idea.search.index

import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
import ru.citeck.idea.artifacts.ArtifactsService
import ru.citeck.idea.search.index.indexers.CiteckFileIndexer

class CiteckIndex : FileBasedIndexExtension<IndexKey, List<IndexValue>>() {

    private val dataExternalizer: DataExternalizer<List<IndexValue>> = IndexValue.Externalizer()
    private val keyDescriptor: KeyDescriptor<IndexKey> = IndexKey.Descriptor()

    override fun getName(): ID<IndexKey, List<IndexValue>> {
        return CiteckIndexConstants.INDEX_NAME
    }

    override fun getIndexer(): DataIndexer<IndexKey, List<IndexValue>, FileContent> {
        return DataIndexer { inputData: FileContent -> this.map(inputData) }
    }

    private fun map(inputData: FileContent): Map<IndexKey, List<IndexValue>> {
        val indexes = Indexes()
        if (inputData.file.fileType.isBinary) {
            return indexes
        }

        val artifactsService = ArtifactsService.getInstance()
        val artifactInfo = artifactsService.getArtifactInfo(inputData.file, inputData.project) ?: return indexes
        if (!artifactInfo.getController().isIndexable(inputData.psiFile)) {
            return indexes
        }

        CiteckFileIndexer.EP_NAME
            .extensionsIfPointIsRegistered
            .stream()
            .filter { it.accept(artifactInfo) }
            .forEach { it.map(inputData, artifactInfo, indexes.withFileContent(inputData)) }

        return indexes
    }

    override fun getKeyDescriptor(): KeyDescriptor<IndexKey> {
        return keyDescriptor
    }

    override fun getValueExternalizer(): DataExternalizer<List<IndexValue>> {
        return dataExternalizer
    }

    override fun getVersion(): Int {
        return 4
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return FileBasedIndex.InputFilter { true }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }
}
