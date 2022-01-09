package ru.citeck.metadata

import com.intellij.openapi.project.Project

abstract class MetadataProvider<T>(val project: Project) {

    private var data: T? = null

    private val initialized: Boolean
        get() {
            return data != null
        }

    protected abstract fun loadData(): T?

    fun getData(): T? {
        return data
    }

    fun initialize() {
        if (initialized) return
        data = loadData()
    }

    fun reset() {
        data = null
    }

}