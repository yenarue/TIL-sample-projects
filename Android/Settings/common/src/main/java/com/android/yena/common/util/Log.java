package com.android.yena.common.util;

/**
 * Created by Yena on 2016-11-02.
 */
public class Log {
    private static final String APP_TAG = "YenaSample";
    private Log() {}

    public static void v(String TAG, String message) {
        android.util.Log.v(APP_TAG, "[" + TAG + "] " + message);
    }

    public static void d(String TAG, String message) {
        android.util.Log.d(APP_TAG, "[" + TAG + "] " + message);
    }

    public static void i(String TAG, String message) {
        android.util.Log.i(APP_TAG, "[" + TAG + "] " + message);
    }

    public static void e(String TAG, String message) {
        android.util.Log.e(APP_TAG, "[" + TAG + "] " + message);
    }

    public static void wtf(String TAG, String message) {
        android.util.Log.wtf(APP_TAG, "[" + TAG + "] " + message);
    }
}
