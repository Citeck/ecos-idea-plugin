package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Namespace(
    @JsonProperty("uri") val uri: String,
    @JsonProperty("prefix") val prefix: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Namespace

        if (uri != other.uri) return false
        if (prefix != other.prefix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + prefix.hashCode()
        return result
    }

}
