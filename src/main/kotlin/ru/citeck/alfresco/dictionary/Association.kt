package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.annotation.JsonProperty

class Association(
    @JsonProperty("name") val name: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("source") val source: AssociationType,
    @JsonProperty("target") val target: AssociationType
) {
}