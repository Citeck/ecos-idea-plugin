package ru.citeck.idea.settings.servers

import com.intellij.util.ui.CollectionItemEditor
import java.util.*

internal class EcosServerCollectionItemEditor : CollectionItemEditor<CiteckServer.Builder> {

    override fun getItemClass(): Class<out CiteckServer.Builder> {
        return CiteckServer.Builder::class.java
    }

    override fun clone(item: CiteckServer.Builder, forInPlaceEditing: Boolean): CiteckServer.Builder {
        return item.build().copy().withId(UUID.randomUUID().toString())
    }
}
