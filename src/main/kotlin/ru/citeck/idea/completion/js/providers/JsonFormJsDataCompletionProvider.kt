package ru.citeck.idea.completion.js.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.lang.javascript.JSElementTypes
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import icons.Icons
import ru.citeck.idea.artifacts.ArtifactTypes.getFormComponents
import ru.citeck.idea.artifacts.ArtifactTypes.isForm
import ru.citeck.idea.completion.js.JsCompletionProvider
import ru.citeck.idea.utils.CiteckPsiUtils
import java.util.*
import java.util.stream.Stream

class JsonFormJsDataCompletionProvider : JsCompletionProvider {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val injectedInFile = getInjectedInFile(parameters) ?: return

        if (!isForm(injectedInFile)) {
            return
        }

        val psiElement = parameters.position
        val referenceExpression = PsiTreeUtil.getParentOfType(
            psiElement,
            JSReferenceExpression::class.java
        )
        if (referenceExpression == null) {
            return
        }

        val qualifier = Optional
            .ofNullable(referenceExpression.qualifier)
            .map { obj: JSExpression -> obj.text }
            .orElse(null)

        if (qualifier == null) {
            return
        }

        val qualifiers = qualifier.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if ("data" != qualifiers[0] || qualifiers.size > 2) {
            return
        }

        if (qualifiers.size == 1) {
            getInputComponents(injectedInFile, null)
                .map { component: JsonObject -> getProperties(component, "key", "type") }
                .forEach { properties: Map<String, String> ->
                    addElement(result, properties["key"], properties["type"])
                }
        } else {
            getInputComponents(injectedInFile, qualifiers[1])
                .flatMap { component: JsonObject -> this.getAsyncDataComponentKeys(component) }
                .forEach { key: String? -> addElement(result, key, null) }
        }
    }

    private fun addElement(result: CompletionResultSet, key: String?, type: String?) {
        if (key == null) {
            return
        }
        var elementBuilder = LookupElementBuilder
            .create(key)
            .withIcon(Icons.CiteckLogo)
        if (type != null) {
            elementBuilder = elementBuilder.withTypeText(type)
        }
        result.addElement(elementBuilder)
    }

    private fun getAsyncDataComponentKeys(component: JsonObject): Stream<String?> {
        if ("asyncData" != getProperty(component, "type")) {
            return Stream.of()
        }

        return Optional
            .of(component)
            .map { c: JsonObject -> c.findProperty("source") }
            .map { obj: JsonProperty? -> obj!!.value }
            .filter { obj: JsonValue? ->
                JsonObject::class.java.isInstance(
                    obj
                )
            }
            .map { obj: JsonValue? ->
                JsonObject::class.java.cast(
                    obj
                )
            }
            .stream()
            .flatMap { source: JsonObject ->
                Stream
                    .of(
                        "recordsArray",
                        "record",
                        "recordsQuery"
                    )
                    .map { name: String? -> source.findProperty(name!!) }
                    .filter { obj: JsonProperty? -> Objects.nonNull(obj) }
            }
            .map { obj: JsonProperty? -> obj!!.value }
            .filter { obj: JsonValue? -> JsonObject::class.java.isInstance(obj) }
            .map { obj: JsonValue? -> JsonObject::class.java.cast(obj) }
            .map { attributesContainer: JsonObject -> attributesContainer.findProperty("attributes") }
            .filter { obj: JsonProperty? -> Objects.nonNull(obj) }
            .map { obj: JsonProperty? -> obj!!.value }
            .filter { obj: JsonValue? -> JsonObject::class.java.isInstance(obj) }
            .map { obj: JsonValue? -> JsonObject::class.java.cast(obj) }
            .flatMap { attributes: JsonObject -> attributes.propertyList.stream() }
            .map { obj: JsonProperty -> obj.name }
    }

    private fun getInputComponents(psiFile: PsiFile, key: String?): Stream<JsonObject> {
        return getFormComponents(psiFile)
            .stream()
            .filter { component: JsonObject -> "true" == getProperty(component, "input") }
            .filter { component: JsonObject -> key == null || key == getProperty(component, "key") }
    }

    private fun getProperties(jsonObject: JsonObject, vararg property: String): Map<String, String> {
        return CiteckPsiUtils.getProperties(jsonObject, *property)
    }

    private fun getProperty(jsonObject: JsonObject, property: String): String {
        return CiteckPsiUtils.getProperty(jsonObject, property)
    }

    override fun getElementPattern(): ElementPattern<out PsiElement?> {
        return PlatformPatterns.psiElement()
            .withParent(
                PlatformPatterns.psiElement(JSElementTypes.REFERENCE_EXPRESSION)
            )
            .afterLeaf(".", "?.")
    }
}
