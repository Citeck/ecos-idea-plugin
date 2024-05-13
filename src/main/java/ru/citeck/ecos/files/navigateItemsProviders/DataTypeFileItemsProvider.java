package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.ecos.model.DataType;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexesService;
import ru.citeck.ecos.index.indexers.EcosDataTypeIndexer;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import java.util.Collection;

public class DataTypeFileItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!(psiFile instanceof YAMLFile) && !(psiFile instanceof JsonFile)) {
            return null;
        }
        if (!(ServiceRegistry.getFileTypeService().getFileType(psiFile) instanceof DataType fileType)) {
            return null;
        }

        Project project = psiFile.getProject();

        String id = fileType.getFullId(psiFile);
        IndexesService indexesService = ServiceRegistry.getIndexesService(project);

        return EcosDataTypeIndexer.MODEL_PARTITIONS_MAPPING
                .keySet()
                .stream()
                .flatMap(key -> indexesService
                        .stream(new IndexKey(id, key))
                        .filter(indexValue -> psiFile
                                .getVirtualFile()
                                .equals(EcosVirtualFileUtils.getFileByPath(indexValue.getFile()))
                        )
                        .map(indexValue -> new NavigateInFileItem(
                                key + "@" + indexValue.getId(),
                                indexValue.getPsiElement(project))
                        )
                )
                .toList();
    }

}
