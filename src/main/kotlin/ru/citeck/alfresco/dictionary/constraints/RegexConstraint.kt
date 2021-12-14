package ru.citeck.alfresco.dictionary.constraints

class RegexConstraint() : Constraint(type = "REGEX") {
    val requiresMatch: Boolean get() = parameters["requiresMatch"]?.asBoolean() ?: false
    val expression: String get() = parameters["expression"]?.asText() ?: ""
}