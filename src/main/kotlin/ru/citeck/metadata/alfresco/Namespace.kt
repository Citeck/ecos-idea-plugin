package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Namespace(@JsonProperty("uri") val uri: String, @JsonProperty("prefix") val prefix: String)
