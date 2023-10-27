package ru.citeck.ecos.index;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;

import java.util.List;
import java.util.Map;

public class EcosIndex extends FileBasedIndexExtension<IndexKey, List<IndexValue>> {

    public static final ID<IndexKey, List<IndexValue>> NAME = ID.create("ru.citeck.ecos.indexes.EcosIndexExtension");

    public static final ExtensionPointName<EcosFileIndexer> EP_NAME =
        ExtensionPointName.create("ru.citeck.ecos.fileIndexer");

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

        EP_NAME
            .extensions()
            .filter(indexer -> {
                FileType fileType = ServiceRegistry
                    .getFileTypeService()
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
        return 1;
    }

    @Override
    public FileBasedIndex.@NotNull InputFilter getInputFilter() {
        return file -> true;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

}
