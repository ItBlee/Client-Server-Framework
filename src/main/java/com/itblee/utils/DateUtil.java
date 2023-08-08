package com.itblee.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
    private static final DateFormat DAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static String dateToString(Date date) {
        if (date == null)
            return "";
        return DATE_FORMAT.format(date);
    }

    public static Date stringToDate(String s) {
        if (StringUtil.isBlank(s))
            return null;
        try {
            return DATE_FORMAT.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String DateToDayString(Date date) {
        if (date == null)
            return "";
        return DAY_FORMAT.format(date);
    }

    public static String DateToTimeString(Date date) {
        if (date == null)
            return "";
        return TIME_FORMAT.format(date);
    }

    public static boolean sameDate(Date date1, Date date2) {
        return DAY_FORMAT.format(date1).equals(DAY_FORMAT.format(date2));
    }

    public static long between(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            throw new IllegalArgumentException();
        long d1 = date1.getTime();
        long d2 = date2.getTime();
        return d1 > d2 ? d1 - d2 : d2 - d1;
    }

}
