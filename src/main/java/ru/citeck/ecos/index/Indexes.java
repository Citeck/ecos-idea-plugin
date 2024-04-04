package ru.citeck.ecos.index;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.FileContent;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexes extends HashMap<IndexKey, List<IndexValue>> {

    public FileIndexes withFileContent(FileContent fileContent) {
        return new FileIndexes((fileContent.getFile()));
    }

    @Data
    public class FileIndexes {

        private final VirtualFile virtualFile;

        public FileIndexes addSearchEverywhere(String id, PsiElement psiElement, Map<String, String> properties) {
            return add(IndexKey.SEARCH_EVERYWHERE, id, psiElement, properties);
        }

        public FileIndexes addSearchEverywhere(String id, PsiElement psiElement) {
            return add(IndexKey.SEARCH_EVERYWHERE, id, psiElement, null);
        }

        public FileIndexes addReference(String id, PsiElement psiElement, Map<String, String> properties) {
            return add(new IndexKey(IndexKey.REFERENCE_TYPE, id), id, psiElement, properties);
        }

        public FileIndexes addReference(String id, PsiElement psiElement) {
            return add(new IndexKey(IndexKey.REFERENCE_TYPE, id), id, psiElement, null);
        }

        public FileIndexes add(IndexKey key, String id, PsiElement psiElement, Map<String, String> properties) {
            return add(key, createIndex(id, psiElement, properties));
        }

        public FileIndexes add(IndexKey key, String id, PsiElement psiElement) {
            return add(key, createIndex(id, psiElement, null));
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

        public IndexValue createIndex(String id, PsiElement psiElement, Map<String, String> properties) {
            IndexValue indexValue = new IndexValue(id, psiElement.getTextOffset(), virtualFile.getPath());
            if (properties != null) {
                properties.forEach(indexValue::setProperty);
            }
            return indexValue;
        }

        public IndexValue createIndex(String id, PsiElement psiElement) {
            return createIndex(id, psiElement, null);
        }

    }

}
