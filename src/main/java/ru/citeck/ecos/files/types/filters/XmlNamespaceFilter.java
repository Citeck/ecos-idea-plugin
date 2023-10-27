package ru.citeck.ecos.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.utils.EcosPsiUtils;

public class XmlNamespaceFilter extends FileExtensionFilter {

    private final String namespace;

    public XmlNamespaceFilter(@NotNull String namespace) {
        super("xml");
        this.namespace = namespace;
    }

    @Override
    public boolean accept(VirtualFile file, Project project) {

        if (!super.accept(file, project)) {
            return false;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null) {
            return false;
        }

        XmlTag rootTag = EcosPsiUtils.getRootTag(psiFile);
        if (rootTag == null) {
            return false;
        }

        return namespace.equals(rootTag.getNamespace());

    }

}
