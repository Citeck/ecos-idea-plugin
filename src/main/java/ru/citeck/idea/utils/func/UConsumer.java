package ru.citeck.idea.utils.func;

@FunctionalInterface
public interface UConsumer<T> {

    void accept(T data) throws Exception;
}
