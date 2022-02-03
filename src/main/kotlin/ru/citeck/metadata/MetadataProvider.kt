package ru.citeck.metadata

import com.intellij.openapi.project.Project

abstract class MetadataProvider<T>(val project: Project, val initOrder: Int = 0) {

    var data: T? = null
        private set(value) {
            field = value
        }

    private val initialized: Boolean
        get() {
            return data != null
        }

    protected abstract fun loadData(): T?


    fun initialize() {
        data = loadData()
    }

}