package ru.citeck.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Association(
    @JsonProperty("name") val name: String,
    @JsonProperty("target") val target: String
) {
}