package ru.citeck.completetion.java

import com.intellij.codeInsight.completion.*
import com.intellij.patterns.PlatformPatterns


class JavaCompletionContributor : CompletionContributor() {

    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), QNamesCompletionProvider())
    }
}