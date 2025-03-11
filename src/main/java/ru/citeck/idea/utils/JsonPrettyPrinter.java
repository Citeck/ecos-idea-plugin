package ru.citeck.idea.utils;

import ecos.com.fasterxml.jackson210.core.JsonGenerator;
import ecos.com.fasterxml.jackson210.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonPrettyPrinter extends DefaultPrettyPrinter {

    private final List<Boolean> arrayHasElements = new ArrayList<>();

    @Override
    public DefaultPrettyPrinter createInstance() {
        return new JsonPrettyPrinter();
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(": ");
    }

    @Override
    public void writeStartArray(JsonGenerator g) throws IOException {
        g.writeRaw("[");
        arrayHasElements.add(false);
        ++_nesting;
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        --_nesting;
        if (arrayHasElements.get(arrayHasElements.size() - 1)) {
            _objectIndenter.writeIndentation(g, _nesting);
        }
        arrayHasElements.remove(arrayHasElements.get(arrayHasElements.size() - 1));
        g.writeRaw("]");
    }

    @Override
    public void beforeArrayValues(JsonGenerator g) throws IOException {
        arrayHasElements.set(arrayHasElements.size() - 1, true);
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        if (!_objectIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfEntries > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        }
        g.writeRaw("}");
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(",");
        _objectIndenter.writeIndentation(g, _nesting);
    }

}
