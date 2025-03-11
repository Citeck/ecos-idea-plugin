package ru.citeck.idea.files.types.citeck;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import ru.citeck.idea.files.types.filters.FileFilter;
import ru.citeck.idea.files.types.filters.FilterAnd;
import ru.citeck.idea.files.types.filters.FolderNamePatternsFilter;

@Getter
public abstract class AbstractEcosArtifact implements EcosArtifact {

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
