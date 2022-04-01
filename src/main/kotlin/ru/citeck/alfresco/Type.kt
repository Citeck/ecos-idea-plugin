package ru.citeck.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Type(
    @JsonProperty("name") val name: String,
    @JsonProperty("prefix") val prefix: String,
    @JsonProperty("parent") val parent: String?,
    @JsonProperty("psiElementOffset") val psiElementOffset: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Type
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}
