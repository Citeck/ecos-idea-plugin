package ru.citeck.idea.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLFile;

import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.NavigateInFileItem;
import ru.citeck.idea.files.NavigateInFileItemsProvider;
import ru.citeck.idea.files.types.citeck.model.DataType;
import ru.citeck.idea.search.index.IndexKey;
import ru.citeck.idea.search.index.IndexesService;
import ru.citeck.idea.search.index.indexers.EcosDataTypeIndexer;
import ru.citeck.idea.utils.CiteckVirtualFileUtils;

import java.util.Collection;

public class DataTypeFileItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!(psiFile instanceof YAMLFile) && !(psiFile instanceof JsonFile)) {
            return null;
        }
        if (!(FileTypeService.getInstance().getFileType(psiFile) instanceof DataType fileType)) {
            return null;
        }

        Project project = psiFile.getProject();

        String id = fileType.getFullId(psiFile);
        IndexesService indexesService = IndexesService.getInstance(project);

        return EcosDataTypeIndexer.getMODEL_PARTITIONS_MAPPING()
            .keySet()
            .stream()
            .flatMap(key -> indexesService
                .stream(new IndexKey(id, key))
                .filter(indexValue -> psiFile
                    .getVirtualFile()
                    .equals(CiteckVirtualFileUtils.getFileByPath(indexValue.getFile()))
                )
                .map(indexValue -> new NavigateInFileItem(
                    key + "@" + indexValue.getId(),
                    indexValue.getPsiElement(project))
                )
            )
            .toList();
    }

}
