package ru.citeck.idea.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.psi.JsonFile
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.idea.artifacts.ArtifactsService

/**
 * Reports attributes of `model/type` and `model/aspect` artifacts whose database column name
 * is longer than [ColumnNameLimit.MAX_BYTES]. The storage layer uses the attribute id as the column
 * name verbatim; PostgreSQL silently truncates longer identifiers, after which every write of
 * a record of that type fails with "column already exists".
 */
class ModelAttColumnNameLengthInspection : LocalInspectionTool() {

    override fun isAvailableForFile(file: PsiFile): Boolean {
        return file is YAMLFile || file is JsonFile
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is YAMLFile && file !is JsonFile) {
            return null
        }
        val typeId = ArtifactsService.getInstance().getArtifactInfo(file)?.getTypeId() ?: return null
        val columns = ModelAttColumnNames.collect(file, typeId)
        if (columns.isEmpty()) {
            return null
        }
        val holder = ProblemsHolder(manager, file, isOnTheFly)
        for (column in columns) {
            val bytes = ColumnNameLimit.byteLength(column.name)
            if (bytes > ColumnNameLimit.MAX_BYTES) {
                holder.registerProblem(column.idElement, message(column, bytes))
            }
        }
        return holder.resultsArray
    }

    private fun message(column: ModelAttColumn, bytes: Int): String {
        val problem = "Column name '${column.name}' is $bytes bytes long, " +
            "but PostgreSQL truncates identifiers to ${ColumnNameLimit.MAX_BYTES} bytes. "
        return if (column.aspect) {
            problem + "Shorten the attribute id or set a short 'prefix' for the aspect."
        } else {
            problem + "Shorten the attribute id."
        }
    }
}
