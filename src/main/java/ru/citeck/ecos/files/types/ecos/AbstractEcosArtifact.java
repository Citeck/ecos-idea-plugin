package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;

@Getter
public abstract class AbstractEcosArtifact implements EcosArtifact, BrowsableEcosArtifact {

    private final FileFilter filter;
    private final String sourceId;

    public AbstractEcosArtifact(String path, FileFilter fileExtensionFilter, String sourceId) {
        this.filter = new FilterAnd(
                fileExtensionFilter,
                new FolderNamePatternsFilter(path)
        );
        this.sourceId = sourceId;
    }

    @Override
    public abstract PsiElement getIdPsiElement(PsiFile psiFile);

}
