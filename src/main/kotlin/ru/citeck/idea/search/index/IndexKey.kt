package ru.citeck.idea.search.index

import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

data class IndexKey(
    val type: String,
    val id: String
) {

    companion object {
        const val SEARCH_EVERYWHERE_TYPE: String = "search-everywhere"
        const val REFERENCE_TYPE: String = "reference"
        const val ARTIFACT_TYPE: String = "artifact-type"
        const val ALL: String = "*"
        val SEARCH_EVERYWHERE: IndexKey = IndexKey(SEARCH_EVERYWHERE_TYPE, ALL)
    }

    constructor(type: String) : this(type, ALL)

    class Descriptor : KeyDescriptor<IndexKey> {
        override fun getHashCode(key: IndexKey): Int {
            return key.hashCode()
        }

        override fun isEqual(key1: IndexKey, key2: IndexKey): Boolean {
            return key1 == key2
        }

        override fun save(output: DataOutput, key: IndexKey) {
            output.writeUTF(key.type)
            output.writeUTF(key.id)
        }

        override fun read(input: DataInput): IndexKey {
            return IndexKey(
                input.readUTF(),
                input.readUTF()
            )
        }
    }
}
