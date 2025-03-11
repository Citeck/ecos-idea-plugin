package ru.citeck.idea.index.indexers;

import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.idea.files.FileType;
import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.index.EcosFileIndexer;
import ru.citeck.idea.index.IndexKey;
import ru.citeck.idea.index.Indexes;


public class EcosArtifactIndexer implements EcosFileIndexer {

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof EcosArtifact;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        PsiFile psiFile = inputData.getPsiFile();
        EcosArtifact fileType = (EcosArtifact) getFileType(inputData);

        if (!fileType.isIndexable(psiFile)) {
            return;
        }

        PsiElement idPsiElement = fileType.getIdPsiElement(psiFile);
        if (idPsiElement == null) {
            return;
        }
        String id = fileType.getId(psiFile);
        if (Strings.isEmpty(id)) {
            return;
        }

        String fullId = fileType.getSourceId() + "@" + id;

        indexes
            .add(new IndexKey(fileType.getSourceId()), indexes.createIndex(id, idPsiElement))
            .addReference(fullId, idPsiElement)
            .addReference(id, idPsiElement)
            .addSearchEverywhere(fullId, idPsiElement);

        fileType
            .getAdditionalReferences(id)
            .forEach(reference -> indexes.addReference(reference, idPsiElement));

    }

}
