package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Type(
    @JsonProperty("name") val name: String
)
