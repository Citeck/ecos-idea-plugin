package ru.citeck.idea.utils.func;

@FunctionalInterface
public interface UFunction<I, O> {

    O apply(I input) throws Exception;
}
