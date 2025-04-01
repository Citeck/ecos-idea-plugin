package ru.citeck.idea.search.index

import com.intellij.util.indexing.ID

object CiteckIndexConstants {

    val INDEX_NAME: ID<IndexKey, List<IndexValue>> = ID.create("ru.citeck.indexes.CiteckIndexExtension")
}
