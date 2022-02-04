package ru.citeck.indexes

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput

class StringDataExternalizer: DataExternalizer<String> {
    override fun save(out: DataOutput, value: String) {
        out.writeUTF(value)
    }

    override fun read(`in`: DataInput): String {
        return `in`.readUTF()
    }
}