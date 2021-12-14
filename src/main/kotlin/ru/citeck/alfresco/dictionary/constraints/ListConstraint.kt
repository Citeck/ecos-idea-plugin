package ru.citeck.alfresco.dictionary.constraints

class ListConstraint() : Constraint(type = "LIST") {
    val allowedValues: List<String> get() = parameters["allowedValues"]?.map { it.textValue() } ?: listOf()
    val sorted: Boolean get() = parameters["sorted"]?.asBoolean() ?: false
    val caseSensitive: Boolean get() = parameters["caseSensitive"]?.asBoolean() ?: false
}