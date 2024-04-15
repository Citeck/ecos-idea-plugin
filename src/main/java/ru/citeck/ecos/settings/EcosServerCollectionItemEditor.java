package ru.citeck.ecos.settings;

import com.intellij.util.ui.CollectionItemEditor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class EcosServerCollectionItemEditor implements CollectionItemEditor<EcosServer> {

    @Override
    public @NotNull Class<? extends EcosServer> getItemClass() {
        return EcosServer.class;
    }

    @Override
    public EcosServer clone(@NotNull EcosServer item, boolean forInPlaceEditing) {
        EcosServer clone = item.clone();
        clone.setId(UUID.randomUUID().toString());
        return clone;
    }

}
