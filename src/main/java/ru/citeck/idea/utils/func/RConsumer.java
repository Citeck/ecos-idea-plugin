package ru.citeck.idea.utils.func;

import ru.citeck.idea.utils.Result;

@FunctionalInterface
public interface RConsumer<T> {

    void accept(Result<T> value);
}
