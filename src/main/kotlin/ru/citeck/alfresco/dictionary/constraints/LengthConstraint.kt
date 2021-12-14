package ru.citeck.alfresco.dictionary.constraints

class LengthConstraint() : Constraint(type = "LENGTH") {
    val minLength: Int get() = parameters["minLength"]?.asInt() ?: 0
    val maxLength: Int get() = parameters["maxLength"]?.asInt() ?: Int.MAX_VALUE
}