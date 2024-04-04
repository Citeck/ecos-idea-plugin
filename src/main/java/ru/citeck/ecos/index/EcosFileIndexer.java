package ru.citeck.ecos.index;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;

public interface EcosFileIndexer {

    ExtensionPointName<EcosFileIndexer> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileIndexer");

    boolean accept(FileType fileType);

    void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes);

    default FileType getFileType(FileContent inputData) {
        return ServiceRegistry
                .getFileTypeService()
                .getFileType(inputData.getFile(), inputData.getProject());
    }

}
