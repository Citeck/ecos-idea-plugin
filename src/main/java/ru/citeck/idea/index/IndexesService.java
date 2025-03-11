package ru.citeck.idea.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class IndexesService {

    public static IndexesService getInstance(Project project) {
        return project.getService(IndexesService.class);
    }

    private final Project project;

    public Stream<IndexValue> stream(IndexKey key) {
        return FileBasedIndex
            .getInstance()
            .getValues(CiteckIndex.NAME, key, GlobalSearchScope.allScope(project))
            .stream()
            .flatMap(Collection::stream);
    }

    public List<IndexValue> list(IndexKey key) {
        return stream(key).collect(Collectors.toList());
    }
}
