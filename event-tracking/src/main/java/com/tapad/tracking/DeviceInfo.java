package com.tapad.tracking;

import android.content.Context;
import android.webkit.WebView;

public class DeviceInfo {

    /**
     * Gets the device's browser user agent.
     */
    public static String getUserAgent(Context context) {
        WebView wv = new WebView(context);
        String userAgent = wv.getSettings().getUserAgentString();
        wv.destroy();
        return userAgent;
    }
}
