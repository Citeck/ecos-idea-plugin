package ru.citeck.idea.search.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.util.indexing.FileContent
import lombok.Data

class Indexes : HashMap<IndexKey, MutableList<IndexValue>>() {

    fun withFileContent(fileContent: FileContent): FileIndexes {
        return FileIndexes(fileContent.file)
    }

    inner class FileIndexes(
        private val virtualFile: VirtualFile
    ) {

        fun addSearchEverywhere(id: String, psiElement: PsiElement, properties: Map<String, String>?): FileIndexes {
            return add(IndexKey.SEARCH_EVERYWHERE, id, psiElement, properties)
        }

        fun addSearchEverywhere(id: String, psiElement: PsiElement): FileIndexes {
            return add(IndexKey.SEARCH_EVERYWHERE, id, psiElement, null)
        }

        fun addReference(id: String, psiElement: PsiElement, properties: Map<String, String>?): FileIndexes {
            return add(IndexKey(IndexKey.REFERENCE_TYPE, id), id, psiElement, properties)
        }

        fun addReference(id: String, psiElement: PsiElement): FileIndexes {
            return add(IndexKey(IndexKey.REFERENCE_TYPE, id), id, psiElement, null)
        }

        fun add(key: IndexKey, id: String, psiElement: PsiElement, properties: Map<String, String>?): FileIndexes {
            return add(key, createIndex(id, psiElement, properties))
        }

        fun add(key: IndexKey, id: String, psiElement: PsiElement): FileIndexes {
            return add(key, createIndex(id, psiElement, null))
        }

        fun add(key: IndexKey, indexValue: IndexValue): FileIndexes {
            var values: MutableList<IndexValue>? = get(key)
            if (values == null) {
                values = ArrayList()
                put(key, values)
            }
            values.add(indexValue)
            return this
        }

        fun createIndex(id: String, psiElement: PsiElement, properties: Map<String, String>?): IndexValue {
            val indexValue = IndexValue(id, psiElement.textOffset, virtualFile.path)
            properties?.forEach { (property: String, value: String) ->
                indexValue.setProperty(property, value)
            }
            return indexValue
        }

        fun createIndex(id: String, psiElement: PsiElement): IndexValue {
            return createIndex(id, psiElement, null)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FileIndexes

            return virtualFile == other.virtualFile
        }

        override fun hashCode(): Int {
            return virtualFile.hashCode()
        }

        override fun toString(): String {
            return "FileIndexes(virtualFile=$virtualFile)"
        }
    }
}
