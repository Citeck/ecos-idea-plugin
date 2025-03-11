package ru.citeck.idea.utils;

import lombok.Getter;
import ru.citeck.idea.utils.func.UFunction;

public class Result<T> {

    private final T value;
    @Getter
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(String error) {
        return failure(new RuntimeException(error));
    }

    public static <T> Result<T> failure(String message, Throwable error) {
        return failure(new RuntimeException(message, error));
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isError(Class<? extends Throwable> type) {
        return type.isInstance(error);
    }

    public boolean isError() {
        return error != null;
    }

    public T getValue() {
        if (error != null) {
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
        return value;
    }

    public <O> Result<O> flatMap(UFunction<T, Result<O>> func) {
        if (isSuccess()) {
            try {
                return func.apply(value);
            } catch (Exception e) {
                return Result.failure(e);
            }
        }
        return Result.failure(error);
    }

    public <O> Result<O> map(UFunction<T, O> func) {
        if (isSuccess()) {
            try {
                return Result.success(func.apply(value));
            } catch (Exception e) {
                return Result.failure(e);
            }
        }
        return Result.failure(error);
    }
}
