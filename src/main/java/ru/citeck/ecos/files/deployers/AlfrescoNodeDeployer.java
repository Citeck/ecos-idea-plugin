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
import ru.citeck.ecos.settings.EcosServer;

import java.util.Base64;

public class AlfrescoNodeDeployer implements FileDeployer {

    private static final String DEPLOYMENT_SCRIPT = """
            var base64 = "${BASE64}";
            var contentService = services.get("contentService");
            var node = search.findNode("workspace://SpacesStore/${NODE_UUID}");
            contentWriter = contentService.getWriter(node.nodeRef, Packages.org.alfresco.model.ContentModel.PROP_CONTENT, true);
            contentWriter.setMimetype("${MIME_TYPE}");
            var bytes = Packages.java.util.Base64.getDecoder().decode(base64);
            var bais = Packages.java.io.ByteArrayInputStream(bytes);
            contentWriter.putContent(bais);
            """;

    @Override
    public void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception {

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
                .getEcosRestApiService(ecosServer, psiFile.getProject())
                .executeJS(deploymentScript);

    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return getIndexValue(psiFile) != null;
    }

    @Override
    public String getArtifactName(PsiFile psiFile) {
        return "workspace://SpacesStore/" + getIndexValue(psiFile).getId();
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
    public String getDestinationName(EcosServer ecosServer, PsiFile psiFile) {
        return ecosServer.getHost();
    }


}
