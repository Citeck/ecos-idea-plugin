package ru.citeck.ecos.index.indexers;

import com.intellij.openapi.util.text.Strings;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Consumer;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.AlfrescoModel;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.Indexes;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

public class AlfrescoModelIndexer implements EcosFileIndexer {

    public static final String MODEL = "alfresco/model";
    public static final String NAMESPACE = "alfresco/namespace";
    public static final String TYPE = "alfresco/type";
    public static final String PROPERTY = "alfresco/property";
    public static final String ASPECT = "alfresco/aspect";
    public static final String ASSOCIATION = "alfresco/association";
    public static final String CHILD_ASSOCIATION = "alfresco/child-association";
    public static final String CONSTRAINT = "alfresco/aspect";

    private static final Map<String, String> ICON_PROPERTIES = Map.of("icon", "alfresco");

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof AlfrescoModel;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        String fileName = inputData.getFile().getPath();
        if (fileName.contains(".jar!/") && !fileName.contains("-sources.jar!/")) {
            VirtualFile srcFile = EcosVirtualFileUtils.getFileByPath(fileName.replace(".jar!/", "-sources.jar!/"));
            if (srcFile != null) {
                return;
            }
        }

        PsiFile psiFile = inputData.getPsiFile();
        if (!(psiFile instanceof XmlFile)) {
            return;
        }

        XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
        if (rootTag == null) {
            return;
        }

        String name = rootTag.getAttributeValue("name");
        if (name != null) {
            indexes.addSearchEverywhere(MODEL + "@" + name, rootTag, ICON_PROPERTIES);
        }

        mapNamespaces(rootTag, indexes);
        mapTypes(rootTag, indexes);
        mapAspects(rootTag, indexes);
        mapConstraints(rootTag, indexes);

    }

    private void mapConstraints(XmlTag parentTag, Indexes.FileIndexes indexes) {
        map(parentTag, "constraints", "constraint", tag -> {
            String name = tag.getAttributeValue("name");
            if (name == null) {
                return;
            }
            indexes
                .addSearchEverywhere(CONSTRAINT + "@" + name, tag, ICON_PROPERTIES)
                .addReference(name, tag, ICON_PROPERTIES);
        });
    }

    private void mapAspects(XmlTag parentTag, Indexes.FileIndexes indexes) {
        map(parentTag, "aspects", "aspect", tag -> {
            String name = tag.getAttributeValue("name");
            if (name == null) {
                return;
            }
            indexes
                .addSearchEverywhere(ASPECT + "@" + name, tag, ICON_PROPERTIES)
                .addReference(name, tag, ICON_PROPERTIES)
                .add(new IndexKey(ASPECT), indexes.createIndex(name, tag, ICON_PROPERTIES));

            mapProperties(tag, indexes);
            mapAssociations(tag, indexes);

        });
    }

    private void mapNamespaces(XmlTag parentTag, Indexes.FileIndexes indexes) {
        map(parentTag, "namespaces", "namespace", tag -> {

            String prefix = tag.getAttributeValue("prefix");
            String uri = tag.getAttributeValue("uri");

            if (Strings.isEmpty(prefix) || Strings.isEmpty(uri)) {
                return;
            }

            IndexValue indexValue = indexes
                .createIndex(prefix, tag, ICON_PROPERTIES)
                .setProperty("prefix", prefix)
                .setProperty("uri", uri);

            indexes
                .addSearchEverywhere(NAMESPACE + "@" + prefix, tag, ICON_PROPERTIES)
                .addSearchEverywhere(NAMESPACE + "@" + uri, tag, ICON_PROPERTIES)
                .addReference(uri, tag, ICON_PROPERTIES)
                .add(new IndexKey(NAMESPACE), indexValue)
                .add(new IndexKey(NAMESPACE, uri), indexValue);

        });
    }

    private void mapTypes(XmlTag parentTag, Indexes.FileIndexes indexes) {
        map(parentTag, "types", "type", tag -> {
            String name = tag.getAttributeValue("name");
            if (name == null) {
                return;
            }
            indexes
                .addSearchEverywhere(TYPE + "@" + name, tag, ICON_PROPERTIES)
                .addReference(name, tag, ICON_PROPERTIES)
                .addReference("dict@" + name, tag, ICON_PROPERTIES)
                .addReference("alf_" + name, tag, ICON_PROPERTIES)
                .add(new IndexKey(TYPE), indexes.createIndex(name, tag, ICON_PROPERTIES));

            mapProperties(tag, indexes);
            mapAssociations(tag, indexes);
        });
    }

    private void mapProperties(XmlTag parentTag, Indexes.FileIndexes indexes) {
        map(parentTag, "properties", "property", tag -> {
            String name = tag.getAttributeValue("name");
            if (name == null) {
                return;
            }
            indexes
                .addSearchEverywhere(PROPERTY + "@" + name, tag, ICON_PROPERTIES)
                .addReference(name, tag, ICON_PROPERTIES)
                .addReference("_ECM_" + name, tag, ICON_PROPERTIES)
                .add(new IndexKey(PROPERTY), indexes.createIndex(name, tag, ICON_PROPERTIES));
        });
    }

    private void mapAssociations(XmlTag parentTag, Indexes.FileIndexes indexes) {

        BiConsumer<XmlTag, String> assocMapper = (tag, type) -> {
            String name = tag.getAttributeValue("name");
            if (name == null) {
                return;
            }
            indexes
                .addSearchEverywhere(type + "@" + name, tag, ICON_PROPERTIES)
                .addReference(name, tag, ICON_PROPERTIES)
                .addReference("_ECM_" + name, tag, ICON_PROPERTIES)
                .add(new IndexKey(type), indexes.createIndex(name, tag, ICON_PROPERTIES));
        };

        map(parentTag, "associations", "association", tag -> assocMapper.accept(tag, ASSOCIATION));
        map(parentTag, "associations", "child-association", tag -> assocMapper.accept(tag, CHILD_ASSOCIATION));

    }

    private void map(XmlTag parentTag, String tagName, String subTagName, Consumer<XmlTag> handler) {
        XmlTag tag = parentTag.findFirstSubTag(tagName);
        if (tag == null) {
            return;
        }
        Arrays.stream(tag.findSubTags(subTagName)).forEach(handler::consume);
    }

}
