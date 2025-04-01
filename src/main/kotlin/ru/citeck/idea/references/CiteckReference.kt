package ru.citeck.idea.references

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import ru.citeck.idea.search.index.IndexValue

class CiteckReference(
    element: PsiElement,
    private val indexValue: IndexValue
) : PsiReferenceBase<PsiElement>(element) {

    override fun resolve(): PsiElement? {
        return indexValue.getPsiElement(element.project)
    }
}
