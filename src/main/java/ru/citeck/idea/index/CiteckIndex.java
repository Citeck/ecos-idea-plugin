package ru.citeck.idea.index;

import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.files.FileType;
import ru.citeck.idea.files.FileTypeService;

import java.util.List;
import java.util.Map;

public class CiteckIndex extends FileBasedIndexExtension<IndexKey, List<IndexValue>> {

    public static final ID<IndexKey, List<IndexValue>> NAME = ID.create("ru.citeck.indexes.EcosIndexExtension");

    private final DataExternalizer<List<IndexValue>> dataExternalizer = new IndexValue.Externalizer();
    private final KeyDescriptor<IndexKey> keyDescriptor = new IndexKey.Descriptor();

    @Override
    public @NotNull ID<IndexKey, List<IndexValue>> getName() {
        return NAME;
    }

    @Override
    public @NotNull DataIndexer<IndexKey, List<IndexValue>, FileContent> getIndexer() {
        return this::map;
    }

    private @NotNull Map<IndexKey, List<IndexValue>> map(@NotNull FileContent inputData) {

        Indexes indexes = new Indexes();
        if (inputData.getFile().getFileType().isBinary()) {
            return indexes;
        }

        EcosFileIndexer.EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .filter(indexer -> {
                FileType fileType = FileTypeService.getInstance()
                    .getFileType(inputData.getFile(), inputData.getProject());
                return indexer.accept(fileType);
            })
            .forEach(ecosDataIndexer ->
                ecosDataIndexer.map(inputData, indexes.withFileContent(inputData))
            );

        return indexes;
    }

    @Override
    public @NotNull KeyDescriptor<IndexKey> getKeyDescriptor() {
        return keyDescriptor;
    }

    @Override
    public @NotNull DataExternalizer<List<IndexValue>> getValueExternalizer() {
        return dataExternalizer;
    }

    @Override
    public int getVersion() {
        return 3;
    }

    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> true;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

}
