package ru.citeck.idea.artifacts.action.navigate.providers

import com.intellij.psi.PsiElement

data class NavigateInFileItem(
    val name: String,
    val psiElement: PsiElement
) {
    override fun toString(): String {
        return name
    }
}
