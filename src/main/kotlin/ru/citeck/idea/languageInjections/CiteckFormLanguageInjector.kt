package ru.citeck.idea.languageInjections

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.utils.CiteckPsiUtils
import java.util.*

class CiteckFormLanguageInjector : MultiHostInjector {

    companion object {
        private val ELEMENTS_TO_INJECT_IN: List<Class<out PsiElement>> = listOf(JsonObject::class.java)
    }

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {

        if (!ArtifactTypes.isForm(context.containingFile) || context !is JsonObject) {
            return
        }

        val componentType = CiteckPsiUtils.getProperty(context, "type")
        if (componentType.isEmpty()) {
            return
        }

        CiteckFormLanguageInjection
            .EP_NAME
            .extensionsIfPointIsRegistered
            .asSequence()
            .filter { p -> "*" == p.componentType || p.componentType == componentType }
            .mapNotNull { getJsonValueLiteralByPath(context, it.path) }
            .forEach { literal: JsonStringLiteral -> inject(registrar, literal) }
    }

    private fun inject(registrar: MultiHostRegistrar, literal: JsonStringLiteral) {
        registrar.startInjecting(JavascriptLanguage)
        registrar.addPlace(null, null, literal as PsiLanguageInjectionHost, TextRange(1, literal.getTextLength() - 1))
        registrar.doneInjecting()
    }

    private fun getJsonValueLiteralByPath(jsonObject: JsonObject, path: String): JsonStringLiteral? {
        if (!path.contains("/")) {
            return Optional
                .ofNullable(jsonObject.findProperty(path))
                .map { obj: JsonProperty -> obj.value }
                .filter { obj: JsonValue? -> JsonStringLiteral::class.java.isInstance(obj) }
                .map { obj: JsonValue? -> JsonStringLiteral::class.java.cast(obj) }
                .orElse(null)
        }
        val index = path.indexOf("/")
        val property = path.substring(0, index)
        return Optional
            .ofNullable(jsonObject.findProperty(property))
            .map { obj: JsonProperty -> obj.value }
            .filter { obj: JsonValue? -> JsonObject::class.java.isInstance(obj) }
            .map { obj: JsonValue? -> JsonObject::class.java.cast(obj) }
            .map { nested: JsonObject ->
                getJsonValueLiteralByPath(
                    nested,
                    path.substring(index + 1)
                )
            }
            .orElse(null)
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement?>?> {
        return ELEMENTS_TO_INJECT_IN
    }
}
