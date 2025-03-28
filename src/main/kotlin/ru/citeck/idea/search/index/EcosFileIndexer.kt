package ru.citeck.idea.search.index

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.indexing.FileContent
import ru.citeck.idea.files.FileType
import ru.citeck.idea.files.FileTypeService
import ru.citeck.idea.search.index.Indexes.FileIndexes

interface EcosFileIndexer {

    companion object {
        val EP_NAME: ExtensionPointName<EcosFileIndexer> = ExtensionPointName.create("ru.citeck.idea.fileIndexer")
    }

    fun accept(fileType: FileType): Boolean

    fun map(inputData: FileContent, indexes: FileIndexes)

    fun getFileType(inputData: FileContent): FileType {
        return FileTypeService.getInstance()
            .getFileType(inputData.file, inputData.project)
    }
}
