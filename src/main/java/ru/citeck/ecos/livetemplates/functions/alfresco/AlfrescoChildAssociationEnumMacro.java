package ru.citeck.ecos.livetemplates.functions.alfresco;

import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.Collections;
import java.util.List;

public class AlfrescoChildAssociationEnumMacro extends AlfrescoEnumMacro {

    private static final List<IndexKey> INDEX_KEYS = Collections.singletonList(
            new IndexKey(AlfrescoModelIndexer.CHILD_ASSOCIATION)
    );

    @Override
    public List<IndexKey> getIndexKeys() {
        return INDEX_KEYS;
    }

    @Override
    public String getName() {
        return "alfresco_child_association";
    }

    @Override
    public String getPresentableName() {
        return "alfresco_child_association()";
    }

}
