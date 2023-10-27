package ru.citeck.ecos.files.deployers;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.CmmnProcessDefinition;
import ru.citeck.ecos.rest.EcosRestApiService;

public class CmmnProcessDefinitionDeployer implements FileDeployer {

    @Override
    public void deploy(PsiFile psiFile) throws Exception {

        VirtualFile virtualFile = psiFile.getVirtualFile();
        byte[] content = psiFile.getText().getBytes(virtualFile.getCharset());

        EcosRestApiService ecosRestApiService = ServiceRegistry.getEcosRestApiService();

        ecosRestApiService.mutateRecord("eproc/procdef", "", "text/xml", virtualFile.getName(), content);
        ecosRestApiService.executeJS(
            "var srv = services.get('eprocActivityService');\n" +
                "var cache1 = Packages.org.apache.commons.lang.reflect.FieldUtils.readField(srv, 'typesToRevisionIdCache', true);\n" +
                "cache1.invalidateAll();\n" +
                "var cache2 = Packages.org.apache.commons.lang.reflect.FieldUtils.readField(srv, 'revisionIdToProcessDefinitionCache', true);\n" +
                "cache2.invalidateAll();"
        );

    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return fileType instanceof CmmnProcessDefinition;
    }

    @Override
    public String getDestinationName(PsiFile psiFile) {
        return "eproc/procdef";
    }
}
