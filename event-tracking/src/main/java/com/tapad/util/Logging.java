package com.tapad.util;

import android.util.Log;

public class Logging {
    private static final boolean enabled = true;

    public static void info(String tag, String message) {
        if (enabled) Log.i(tag, message);
    }

    public static void warn(String tag, String message) {
        if (enabled) Log.w(tag, message);
    }

    public static void error(String tag, String message) {
        if (enabled) Log.e(tag, message);
    }

}
