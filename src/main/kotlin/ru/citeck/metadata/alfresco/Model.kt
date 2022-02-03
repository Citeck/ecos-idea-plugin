package ru.citeck.metadata.alfresco

import com.fasterxml.jackson.annotation.JsonProperty

class Model(
    @JsonProperty("name") val name: String?,
    @JsonProperty("description") val description: String?,
    @JsonProperty("author") val author: String?,
    @JsonProperty("published") val published: String?,
    @JsonProperty("version") val version: String?,
    @JsonProperty("imports") val imports: List<Import>?,
    @JsonProperty("namespaces") val namespaces: List<Namespace>?,
    @JsonProperty("types") val types: List<Type>?
)