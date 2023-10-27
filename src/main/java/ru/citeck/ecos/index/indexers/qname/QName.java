package ru.citeck.ecos.index.indexers.qname;

import lombok.Data;

@Data
public class QName {

    public static final String QNAME_PATTERN = "org.alfresco.service.namespace.QNamePattern";
    public static final String CLASS = "org.alfresco.service.namespace.QName";

    private final String localName;
    private final String uri;
    private final String prefix;
    private final String javaField;
    private final String javaClass;

}
