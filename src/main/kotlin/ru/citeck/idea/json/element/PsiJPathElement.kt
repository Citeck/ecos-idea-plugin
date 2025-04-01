package ru.citeck.idea.json.element

import com.intellij.psi.PsiElement

interface PsiJPathElement {

    fun getByKey(key: String): PsiJPathElement?

    fun getChildren(from: Int, to: Int): List<PsiJPathElement>

    fun asText(): String

    fun getPsiElement(): PsiElement
}
