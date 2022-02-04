package ru.citeck.indexes.qnames

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.*
import com.intellij.util.io.KeyDescriptor
import ru.citeck.indexes.BooleanKeyDescriptor
import ru.citeck.metadata.QName

class QNamesFileBasedIndex : ScalarIndexExtension<Boolean>() {

    companion object {
        val NAME = ID.create<Boolean, Void>("ru.citeck.indexes.qnames.QNamesFileBasedIndex")
    }

    private val keyDescriptor = BooleanKeyDescriptor()
    private val indexer = QnamesDataIndexer()
    private val inputFilter = QnamesInputFilter()

    override fun getName(): ID<Boolean, Void> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<Boolean, Void, FileContent> {
        return indexer
    }

    override fun getKeyDescriptor(): KeyDescriptor<Boolean> {
        return keyDescriptor
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return inputFilter
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    private class QnamesInputFilter : FileBasedIndex.InputFilter {
        override fun acceptInput(file: VirtualFile): Boolean {
            return file.extension == "java"
        }
    }

    class QnamesDataIndexer : DataIndexer<Boolean, Void, FileContent> {

        override fun map(inputData: FileContent): MutableMap<Boolean, Void?> {
            val result = mutableMapOf<Boolean, Void?>()
            val project = inputData.project ?: return result

            val text = inputData.contentAsText
            if (!(text.contains(QName.CLASS) && text.contains("QName.createQName"))) {
                return result
            }
            val qnamesService = project.getService(QNamesService::class.java)
            if (qnamesService.getQNamePsiFields(inputData.psiFile).isNotEmpty()) {
                result[true] = null
            }
            return result
        }

    }

}