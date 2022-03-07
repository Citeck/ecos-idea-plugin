package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Type(
    @JsonProperty("name") val name: String,
    @JsonProperty("prefix") val prefix: String,
    @JsonProperty("parent") val parent: String?,
    @JsonProperty("properties") val properties: List<Property>,
    @JsonProperty("psiElementOffset") val psiElementOffset: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Type

        if (name != other.name) return false
        if (prefix != other.prefix) return false
        if (parent != other.parent) return false
        if (properties != other.properties) return false
        if (psiElementOffset != other.psiElementOffset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + prefix.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + properties.hashCode()
        result = 31 * result + psiElementOffset
        return result
    }

}
