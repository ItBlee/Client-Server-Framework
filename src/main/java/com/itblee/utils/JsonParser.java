package com.itblee.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Optional;


public final class JsonParser {

    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public JsonParser() {
        throw new AssertionError();
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static String toJson(Object obj, Type type) {
        return GSON.toJson(obj, type);
    }


    public static String toPrettyJson(Object obj) {
        return PRETTY_GSON.toJson(obj);
    }

    public static <T> Optional<T> fromJson(String json, Class<T> tClass) {
        return Optional.ofNullable(GSON.fromJson(json, tClass));
    }

    public static <T> Optional<T> fromJson(String json, Type type) {
        return Optional.ofNullable(GSON.fromJson(json, type));
    }

}
