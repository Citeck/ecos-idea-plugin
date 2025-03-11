package ru.citeck.idea.index;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.files.FileType;
import ru.citeck.idea.files.FileTypeService;

public interface EcosFileIndexer {

    ExtensionPointName<EcosFileIndexer> EP_NAME = ExtensionPointName.create("ru.citeck.idea.fileIndexer");

    boolean accept(FileType fileType);

    void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes);

    default FileType getFileType(FileContent inputData) {
        return FileTypeService.getInstance()
            .getFileType(inputData.getFile(), inputData.getProject());
    }

}
