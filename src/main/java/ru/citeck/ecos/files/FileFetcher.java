package ru.citeck.ecos.files;

import com.intellij.psi.PsiFile;

public interface FileFetcher {

    String fetch(PsiFile psiFile) throws Exception;

    boolean canFetch(PsiFile psiFile, FileType fileType);

    String getSourceName(PsiFile psiFile);

}
