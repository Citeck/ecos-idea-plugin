package ru.citeck.metadata

class QName(val localName: String, val prefix: String, val javaField: String, val javaClass: String) {

    companion object {
        const val CLASS = "org.alfresco.service.namespace.QName"
    }

    override fun toString(): String {
        return "$prefix:$localName"
    }
}