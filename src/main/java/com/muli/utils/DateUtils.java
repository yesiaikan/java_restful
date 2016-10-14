package com.muli.utils;

/**
 * Created by admin on 2014/10/8.
 */
public class DateUtils {


    private static   java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
    public static String  getCurrentDay() {
        java.util.Date currTime = new java.util.Date();
        return formatter.format(currTime);
    }
}

