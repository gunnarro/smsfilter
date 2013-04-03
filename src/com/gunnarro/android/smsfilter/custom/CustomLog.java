package com.gunnarro.android.smsfilter.custom;

import android.util.Log;

public class CustomLog {

    public static void i(Class<?> clazz, String msg) {
        Log.i(createTag(clazz), msg);
    }

    public static void d(Class<?> clazz, String msg) {
        Log.i(createTag(clazz), msg);
    }

    public static void e(Class<?> clazz, String msg) {
        Log.e(createTag(clazz), msg);
    }

    private static String createTag(Class<?> clazz) {
        StringBuffer tag = new StringBuffer();
        // tag.append(DateFormat.getDateInstance(DateFormat.MEDIUM,
        // Locale.ENGLISH).format(new Date())).append(" ");
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            if (stackTrace.getClassName().equals(clazz.getName())) {
                tag.append(clazz.getSimpleName()).append(".").append(stackTrace.getMethodName()).append(":").append(stackTrace.getLineNumber());
                break;
            }
        }
        return tag.toString();
    }

}
