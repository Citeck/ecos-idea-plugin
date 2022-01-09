package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Import(
    @JsonProperty("uri") val uri: String,
    @JsonProperty("prefix") val prefix: String
)
