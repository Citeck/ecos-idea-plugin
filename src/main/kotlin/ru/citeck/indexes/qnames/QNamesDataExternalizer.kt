package ru.citeck.indexes.qnames

import com.intellij.util.io.DataExternalizer
import ru.citeck.alfresco.QName
import java.io.DataInput
import java.io.DataOutput

class QNamesDataExternalizer : DataExternalizer<List<QName>> {

    override fun save(out: DataOutput, value: List<QName>) {
        out.writeInt(value.size)
        value.forEach { qName ->
            out.writeUTF(qName.localName)
            out.writeUTF(qName.uri)
            out.writeUTF(qName.jField)
            out.writeUTF(qName.jClass)
        }
    }

    override fun read(input: DataInput): List<QName> {

        val size = input.readInt()
        val list = mutableListOf<QName>()

        for (i in 0 until size) {
            val localName = input.readUTF()
            val prefix = input.readUTF()
            val jField = input.readUTF()
            val jClass = input.readUTF()
            list.add(QName(localName, prefix, jField, jClass))
        }

        return list

    }
}