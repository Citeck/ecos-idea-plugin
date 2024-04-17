package ru.citeck.ecos.templates.files;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.types.ecos.Theme;

import java.util.List;
import java.util.Optional;

public class CreateTheme extends AbstractCreateEcosArtifactAction {

    public CreateTheme() {
        super("Theme", List.of("json"), "ecos-theme", Theme.PATH);
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
        PsiFile meta = super.createFileFromTemplate(name + "/meta", template, dir);
        Optional
                .ofNullable(meta.getParent())
                .ifPresent(psiDirectory -> {
                    psiDirectory.createFile("main.css");
                    psiDirectory.createSubdirectory("image");
                });

        return meta;
    }

}
