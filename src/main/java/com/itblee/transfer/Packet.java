package com.itblee.transfer;

import com.itblee.utils.JsonParser;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

public final class Packet implements Serializable, Cloneable {
    private String request;
    private String code;
    private String message;
    private final Map<String, String> data = new HashMap<>();

    public String getHeader() {
        return request;
    }

    public <T extends Enum<T> & Request> T getHeader(Class<T> tClass) {
        return T.valueOf(tClass, request);
    }

    public void setHeader(Request request) {
        if (request != null)
            this.request = request.toString();
    }

    public String getCode() {
        return code;
    }

    public <T extends Enum<T> & StatusCode> T getCode(Class<T> tClass) {
        return T.valueOf(tClass, code);
    }

    public void setCode(StatusCode code) {
        if (code != null)
            this.code = code.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String get(DataKey key) {
        return key != null ? data.get(key.toString()) : null;
    }

    public <T> Optional<T> get(DataKey key, Class<T> tClass) {
        return JsonParser.fromJson(get(key), tClass);
    }

    public <T> Optional<T> get(DataKey key, Type type) {
        return JsonParser.fromJson(get(key), type);
    }

    public void putData(DataKey key, Object value) {
        if (key == null)
            return;
        if (value instanceof CharSequence || value instanceof UUID)
            data.put(key.toString(), value.toString());
        else data.put(key.toString(), JsonParser.toJson(value));
    }

    public void putData(DataKey key, Object value, Type type) {
        if (key == null)
            return;
        data.put(key.toString(), JsonParser.toJson(value, type));
    }

    public boolean is(StatusCode code) {
        return Objects.equals(this.code, code.toString());
    }

    public boolean is(Request request) {
        return Objects.equals(this.request, request.toString());
    }

    public Packet clear() {
        request = null;
        code = null;
        data.clear();
        message = null;
        return this;
    }

    @Override
    public Packet clone() {
        try {
            return (Packet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
