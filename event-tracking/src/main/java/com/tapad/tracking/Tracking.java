package com.tapad.tracking;

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
    private static String deviceId;

    /**
     * Initializes the tracking API using the app package name as the id.
     *
     * One of the initialization functions must be called before TrackingService.get().
     *
     * @param context
     */
    public static void init(Context context) {
        init(context, null);
    }


    /**
     * Initializes the tracking API using the supplied value as the application id.  If the
     * supplied value is null or consist only of white space, then the app package name is
     * used.
     *
     * One of the initialization functions must be called before TrackingService.get().
     *
     * @param context
     * @param appId the application identifier
     */
    public static void init(Context context, String appId) {
        synchronized(Tracking.class) {
            if (service == null) {
                if (appId == null || appId.trim().length() == 0) {
                  appId = context.getApplicationContext().getPackageName();
                }

                deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TAPAD_DEVICE_ID, null);
                boolean firstRun = deviceId == null;
                if (firstRun) {
                    deviceId = UUID.randomUUID().toString();
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
                }

                service = new TrackingServiceImpl(new EventDispatcher(new EventResource(appId, deviceId)));

                if (firstRun) {
                    service.onEvent("install");
                }
            }
        }
    }
    
    public static String getDeviceId() {
        if (service == null) throw new IllegalStateException("Please call Tracking.init(context) to initialize the API first!");
        else return deviceId;
    }

    public static TrackingService get() {
        if (service == null) throw new IllegalStateException("Please call Tracking.init(context) to initialize the API first!");
        else return service;
    }

}
