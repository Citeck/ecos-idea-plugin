package ru.citeck.livetemplate.functions

import com.intellij.codeInsight.lookup.LookupElement
import ru.citeck.EcosServer
import ru.citeck.alfresco.dictionary.Dictionary

typealias EcosEnumLookupEvaluator = () -> Array<LookupElement>

enum class EcosEnumVariant(val type: String, val evaluator: EcosEnumLookupEvaluator) {

    ECOS_TYPE("ECOS_TYPE", { Dictionary.get(EcosServer.current()).ecosTypesLookupArray }),
    TYPE("TYPE", { Dictionary.get(EcosServer.current()).typesLookupArray });

    val lookupElements get() = evaluator.invoke()

}