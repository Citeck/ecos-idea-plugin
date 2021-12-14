package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.annotation.JsonProperty

class Reference(
    @JsonProperty("name") val name: String?,
    @JsonProperty("title") val title: String?,
    @JsonProperty("url") val url: String?
)