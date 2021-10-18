package ru.citeck.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter

class EcosPrettyPrinter : DefaultPrettyPrinter() {

    override fun createInstance(): DefaultPrettyPrinter {
        return EcosPrettyPrinter()
    }

    override fun writeObjectFieldValueSeparator(jg: JsonGenerator?) {
        jg?.writeRaw(": ")
    }

    var _arraysHasElements = ArrayList<Boolean>()

    override fun writeStartArray(jg: JsonGenerator) {
        jg.writeRaw("[")
        _arraysHasElements.add(false)
        ++_nesting
    }

    override fun writeEndArray(jg: JsonGenerator, nrOfValues: Int) {
        --_nesting
        if (_arraysHasElements[_arraysHasElements.size - 1]) {
            _objectIndenter.writeIndentation(jg, _nesting)
        }
        _arraysHasElements.removeAt(_arraysHasElements.size - 1)
        jg.writeRaw("]")
    }

    override fun beforeArrayValues(jg: JsonGenerator?) {
        _arraysHasElements[_arraysHasElements.size - 1] = true
        _objectIndenter.writeIndentation(jg, _nesting)
    }

    override fun writeEndObject(jg: JsonGenerator?, nrOfEntries: Int) {
        if (!_objectIndenter.isInline) --_nesting
        if (nrOfEntries > 0) _objectIndenter.writeIndentation(jg, _nesting)
        jg!!.writeRaw('}')
    }

    override fun writeArrayValueSeparator(jg: JsonGenerator?) {
        jg?.writeRaw(",")
        _objectIndenter.writeIndentation(jg, _nesting)
    }

}