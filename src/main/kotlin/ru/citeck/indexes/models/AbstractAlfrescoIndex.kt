package ru.citeck.indexes.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput


abstract class AbstractAlfrescoIndex<T>(clazz: Class<T>) : FileBasedIndexExtension<String, T>(), DocumentChangeDependentIndex {

    companion object {
        val MODEL_NAMESPACE = "http://www.alfresco.org/model/dictionary/1.0"
    }

    private val inputFilter = ModelsInputFilter()
    private val valueExternalizer = AlfDataExternalizer(clazz)
    private val dataIndexer = AlfDataIndexer(::map)

    abstract override fun getName(): ID<String, T>

    abstract fun map(inputData: FileContent): MutableMap<String, T>

    override fun getIndexer(): DataIndexer<String, T, FileContent> {
        return dataIndexer
    }

    override fun getValueExternalizer(): DataExternalizer<T> {
        return valueExternalizer
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getVersion(): Int {
        return 1
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return inputFilter
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    private class AlfDataExternalizer<T>(val clazz: Class<T>) : DataExternalizer<T> {

        private val objectMapper = ObjectMapper()

        override fun save(out: DataOutput, value: T) {
            out.writeUTF(objectMapper.writeValueAsString(value))
        }

        override fun read(`in`: DataInput): T {
            return objectMapper.readValue(`in`.readUTF(), clazz)
        }

    }

    private class AlfDataIndexer<T>(val indexer: (inputData: FileContent) -> MutableMap<String, T>) :
        DataIndexer<String, T, FileContent> {
        override fun map(inputData: FileContent): MutableMap<String, T> {
            return indexer.invoke(inputData)
        }
    }

    private class ModelsInputFilter : FileBasedIndex.InputFilter {
        override fun acceptInput(file: VirtualFile): Boolean {
            val path = file.parent?.path ?: return false
            val isModel = file.extension == "xml" && path.endsWith("/model")
            if (!isModel) return false
            if (path.contains(".jar!/") && !path.contains("-sources.jar!")) return false
            return true
        }
    }


}