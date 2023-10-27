package ru.citeck.ecos.index.indexers;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileContent;
import com.jgoodies.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.XmlBeanDefinitions;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.Indexes;

import java.util.Arrays;
import java.util.Set;

public class XmlBeanDefinitionIndexer implements EcosFileIndexer {

    public static final String JAVASCRIPT_EXTENSION_KEY_NAME = "java-script-extension";
    public static final IndexKey JAVASCRIPT_EXTENSION_KEY = new IndexKey(JAVASCRIPT_EXTENSION_KEY_NAME);

    private static final Set<String> JAVASCRIPT_EXTENSION_PARENTS = Set.of(
        "baseJavaScriptExtension",
        "ecos.baseJavaScriptExtension"
    );

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof XmlBeanDefinitions;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        XmlTag rootTag = ((XmlFile) inputData.getPsiFile()).getRootTag();
        if (rootTag == null) {
            return;
        }

        Arrays.stream(rootTag.findSubTags("bean"))
            .filter(xmlTag ->
                "bean".equals(xmlTag.getName()) &&
                    JAVASCRIPT_EXTENSION_PARENTS.contains(xmlTag.getAttributeValue("parent"))
            ).forEach(xmlTag -> {

            String clazz = xmlTag.getAttributeValue("class");
            String extensionName = Arrays
                .stream(xmlTag.findSubTags("property"))
                .filter(propertyTag -> "extensionName".equals(propertyTag.getAttributeValue("name")))
                .findFirst()
                .map(extensionNameTag -> {
                    XmlTag valueTag = extensionNameTag.findFirstSubTag("value");
                    if (valueTag != null) {
                        return valueTag.getValue().getText();
                    } else {
                        return extensionNameTag.getAttributeValue("value");
                    }
                })
                .orElse(null);

            if (Strings.isEmpty(clazz) || Strings.isEmpty(extensionName)) {
                return;
            }

            IndexValue indexValue = indexes
                .createIndex(extensionName, xmlTag)
                .setProperty("class", clazz);

            indexes
                .add(JAVASCRIPT_EXTENSION_KEY, indexValue)
                .add(new IndexKey(JAVASCRIPT_EXTENSION_KEY_NAME, extensionName), indexValue);

        });

    }

}
