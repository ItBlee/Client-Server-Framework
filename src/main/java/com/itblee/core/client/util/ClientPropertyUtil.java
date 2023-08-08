package com.itblee.core.client.util;

import java.util.ResourceBundle;

public final class ClientPropertyUtil {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("client.Client");

    public ClientPropertyUtil() {
        throw new AssertionError();
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(BUNDLE.getString(key));
    }

    public static Object getObject(String key) {
        return BUNDLE.getObject(key);
    }

}
