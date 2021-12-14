package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.annotation.JsonProperty

class AssociationType(
    @JsonProperty("class") val clazz: String,
    @JsonProperty("mandatory") val mandatory: Boolean,
    @JsonProperty("role") val role: String?,
    @JsonProperty("many") val many: Boolean
)