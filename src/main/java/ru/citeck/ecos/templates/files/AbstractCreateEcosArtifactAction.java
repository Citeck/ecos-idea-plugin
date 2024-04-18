package ru.citeck.ecos.templates.files;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiFileImplUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import icons.Icons;
import lombok.Getter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.utils.EcosMessages;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;
import ru.citeck.ecos.utils.MavenUtils;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public abstract class AbstractCreateEcosArtifactAction extends CreateFileFromTemplateAction {

    private static final String REPO_PATH = "%s/src/main/resources/alfresco/module/%s%s";
    private static final String EAPPS_PATH = "%s/src/main/resources/eapps/artifacts/%s";
    private static final String APP_PATH = "%s/src/main/resources/app/artifacts/%s";

    private static final Map<String, String> PATH_MAPPING_BY_PARENT_GROUP = Map.of(
            "ru.citeck.ecos.eapps.project", APP_PATH,
            "ru.citeck.ecos.webapp", EAPPS_PATH
    );

    private static final Map<String, Icon> EXTENSIONS_ICONS = Map.of(
            "json", AllIcons.FileTypes.Json,
            "yml", AllIcons.FileTypes.Yaml
    );

    private final String artifactName;
    private final Collection<String> extensions;
    private final String templateName;
    private final String path;

    public AbstractCreateEcosArtifactAction(String artifactName, Collection<String> extensions, String templateName, String path) {
        super(artifactName, "Create " + artifactName, Icons.CiteckLogo);
        this.artifactName = artifactName;
        this.extensions = extensions;
        this.templateName = templateName;
        this.path = path;
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("Artifact ID:");
        extensions.forEach(extension -> builder.addKind(
                extension,
                EXTENSIONS_ICONS.getOrDefault(extension, Icons.CiteckLogo),
                getTemplateName() + "." + extension
        ));
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull @NonNls String newName, @NonNls String templateName) {
        return "Create " + getArtifactName();
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {

        Project project = dir.getProject();

        name = cleanExtension(name);

        String path = getArtifactPath(dir);
        if (path == null) {
            EcosMessages.error("Error", "Cannot determine artifact path", project);
            return null;
        }

        PsiDirectory psiDirectory = Optional
                .ofNullable(EcosVirtualFileUtils.getFileByPath(path))
                .or(() -> {
                    File newDir = new File(path);
                    FileUtil.createDirectory(newDir);
                    VirtualFile virtualFile = VirtualFileManager
                            .getInstance()
                            .refreshAndFindFileByNioPath(newDir.toPath());
                    return Optional.ofNullable(virtualFile);
                })
                .map(virtualFile -> PsiManager
                        .getInstance(project)
                        .findDirectory(EcosVirtualFileUtils.getFileByPath(path))
                )
                .orElse(null);

        if (psiDirectory == null) {
            EcosMessages.error("Error", "Cannot get file directory", project);
            return null;
        }

        PsiFile file = super.createFileFromTemplate(name, template, psiDirectory);

        String templateName = template.getName();
        if (templateName.contains(".")){
            String extension = templateName.substring(templateName.indexOf("."));
            PsiFileImplUtil.setName(file, name + extension + "." + template.getExtension());
        }

        return file;

    }

    private String cleanExtension(String name) {
        boolean cleanExtension = Stream
                .of(".yml", ".yaml", ".json", ".xml")
                .anyMatch(extension -> name.toLowerCase().endsWith(extension));
        if (cleanExtension) {
            return name.substring(0, name.lastIndexOf("."));
        }
        return name;
    }

    @Nullable
    private String getArtifactPath(PsiDirectory dir) {

        Module module = ModuleUtil.findModuleForPsiElement(dir);
        if (module == null) {
            return null;
        }

        VirtualFile moduleDir = ProjectUtil.guessModuleDir(module);
        if (moduleDir == null) {
            return null;
        }

        String path = Optional
                .ofNullable(MavenUtils.getPomFile(module))
                .map(XmlFile::getRootTag)
                .map(xmlTag -> xmlTag.findFirstSubTag("parent"))
                .map(xmlTag -> xmlTag.findFirstSubTag("groupId"))
                .map(XmlTag::getValue)
                .map(XmlTagValue::getText)
                .map(PATH_MAPPING_BY_PARENT_GROUP::get)
                .orElse(null);

        if (path == null && module.getName().endsWith("-repo")) {
            return String.format(
                    REPO_PATH,
                    moduleDir.getPath(),
                    module.getName(),
                    getPath()
            );
        }

        return String.format(
                path == null ? APP_PATH : path,
                moduleDir.getPath(),
                getPath()
        );

    }

}
