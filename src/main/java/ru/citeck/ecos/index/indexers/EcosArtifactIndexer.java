package ru.citeck.ecos.index.indexers;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.Indexes;


public class EcosArtifactIndexer implements EcosFileIndexer {

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof EcosArtifact;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        PsiFile psiFile = inputData.getPsiFile();
        EcosArtifact fileType = (EcosArtifact) getFileType(inputData);

        PsiElement idPsiElement = fileType.getIdPsiElement(psiFile);
        if (idPsiElement == null) {
            return;
        }
        String id = JsonPsiUtil.stripQuotes(idPsiElement.getText());
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
