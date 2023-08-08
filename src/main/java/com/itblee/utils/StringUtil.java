package com.itblee.utils;

public final class StringUtil {

    public StringUtil() {
        throw new AssertionError();
    }

    public static boolean isBlank(final CharSequence cs) {
        if (cs == null || cs.length() == 0)
            return true;
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isWhitespace(cs.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static String requireNonBlank(final String cs) {
        if (isBlank(cs))
            throw new IllegalArgumentException();
        return cs;
    }

    public static boolean containsBlank(final CharSequence[] arr) {
        if (arr == null || arr.length == 0)
            return true;
        for (CharSequence charSequence : arr) {
            if (isBlank(charSequence))
                return true;
        }
        return false;
    }

    public static boolean containsIgnoreCase(String str, String search)     {
        if(str == null || search == null)
            return false;
        final int length = search.length();
        if (length == 0)
            return true;
        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, search, 0, length))
                return true;
        }
        return false;
    }

}
