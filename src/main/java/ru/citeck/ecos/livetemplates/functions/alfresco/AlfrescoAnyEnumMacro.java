package ru.citeck.ecos.livetemplates.functions.alfresco;

import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.List;

public class AlfrescoAnyEnumMacro extends AlfrescoEnumMacro {

    private static final List<IndexKey> INDEX_KEYS = List.of(
            new IndexKey(AlfrescoModelIndexer.TYPE),
            new IndexKey(AlfrescoModelIndexer.PROPERTY),
            new IndexKey(AlfrescoModelIndexer.ASPECT),
            new IndexKey(AlfrescoModelIndexer.ASSOCIATION),
            new IndexKey(AlfrescoModelIndexer.CHILD_ASSOCIATION)
    );

    @Override
    public List<IndexKey> getIndexKeys() {
        return INDEX_KEYS;
    }

    @Override
    public String getName() {
        return "alfresco_any";
    }

    @Override
    public String getPresentableName() {
        return "alfresco_any()";
    }
}
