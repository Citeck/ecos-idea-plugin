package ru.citeck.idea.languageInjections

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.xmlb.annotations.Attribute
import lombok.Data

@Data
class CiteckFormLanguageInjection {

    companion object {
        val EP_NAME: ExtensionPointName<CiteckFormLanguageInjection> =
            ExtensionPointName.create("ru.citeck.idea.citeckFormLanguageInjection")
    }

    @Attribute("componentType")
    lateinit var componentType: String

    @Attribute("path")
    lateinit var path: String
}
