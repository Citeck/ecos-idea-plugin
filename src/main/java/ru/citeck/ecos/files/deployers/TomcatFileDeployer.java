package ru.citeck.ecos.files.deployers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.JavaScript;
import ru.citeck.ecos.settings.EcosServer;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class TomcatFileDeployer implements FileDeployer {

    private static final String DEPLOYMENT_SCRIPT = """
            var base64 = "${BASE64}";
            var relativePath = "/WEB-INF/classes/${RELATIVE_PATH}";
            var webAppsPath = Packages.java.lang.System.getProperty("catalina.base") + "/webapps/";
            var found = new java.io.File(webAppsPath).listFiles().filter(function (file) {
                if (!file.isDirectory()) {
                    return false;
                }
                return new java.io.File(file.toString() + relativePath).exists();
            })
            if (found.length !== 1) {
                throw Error("Unable to determine file path");
            }
            var path = found[0].toString() + relativePath;
            var bytes = Packages.java.util.Base64.getDecoder().decode(base64);
            var fos = new Packages.java.io.FileOutputStream(path);
            fos.write(bytes);
            fos.close();
            """;

    @Override
    public void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception {
        ServiceRegistry
                .getEcosRestApiService(ecosServer, psiFile.getProject())
                .executeJS(getDeploymentScript(psiFile));
    }

    public String getDeploymentScript(PsiFile psiFile) throws Exception {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Project project = psiFile.getProject();

        String relativePath = Optional
                .of(ProjectFileIndex.getInstance(project))
                .map(projectFileIndex -> projectFileIndex.getSourceRootForFile(virtualFile))
                .map(sourceRoot -> VfsUtilCore.getRelativePath(virtualFile, sourceRoot))
                .orElse(null);

        if (relativePath == null) {
            throw new Exception("Unable to get file relative path");
        }

        Charset charset = virtualFile.getCharset();
        String base64 = Base64.getEncoder().encodeToString(psiFile.getText().getBytes(charset));

        return DEPLOYMENT_SCRIPT
                .replace("${BASE64}", base64)
                .replace("${RELATIVE_PATH}", relativePath);
    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return fileType instanceof JavaScript && getModule(psiFile) != null;
    }

    @Override
    public String getArtifactName(PsiFile psiFile) {
        return psiFile.getName();
    }

    @Override
    public String getDestinationName(EcosServer ecosServer, PsiFile psiFile) {
        return ecosServer.getHost();
    }

    private Module getModule(PsiFile psiFile) {

        VirtualFile virtualFile = psiFile.getVirtualFile();
        ModuleManager moduleManager = ModuleManager.getInstance(psiFile.getProject());

        return Arrays
                .stream(moduleManager.getModules())
                .filter(module -> module.getName().endsWith("-repo"))
                .filter(module -> module.getModuleScope().contains(virtualFile))
                .findFirst()
                .orElse(null);

    }

}
