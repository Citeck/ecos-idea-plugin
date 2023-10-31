package ru.citeck.ecos.files.deployers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiBinaryFile;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.indexers.AlfrescoContentNodesIndexer;

import java.nio.charset.Charset;
import java.util.Base64;

public class AlfrescoNodeDeployer implements FileDeployer {

    private static final String DEPLOYMENT_SCRIPT = "" +
        "var base64 = \"${BASE64}\";\n" +
        "var contentService = services.get(\"contentService\");\n" +
        "var node = search.findNode(\"workspace://SpacesStore/${NODE_UUID}\");\n" +
        "contentWriter = contentService.getWriter(node.nodeRef, Packages.org.alfresco.model.ContentModel.PROP_CONTENT, true);\n" +
        "contentWriter.setMimetype(\"${MIME_TYPE}\");\n" +
        "var bytes = Packages.java.util.Base64.getDecoder().decode(base64);\n" +
        "var bais = Packages.java.io.ByteArrayInputStream(bytes);\n" +
        "contentWriter.putContent(bais);";

    @Override
    public void deploy(PsiFile psiFile) throws Exception {

        IndexValue indexValue = getIndexValue(psiFile);
        VirtualFile virtualFile = psiFile.getVirtualFile();

        byte[] content;
        if (psiFile instanceof PsiBinaryFile) {
            content = virtualFile.contentsToByteArray();
        } else {
            content = psiFile.getText().getBytes(virtualFile.getCharset());
        }

        String base64 = Base64.getEncoder().encodeToString(content);

        String deploymentScript = DEPLOYMENT_SCRIPT
            .replace("${BASE64}", base64)
            .replace("${NODE_UUID}", indexValue.getId())
            .replace("${MIME_TYPE}", indexValue.getProperty("mimetype"));

        ServiceRegistry
            .getEcosRestApiService()
            .executeJS(deploymentScript);

    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return getIndexValue(psiFile) != null;
    }

    private IndexValue getIndexValue(PsiFile psiFile) {
        Project project = psiFile.getProject();
        IndexKey key = new IndexKey(AlfrescoContentNodesIndexer.NODE_INDEX_KEY, psiFile.getVirtualFile().getPath());
        return ServiceRegistry
            .getIndexesService(project)
            .stream(key)
            .findFirst()
            .orElse(null);
    }

    @Override
    public String getDestinationName(PsiFile psiFile) {
        IndexValue indexValue = getIndexValue(psiFile);
        return ServiceRegistry.getEcosRestApiService().getHost() + "@workspace://SpacesStore/" + indexValue.getId();
    }



}
