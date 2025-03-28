package ru.citeck.idea.search.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.xml.XmlTokenImpl
import com.intellij.psi.util.PsiUtil
import com.intellij.util.io.DataExternalizer
import icons.Icons
import ru.citeck.idea.utils.CiteckVirtualFileUtils.getFileByPath
import java.io.DataInput
import java.io.DataOutput
import javax.swing.Icon

data class IndexValue(
    val id: String,
    val offset: Int,
    val file: String
) {

    companion object {
        private val DEFAULT_ICON = Icons.CiteckLogo
        private val ICONS: Map<String, Icon> = java.util.Map.of()
    }

    private var properties: MutableMap<String, String>? = null

    fun getProperty(property: String): String? {
        return properties?.get(property)
    }

    fun setProperty(property: String, value: String): IndexValue {
        val props = properties ?: run {
            val newProps = HashMap<String, String>()
            properties = newProps
            newProps
        }
        props[property] = value
        return this
    }

    fun getPsiElement(project: Project): PsiElement? {
        val virtualFile = getFileByPath(file) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null
        if (offset == 0) {
            return psiFile
        }
        val element = PsiUtil.getElementAtOffset(psiFile, offset)
        if (element is XmlTokenImpl) {
            return element.getParent()
        }
        return element
    }

    fun getIcon(): Icon {
        val icon = getProperty("icon")
        if (icon != null) {
            return ICONS.getOrDefault(icon, DEFAULT_ICON)
        }
        return DEFAULT_ICON
    }

    class Externalizer : DataExternalizer<List<IndexValue>> {

        override fun save(out: DataOutput, indexValues: List<IndexValue>) {
            out.writeInt(indexValues.size)
            for (indexValue in indexValues) {
                out.writeUTF(indexValue.id)
                out.writeInt(indexValue.offset)
                out.writeUTF(indexValue.file)
                val properties: Map<String, String>? = indexValue.properties
                if (properties == null) {
                    out.writeInt(0)
                } else {
                    out.writeInt(properties.size)
                    properties.forEach { (k, v) ->
                        out.writeUTF(k)
                        out.writeUTF(v)
                    }
                }
            }
        }

        override fun read(input: DataInput): List<IndexValue> {
            val indexValues: MutableList<IndexValue> = ArrayList()
            val size = input.readInt()
            for (counter in 0..<size) {
                val indexValue = IndexValue(
                    input.readUTF(),
                    input.readInt(),
                    input.readUTF()
                )
                val propSize = input.readInt()
                for (i in 0..< propSize) {
                    indexValue.setProperty(input.readUTF(), input.readUTF())
                }
                indexValues.add(indexValue)
            }
            return indexValues
        }
    }
}
