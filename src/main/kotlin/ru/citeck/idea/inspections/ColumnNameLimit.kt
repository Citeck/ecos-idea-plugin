package ru.citeck.idea.inspections

object ColumnNameLimit {

    /**
     * Portable column name limit of the platform: the minimum over the storage backends
     * a model may be deployed to. Today it is PostgreSQL's NAMEDATALEN - 1: longer identifiers
     * are silently truncated by the database, and quoting does not help.
     */
    const val MAX_BYTES = 63

    fun byteLength(name: String): Int {
        return name.toByteArray(Charsets.UTF_8).size
    }
}
