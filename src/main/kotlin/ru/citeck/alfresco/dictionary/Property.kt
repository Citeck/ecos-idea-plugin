package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import ru.citeck.EcosServer
import ru.citeck.alfresco.dictionary.constraints.Constraint
import ru.citeck.alfresco.dictionary.constraints.PropertyConstraints

class Property(
    @JsonProperty("name") val name: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("dataType") val dataType: String,
    @JsonProperty("defaultValue") val defaultValue: String?,
    @JsonProperty("multiValued") val multiValued: Boolean,
    @JsonProperty("mandatory") val mandatory: Boolean,
    @JsonProperty("enforced") val enforced: Boolean,
    @JsonProperty("protected") val protected: Boolean,
    @JsonProperty("indexed") val indexed: Boolean,
    @JsonProperty("url") val url: String,
    @JacksonInject("dictionary") private val dictionary: Dictionary,
    @JacksonInject("ecosServer") private val ecosServer: EcosServer
) {

    val constraints: List<Constraint> by lazy {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        val json = ecosServer.execute(
            path ="alfresco/service$url",
            clazz = String::class.java,
            method = "GET",
            basicAuth = true
        )
        val property = objectMapper.readValue(json, PropertyConstraints::class.java)
        return@lazy property.constraints?: listOf<Constraint>()
    }

    override fun toString(): String {
        return name
    }
}