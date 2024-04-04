package ru.citeck.ecos.files.types;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;

import java.util.Optional;

public class XmlBeanDefinitions implements FileType {

    private static final FileFilter BEAN_DEFINITIONS_FILE_FILTER = (file, project) -> {

        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null) {
            return false;
        }

        return Optional
                .of(psiFile)
                .filter(XmlFile.class::isInstance)
                .map(XmlFile.class::cast)
                .map(XmlFile::getRootTag)
                .map(XmlTag::getName)
                .map("beans"::equals)
                .orElse(false);

    };

    private static final FileFilter FILE_FILTER = new FilterAnd(FileExtensionFilter.XML, BEAN_DEFINITIONS_FILE_FILTER);

    @Override
    public FileFilter getFilter() {
        return FILE_FILTER;
    }

}
