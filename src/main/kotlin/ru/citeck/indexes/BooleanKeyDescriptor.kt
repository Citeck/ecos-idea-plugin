package ru.citeck.indexes

import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

class BooleanKeyDescriptor : KeyDescriptor<Boolean> {
    override fun getHashCode(value: Boolean): Int {
        return if (value) 1 else 0
    }

    override fun isEqual(val1: Boolean, val2: Boolean): Boolean {
        return val1 == val2
    }

    override fun save(out: DataOutput, value: Boolean) {
        out.writeBoolean(value)
    }

    override fun read(`in`: DataInput): Boolean {
        return `in`.readBoolean()
    }
}