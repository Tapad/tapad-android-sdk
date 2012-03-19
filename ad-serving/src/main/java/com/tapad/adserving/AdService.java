package com.tapad.adserving;

import android.content.Context;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import java.util.UUID;

/**
 * Initialization touch point
 */
public class AdService {
    private static final String PREF_TAPAD_DEVICE_ID = "_tapad_device_id";
    private static AdRequestDispatcher dispatcher;

    /**
     * Initializes the ad serving API with a publisherId and propertyId
     *
     * @param context a context reference
     * @param publisherId the publisher id as specified by Tapad
     * @param propertyId
     */
    public static void init(Context context, String publisherId, String propertyId) {
        WebView wv = new WebView(context);
        String userAgent = wv.getSettings().getUserAgentString();
        wv.destroy();

        String deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TAPAD_DEVICE_ID, null);
        boolean firstRun = deviceId == null;
        if (firstRun) {
            deviceId = UUID.randomUUID().toString();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
        }

        AdResource resource = new AdResource(deviceId, publisherId, propertyId, userAgent);
        dispatcher = new AdRequestDispatcher(resource, 2);
    }

    public static void getAd(AdRequest adRequest) {
        dispatcher.dispatch(adRequest);
    }
}
