package ru.citeck.alfresco.dictionary.constraints

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.JsonNode
import ru.citeck.ui.ecos.EcosComponent

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = Constraint::class
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ListConstraint::class, name = "LIST"),
    JsonSubTypes.Type(value = RegexConstraint::class, name = "REGEX"),
    JsonSubTypes.Type(value = LengthConstraint::class, name = "LENGTH"),
    JsonSubTypes.Type(value = MinMaxConstraint::class, name = "MINMAX")
)
open class Constraint(@JsonProperty("type") type: String) {

    val parameters: Map<String, JsonNode> = HashMap()

    @JsonSetter("parameters")
    private fun parameters(parameters: List<JsonNode>) {
        parameters.forEach { jsonNode ->
            jsonNode.fieldNames().forEach { field ->
                (this.parameters as HashMap)[field] = jsonNode[field]
            }
        }
    }

    open fun applyConstraint(component: EcosComponent) {}

}

