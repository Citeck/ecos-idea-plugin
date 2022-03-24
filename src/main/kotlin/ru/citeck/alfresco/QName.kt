package ru.citeck.alfresco

class QName(
    val localName: String,
    val uri: String,
    val jField: String,
    val jClass: String
) {

    companion object {
        const val CLASS = "org.alfresco.service.namespace.QName"
    }

    override fun toString(): String {
        return "${uri ?: ""}:$localName $jClass.$jField"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QName

        if (localName != other.localName) return false
        if (uri != other.uri) return false
        if (jField != other.jField) return false
        if (jClass != other.jClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = localName.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + jField.hashCode()
        result = 31 * result + jClass.hashCode()
        return result
    }

}