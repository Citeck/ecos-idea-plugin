package ru.citeck.ecos.index.indexers;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.AlfrescoViewRepository;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.Indexes;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AlfrescoContentNodesIndexer implements EcosFileIndexer {

    public static final String SYS_NODE_UUID = "sys:node-uuid";
    public static final String CM_NAME = "cm:name";
    public static final String CM_CONTENT = "cm:content";
    public static final String NODE_INDEX_KEY = "alfresco/node";
    private static final String CONTENT_URL_CLASSPATH = "contentUrl=classpath:";

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof AlfrescoViewRepository;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        Project project = inputData.getProject();
        if (project == null) {
            return;
        }

        PsiFile psiFile = inputData.getPsiFile();
        if (!(psiFile instanceof XmlFile)) {
            return;
        }

        XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
        if (rootTag == null) {
            return;
        }

        List<XmlTag> views = PsiTreeUtil
                .findChildrenOfType(rootTag, XmlTag.class)
                .stream()
                .filter(xmlTag -> "view:properties".equals(xmlTag.getName()))
                .filter(xmlTag -> Stream
                        .of(SYS_NODE_UUID, CM_NAME, CM_CONTENT)
                        .allMatch(qName -> xmlTag.findFirstSubTag(qName) != null)
                )
                .toList();

        if (views.isEmpty()) {
            return;
        }

        Set<String> modulesRoots = Arrays
                .stream(ModuleManager.getInstance(inputData.getProject()).getModules())
                .map(ModuleUtil::getModuleDirPath)
                .collect(Collectors.toSet());

        views.forEach(view -> {

            String uuid = Optional
                    .ofNullable(view.findFirstSubTag(SYS_NODE_UUID))
                    .map(XmlTag::getValue)
                    .map(XmlTagValue::getText)
                    .orElse("");

            String content = Optional
                    .ofNullable(view.findFirstSubTag(CM_CONTENT))
                    .map(XmlTag::getValue)
                    .map(XmlTagValue::getText)
                    .orElse("");

            List<String> contentUrls = Arrays.asList(content.split("\\|"));

            if (contentUrls.isEmpty()) {
                return;
            }

            String contentUrl = contentUrls
                    .stream()
                    .filter(s -> s.startsWith(CONTENT_URL_CLASSPATH))
                    .map(s -> s.substring(CONTENT_URL_CLASSPATH.length()))
                    .findFirst()
                    .orElse(null);

            if (contentUrl == null) {
                return;
            }

            VirtualFile contentFile = modulesRoots
                    .stream()
                    .map(module -> VirtualFileManager.getInstance().findFileByNioPath(Path.of(module + "/src/main/resources/" + contentUrl)))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (contentFile == null) {
                return;
            }

            String mimeType = contentUrls
                    .stream()
                    .filter(s -> s.startsWith("mimetype="))
                    .map(s -> s.replace("mimetype=", ""))
                    .findFirst()
                    .orElse("");

            IndexValue indexValue = new IndexValue(uuid, 0, contentFile.getPath())
                    .setProperty("mimetype", mimeType);

            indexes
                    .addReference("workspace://SpacesStore/" + uuid, view)
                    .add(new IndexKey(NODE_INDEX_KEY, contentFile.getPath()), indexValue);

        });

    }

    @Override
    public FileType getFileType(FileContent inputData) {
        return null;
    }

}
