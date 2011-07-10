package com.tapad.tracking;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import java.util.UUID;

/**
 * Public entry
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Tracking {
    private static final String PREF_TAPAD_DEVICE_ID = "_tapad_device_id";
    private static TrackingService service = null;

    /**
     * Initializes the tracking API. Must be called before TrackingService.get().
     * @param context
     */
    public static void init(Context context) {
        String appId = context.getApplicationContext().getPackageName();

        String deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TAPAD_DEVICE_ID, null);
        boolean firstRun = deviceId == null;
        if (firstRun) {
            deviceId = UUID.randomUUID().toString();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
        }

        service = new TrackingServiceImpl(new EventDispatcher(new EventResource(appId, deviceId)));

        if (firstRun) {
            service.onEvent("install");
            service.onEvent("first-run");
        }
    }


    public static TrackingService get() {
        if (service == null) throw new IllegalStateException("Please call Tracking.init(context) to initialize the API first!");
        return service;
    }

}
