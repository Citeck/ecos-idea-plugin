package ru.citeck.ecos.files.types;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.utils.EcosPsiUtils;

public class AlfrescoConfig implements FileType {

    private final FileFilter filter = new FilterAnd(
            FileExtensionFilter.XML,
            (file, project) -> {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile == null) {
                    return false;
                }

                XmlTag rootTag = EcosPsiUtils.getRootTag(psiFile);
                if (rootTag == null) {
                    return false;
                }

                return "alfresco-config".equals(rootTag.getName());
            }
    );

    @Override
    public FileFilter getFilter() {
        return filter;
    }

}
