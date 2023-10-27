package ru.citeck.ecos.livetemplates.functions.alfresco;

import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.Collections;
import java.util.List;

public class AlfrescoPropertyEnumMacro extends AlfrescoEnumMacro {

    private static final List<IndexKey> INDEX_KEYS = Collections.singletonList(
        new IndexKey(AlfrescoModelIndexer.PROPERTY)
    );

    @Override
    public List<IndexKey> getIndexKeys() {
        return INDEX_KEYS;
    }

    @Override
    public String getName() {
        return "alfresco_property";
    }

    @Override
    public String getPresentableName() {
        return "alfresco_property()";
    }

}
