package ru.citeck.ecos.index;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileContent;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Indexes extends HashMap<IndexKey, List<IndexValue>> {

    public FileIndexes withFileContent(FileContent fileContent) {
        return new FileIndexes((fileContent.getFile()));
    }

    @Data
    public class FileIndexes {

        private final VirtualFile virtualFile;

        public FileIndexes addSearchEverywhere(String id, PsiElement psiElement) {
            return add(IndexKey.SEARCH_EVERYWHERE, id, psiElement);
        }

        public FileIndexes addReference(String id, PsiElement psiElement) {
            return add(new IndexKey(IndexKey.REFERENCE_TYPE, id), id, psiElement);
        }

        public FileIndexes add(IndexKey key, String id, PsiElement psiElement) {
            return add(key, createIndex(id, psiElement));
        }

        public FileIndexes add(IndexKey key, IndexValue indexValue) {
            List<IndexValue> values = get(key);
            if (values == null) {
                values = new ArrayList<>();
                put(key, values);
            }
            values.add(indexValue);
            return this;
        }

        public IndexValue createIndex(String id, PsiElement psiElement) {
            return new IndexValue(id, psiElement.getTextOffset(), virtualFile.getPath());
        }

    }

}
