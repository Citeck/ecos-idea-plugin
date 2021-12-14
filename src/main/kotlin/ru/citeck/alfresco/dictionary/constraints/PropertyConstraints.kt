package ru.citeck.alfresco.dictionary.constraints

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class PropertyConstraints(@JsonProperty("constraints") val constraints: List<Constraint>?)