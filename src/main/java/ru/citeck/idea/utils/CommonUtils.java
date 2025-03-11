package ru.citeck.idea.utils;

import java.util.function.Function;

public class CommonUtils {

    public static <U> Function<Object, U> filterAndCast(Class<? extends U> clazz) {
        return t -> clazz.isInstance(t) ? clazz.cast(t) : null;
    }
}
