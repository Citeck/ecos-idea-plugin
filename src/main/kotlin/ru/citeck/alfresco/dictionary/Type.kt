package ru.citeck.alfresco.dictionary

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty


class Type(
    @JsonProperty("name") val name: String,
    @JsonProperty("isAspect") val isAspect: Boolean,
    @JsonProperty("isContainer") val isContainer: Boolean,
    @JsonProperty("title") val title: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("parent") val parent: Reference,
    @JsonProperty("defaultAspects") val defaultAspects: Map<String, Reference>,
    @JsonProperty("properties") private val propertiesMap: Map<String, Property>,
    @JsonProperty("associations") val associations: Map<String, Association>,
    @JsonProperty("childassociations") val childAssociations: Map<String, Association>,
    @JsonProperty("url") val url: String,
    @JacksonInject("dictionary") val dictionary: Dictionary
) {

    val properties : Map<String, Property> by lazy {
        val map = HashMap<String, Property>()
        map.putAll(propertiesMap)
        defaultAspects.keys.forEach {
            val aspect = dictionary.aspects[it]
            aspect?.properties?.forEach { kv -> map[kv.key] = kv.value }
        }
        return@lazy map
    }

    val ecosType: String? get() {
        return properties["etype:type"]?.defaultValue
    }

    override fun toString(): String {
        return name
    }
}