package ru.citeck.idea.artifacts.type

import com.intellij.openapi.diagnostic.Logger
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.json.Json

object FetchedJsonYamlFormatter {

    private const val MAX_YAML_LINE_SIMILARITY = 1

    private val log = Logger.getInstance(FetchedJsonYamlFormatter::class.java)

    fun sortProps(oldData: DataValue, newData: DataValue): DataValue {
        if (newData.isObject()) {
            if (!oldData.isObject()) {
                return newData
            }
            val keys = oldData.fieldNamesList().toMutableList()
            val keysIt = keys.iterator()
            while (keysIt.hasNext()) {
                if (!newData.has(keysIt.next())) {
                    keysIt.remove()
                }
            }
            val newKeys = newData.fieldNamesList()
            for ((idx, key) in newKeys.withIndex()) {
                if (keys.contains(key)) {
                    continue
                }
                when (idx) {
                    0 -> keys.add(0, key)
                    newKeys.lastIndex -> keys.add(key)
                    else -> {
                        var indexForNewKey = keys.indexOf(key)
                        if (indexForNewKey == -1) {
                            indexForNewKey = keys.indexOf(newKeys[idx - 1]) + 1
                        }
                        if (indexForNewKey == -1) {
                            indexForNewKey = idx
                        }
                        keys.add(indexForNewKey, key)
                    }
                }
            }
            val newOrderedData = DataValue.createObj()
            for (key in keys) {
                newOrderedData[key] = sortProps(oldData[key], newData[key])
            }
            return newOrderedData
        } else {
            return newData
        }
    }

    fun restoreYamlFormat(baseContent: String, newContent: String): String {

        val newTextContentLines = newContent.lines().toMutableList()
        if (newTextContentLines.isNotEmpty() && newTextContentLines.last() == "") {
            newTextContentLines.removeLast()
        }
        // let's try to restore comments and empty lines
        val linesBefore = baseContent.lines()
        fun isLineBlankOrComment(line: String): Boolean {
            return line.isBlank() || line.firstOrNull { !it.isWhitespace() } == '#'
        }
        var lineIdx = 0
        while (lineIdx < linesBefore.size) {
            val line = linesBefore[lineIdx]
            if (!isLineBlankOrComment(line)) {
                val inlineCommentIdx = indexOfOutsideOfQuotes(line, 1, " #")
                if (inlineCommentIdx > 0) {
                    val lineIdxToAddComment = findSimilarYamlLineIdx(newTextContentLines, lineIdx, line)
                    if (lineIdxToAddComment > 0) {
                        newTextContentLines[lineIdxToAddComment] += line.substring(inlineCommentIdx)
                    }
                }
                lineIdx++
                continue
            }
            var nextNotEmptyLineIdx = lineIdx + 1
            while (nextNotEmptyLineIdx < linesBefore.size && isLineBlankOrComment(linesBefore[nextNotEmptyLineIdx])) {
                nextNotEmptyLineIdx++
            }
            if (nextNotEmptyLineIdx == linesBefore.size) {
                var lastNotBlankIdx = linesBefore.lastIndex
                while (lastNotBlankIdx > 0 && linesBefore[lastNotBlankIdx].isBlank()) {
                    lastNotBlankIdx--
                }
                for (i in lineIdx..lastNotBlankIdx) {
                    newTextContentLines.add(linesBefore[i])
                }
                break
            } else {
                val nextNotEmptyLine = linesBefore[nextNotEmptyLineIdx]
                val linesResIdx = findSimilarYamlLineIdx(
                    newTextContentLines,
                    nextNotEmptyLineIdx,
                    nextNotEmptyLine
                )
                val linesToInsert = nextNotEmptyLineIdx - lineIdx
                if (linesResIdx >= 0) {
                    for (i in 0 until linesToInsert) {
                        newTextContentLines.add(linesResIdx + i, linesBefore[lineIdx + i])
                    }
                }
                lineIdx += linesToInsert
            }
        }
        if (newTextContentLines.lastOrNull() != "") {
            newTextContentLines.add("")
        }
        val resultText = newTextContentLines.joinToString("\n")
        val dataAfterRestoring = Json.mapper.readData(resultText)
        val sourceData = Json.mapper.readData(newContent)
        if (dataAfterRestoring != sourceData) {
            log.error("Invalid data after restoring. Before $sourceData After $dataAfterRestoring")
            return newContent
        }
        return resultText
    }

    private fun indexOfOutsideOfQuotes(string: String, fromIdx: Int, substring: String): Int {
        var idxIt = fromIdx
        var nextQuote: Char? = null
        while ((idxIt + substring.length) <= string.length) {
            val char = string[idxIt]
            if (char == '\'' || char == '"') {
                nextQuote = if (nextQuote == char) {
                    null
                } else {
                    char
                }
            } else if (nextQuote == null) {
                var equal = true
                for (i in substring.indices) {
                    if (string[idxIt + i] != substring[i]) {
                        equal = false
                        break
                    }
                }
                if (equal) {
                    return idxIt
                }
            }
            idxIt++
        }
        return -1
    }

    private fun findSimilarYamlLineIdx(lines: List<String>, idx: Int, line: String): Int {
        if (lines.isEmpty()) {
            return -1
        }
        var startIdx = idx
        if (startIdx < 0) {
            startIdx = 0
        }
        if (startIdx >= lines.size) {
            startIdx = lines.lastIndex
        }
        var resultSimilarity = getYamlLinesSimilarity(line, lines[startIdx])
        if (resultSimilarity == MAX_YAML_LINE_SIMILARITY) {
            return startIdx
        }
        var result = startIdx
        fun checkNewSimilarity(idxToCheck: Int): Boolean {
            if (idxToCheck < 0 || idxToCheck >= lines.size) {
                return false
            }
            val newSimilarity = getYamlLinesSimilarity(lines[idxToCheck], line)
            if (newSimilarity > resultSimilarity) {
                result = idxToCheck
                resultSimilarity = newSimilarity
            }
            return resultSimilarity == MAX_YAML_LINE_SIMILARITY
        }
        for (nearIdx in 1 until lines.size) {
            if (checkNewSimilarity(startIdx - nearIdx)) break
            if (checkNewSimilarity(startIdx + nearIdx)) break
        }
        if (resultSimilarity == 0) {
            return -1
        }
        return result
    }

    private fun getYamlLinesSimilarity(line0: String, line1: String): Int {

        val normalizedLine0 = line0.trim()
        val normalizedLine1 = line1.trim()

        val strToken0 = parseYamlStrToken(normalizedLine0)
        val strToken1 = parseYamlStrToken(normalizedLine1)

        if (strToken0 != strToken1 || strToken0 == null) {
            return 0
        }

        return MAX_YAML_LINE_SIMILARITY
    }

    private fun parseYamlStrToken(line: String): String? {
        if (line.isEmpty()) {
            return null
        }
        var nextQuoteIdx = -1
        if (line[0] == '"') {
            nextQuoteIdx = line.indexOf('"', 1)
        } else if (line[0] == '\'') {
            nextQuoteIdx = line.indexOf('\'', 1)
        }
        if (nextQuoteIdx > 1) {
            return line.substring(1, nextQuoteIdx)
        } else {
            val twoDotsIdx = line.indexOf(": ")
            if (twoDotsIdx > 0) {
                return line.substring(0, twoDotsIdx)
            } else if (line.last() == ':') {
                return line.substring(0, line.length - 1)
            }
        }
        return null
    }

}
