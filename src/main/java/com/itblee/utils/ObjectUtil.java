package com.itblee.utils;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public final class ObjectUtil {

    public ObjectUtil() {
        throw new AssertionError();
    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NoSuchElementException();
        return obj;
    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NoSuchElementException(message);
        return obj;
    }

    public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null)
            throw new NoSuchElementException(messageSupplier.get());
        return obj;
    }

}
