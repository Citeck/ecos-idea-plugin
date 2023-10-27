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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class TomcatFileDeployer implements FileDeployer {

    private static final String DEPLOYMENT_SCRIPT = "" +
        "var base64 = \"${BASE64}\";\n" +
        "var relativePath = \"/WEB-INF/classes/${RELATIVE_PATH}\";\n" +
        "var webAppsPath = Packages.java.lang.System.getProperty(\"catalina.base\") + \"/webapps/\";\n" +
        "var found = new java.io.File(webAppsPath).listFiles().filter(function (file) {\n" +
        "    if (!file.isDirectory()) {\n" +
        "        return false;\n" +
        "    }\n" +
        "    return new java.io.File(file.toString() + relativePath).exists();\n" +
        "})\n" +
        "if (found.length !== 1) {\n" +
        "    throw Error(\"Unable to determine file path\");\n" +
        "}\n" +
        "var path = found[0].toString() + relativePath;\n" +
        "var bytes = Packages.java.util.Base64.getDecoder().decode(base64);\n" +
        "var fos = new Packages.java.io.FileOutputStream(path);\n" +
        "fos.write(bytes);\n" +
        "fos.close();";

    @Override
    public void deploy(PsiFile psiFile) throws Exception {
        ServiceRegistry
            .getEcosRestApiService()
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
    public String getDestinationName(PsiFile psiFile) {
        return ServiceRegistry.getEcosRestApiService().getHost();
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
