package ru.citeck.idea.json

import com.intellij.json.psi.JsonFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.idea.json.element.PsiJPathJsonElement
import ru.citeck.idea.json.element.PsiJPathYamlElement

/**
 * Provides JSON Path support for PSI files (JSON, YAML).
 *
 * This implementation allows retrieving specific elements from JSON/YAML files using JSON Path
 * without full parsing, leveraging core PSI types (`JsonFile` and `YAMLFile`).
 *
 * **Warning:** This implementation does not support the full JSON Path specification.
 * Only absolute paths starting from `$` are supported, using dot-separated keys (`$.a.b.c`).
 * Array selection is limited to index ranges (`[0:1]`, `[:]`).
 */
class PsiJPath private constructor(
    private val path: List<PathPart>
) {

    companion object {

        private fun indexOf(path: String, char: Char, from: Int, to: Int): Int {
            for (i in from until to) {
                if (path[i] == char) return i
            }
            return -1
        }

        private fun assertPath(value: Boolean, path: String) {
            if (!value) {
                error("'$path' is not a valid path")
            }
        }

        private fun parsePathPart(path: String, startIdx: Int, toIdx: Int, result: MutableList<PathPart>) {
            val squareStart = indexOf(path, '[', startIdx, toIdx)
            if (squareStart == -1) {
                result.add(KeyPathPart(path.substring(startIdx, toIdx)))
                return
            }
            result.add(KeyPathPart(path.substring(startIdx, squareStart)))

            val squareEnd = indexOf(path, ']', squareStart, toIdx)
            assertPath(squareEnd > -1, path)

            val squareContent = path.substring(squareStart + 1, squareEnd)
            assertPath(squareContent.isNotEmpty(), path)

            if (squareContent == ":") {
                result.add(RangePathPart(0, Int.MAX_VALUE))
            } else {
                val fromAndTo = squareContent.split(":")
                assertPath(fromAndTo.size == 2, path)
                val from = fromAndTo[0].toIntOrNull() ?: 0
                val to = fromAndTo[1].toIntOrNull() ?: Int.MAX_VALUE
                result.add(RangePathPart(from, to))
            }
        }

        fun parse(path: String): PsiJPath {
            if (!path.startsWith("$.")) {
                error("Path must start with $.")
            }
            val parts = ArrayList<PathPart>()
            var idx = 2
            while (idx < path.length) {
                val nextDelimIdx = path.indexOf('.', idx)
                if (nextDelimIdx == -1) {
                    parsePathPart(path, idx, path.length, parts)
                    break
                } else {
                    parsePathPart(path, idx, nextDelimIdx, parts)
                    idx = nextDelimIdx + 1
                }
            }
            return PsiJPath(parts)
        }
    }

    fun getStr(file: PsiFile): String? {
        return getStrListWithElements(file).firstOrNull()?.first
    }

    fun getStrListWithElements(file: PsiFile): List<Pair<String, PsiElement>> {
        val rootElement = when (file) {
            is YAMLFile -> PsiJPathYamlElement.fromFile(file)
            is JsonFile -> PsiJPathJsonElement.fromFile(file)
            else -> null
        } ?: return emptyList()

        var elements = listOf(rootElement)
        for (pathPart in path) {
            if (elements.isEmpty()) {
                break
            }
            elements = when (pathPart) {
                is RangePathPart -> elements.flatMap {
                    if (pathPart.from >= pathPart.to) {
                        emptyList()
                    } else {
                        it.getChildren(pathPart.from, pathPart.to)
                    }
                }
                is KeyPathPart -> elements.mapNotNull { it.getByKey(pathPart.key) }
            }
        }
        val result = ArrayList<Pair<String, PsiElement>>()
        for (element in elements) {
            val value = element.asText()
            if (value.isBlank()) {
                continue
            }
            result.add(value to element.getPsiElement())
        }
        return result
    }

    override fun toString(): String {
        return "$." + path.joinToString(".")
    }


    private sealed interface PathPart

    private class RangePathPart(
        val from: Int,
        val to: Int
    ) : PathPart {

        override fun toString(): String {
            var res = "["
            if (from > 0) {
                res += from
            }
            res += ":"
            if (to < Int.MAX_VALUE) {
                res += to
            }
            return "$res]"
        }
    }


    private class KeyPathPart(
        val key: String
    ) : PathPart {
        override fun toString(): String {
            return key
        }
    }
}
