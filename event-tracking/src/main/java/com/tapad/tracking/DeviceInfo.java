package com.tapad.tracking;

import android.content.Context;

public class DeviceInfo {

    /**
     * Check if the screen configuration indicates that this is a tablet.
     */
    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().screenLayout == 4; // SCREENLAYOUT_SIZE_XLARGE
    }

    public static String getUserAgent(Context context) {
        if (isTablet(context)) return "Android Tablet/TapadEventAPI/1.0";
        else return "Android Mobile/TapadEventAPI/1.0";
    }
}
