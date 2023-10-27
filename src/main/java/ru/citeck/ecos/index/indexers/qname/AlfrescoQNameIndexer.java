package ru.citeck.ecos.index.indexers.qname;

import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.Java;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.Indexes;

public class AlfrescoQNameIndexer implements EcosFileIndexer {

    public static final IndexKey CONTAINS_QNAMES_KEY = new IndexKey("contains-qnames");

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof Java;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {
        PsiFile psiFile = inputData.getPsiFile();
        String text = psiFile.getText();
        if (text.contains(QName.CLASS) && text.contains("QName.createQName(")) {
            indexes.add(CONTAINS_QNAMES_KEY, "", psiFile);
        }
    }

}
