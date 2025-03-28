package ru.citeck.idea.search.index

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import java.util.stream.Collectors
import java.util.stream.Stream

@Service(Service.Level.PROJECT)
class IndexesService(
    private val project: Project
) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): IndexesService {
            return project.getService(IndexesService::class.java)
        }
    }

    fun stream(key: IndexKey): Stream<IndexValue> {
        return FileBasedIndex
            .getInstance()
            .getValues(CiteckIndexConstants.INDEX_NAME, key, GlobalSearchScope.allScope(project))
            .stream()
            .flatMap { obj: List<IndexValue> -> obj.stream() }
    }

    fun list(key: IndexKey): List<IndexValue> {
        return stream(key).collect(Collectors.toList())
    }
}
