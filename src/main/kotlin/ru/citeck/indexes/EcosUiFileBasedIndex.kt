package ru.citeck.indexes

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.KeyDescriptor
import ru.citeck.deployment.EcosUiFileType
import java.io.DataInput
import java.io.DataOutput

class EcosUiFileBasedIndex : FileBasedIndexExtension<EcosUiFileType, String>(), DocumentChangeDependentIndex {

    companion object {
        val NAME = ID.create<EcosUiFileType, String>("ru.citeck.indexes.EcosUiFileBasedIndex")
    }

    private val indexer = EcosUiDataIndexer()
    private val keyDescriptor = EcosUiKeyDescriptor()
    private val valueExternalizer = StringDataExternalizer()
    private val inputFilter = EcosUiInputFilter()

    override fun getName(): ID<EcosUiFileType, String> {
        return NAME
    }

    override fun getIndexer(): DataIndexer<EcosUiFileType, String, FileContent> {
        return indexer
    }

    override fun getKeyDescriptor(): KeyDescriptor<EcosUiFileType> {
        return keyDescriptor
    }

    override fun getValueExternalizer(): DataExternalizer<String> {
        return valueExternalizer
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

    private class EcosUiDataIndexer : DataIndexer<EcosUiFileType, String, FileContent> {

        override fun map(inputData: FileContent): MutableMap<EcosUiFileType, String> {
            val id = (inputData.psiFile as PsiFileImpl)
                .findChildByClass(JsonObject::class.java)?.findProperty("id")?.value?.text ?: return mutableMapOf()
            return mutableMapOf(
                EcosUiFileType.values().first { it.accept(inputData.file) } to JsonPsiUtil.stripQuotes(id)
            )
        }

    }

    private class EcosUiKeyDescriptor : KeyDescriptor<EcosUiFileType> {

        override fun getHashCode(value: EcosUiFileType): Int {
            return value.name.hashCode()
        }

        override fun isEqual(val1: EcosUiFileType?, val2: EcosUiFileType): Boolean {
            return val1 == val2
        }

        override fun save(out: DataOutput, value: EcosUiFileType) {
            out.writeUTF(value.name)
        }

        override fun read(`in`: DataInput): EcosUiFileType {
            return EcosUiFileType.valueOf(`in`.readUTF())
        }
    }

    private class EcosUiInputFilter : FileBasedIndex.InputFilter {
        override fun acceptInput(file: VirtualFile): Boolean {
            if (file.extension != "json") {
                return false
            }
            return EcosUiFileType.values().firstOrNull { it.accept(file) } != null
        }
    }

}