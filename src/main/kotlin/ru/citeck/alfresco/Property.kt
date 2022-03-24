package ru.citeck.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Property(
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String
)