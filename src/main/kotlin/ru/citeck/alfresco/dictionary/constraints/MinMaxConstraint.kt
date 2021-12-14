package ru.citeck.alfresco.dictionary.constraints

class MinMaxConstraint() : Constraint(type = "MINMAX") {
    val minValue: Int get() = parameters["minValue"]?.asInt() ?: Int.MIN_VALUE
    val maxValue: Int get() = parameters["maxValue"]?.asInt() ?: Int.MAX_VALUE
}