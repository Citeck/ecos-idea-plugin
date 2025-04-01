package ru.citeck.idea.references

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement

interface PsiElementValueResolver<T : PsiElement> {

    companion object {
        val EP_NAME: ExtensionPointName<PsiElementValueResolver<*>> =
            ExtensionPointName.create("ru.citeck.idea.psiElementValueResolver")
    }

    fun getPsiElementType(): Class<T>

    fun getValue(psiElement: T): Any?

    fun getPsiElementValue(psiElement: PsiElement): Any? {
        if (getPsiElementType().isInstance(psiElement)) {
            @Suppress("UNCHECKED_CAST")
            return getValue(psiElement as T)
        }
        return null
    }
}
