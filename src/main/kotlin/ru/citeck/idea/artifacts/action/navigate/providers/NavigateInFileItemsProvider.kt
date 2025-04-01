package ru.citeck.idea.artifacts.action.navigate.providers

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiFile

interface NavigateInFileItemsProvider {

    companion object {
        val EP_NAME: ExtensionPointName<NavigateInFileItemsProvider> =
            ExtensionPointName.create("ru.citeck.idea.navigateInFileItemsProvider")
    }

    fun getItems(psiFile: PsiFile?): List<NavigateInFileItem>?
}
