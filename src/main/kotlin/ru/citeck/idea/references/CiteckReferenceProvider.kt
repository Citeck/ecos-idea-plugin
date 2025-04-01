package ru.citeck.idea.references

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.IndexesService

class CiteckReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {

        val value = PsiElementValueResolver.EP_NAME
            .extensionsIfPointIsRegistered
            .asSequence()
            .map { it.getPsiElementValue(element) }
            .filterIsInstance<String>()
            .firstOrNull()

        if (value == null) {
            return PsiReference.EMPTY_ARRAY
        }

        return IndexesService.getInstance(element.project)
            .list(IndexKey(IndexKey.REFERENCE_TYPE, value))
            .map { CiteckReference(element, it) }
            .toTypedArray()
    }

    class Contributor : PsiReferenceContributor() {
        override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
            val provider = CiteckReferenceProvider()
            PsiElementValueResolver.EP_NAME
                .extensionsIfPointIsRegistered
                .forEach { psiElementValueResolver ->
                    registrar.registerReferenceProvider(
                        PlatformPatterns.psiElement(psiElementValueResolver.getPsiElementType()),
                        provider
                    )
                }
        }
    }
}
