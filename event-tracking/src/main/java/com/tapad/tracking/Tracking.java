package com.tapad.tracking;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.tapad.util.Logging;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Public entry
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Tracking {
    private static final String PREF_TAPAD_DEVICE_ID = "_tapad_device_id";
    private static final String OPTED_OUT_DEVICE_ID = "OptedOut";

    private static TrackingService service = null;
    private static String deviceId;
    private static DeviceIdentifier deviceIdLocator = new DeviceIdentifier() {
        @Override
        public String get() {
            return deviceId;
        }
    };

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
                    deviceId = getHashedDeviceId(context);
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
                }

                service = new TrackingServiceImpl(new EventDispatcher(new EventResource(appId, deviceIdLocator)));

                if (firstRun) {
                    service.onEvent("install");
                }
            }
        }
    }

    /**
     * Gets the Android system ID hashed with MD5 and formatted as a 32 byte hexadecimal number.
     * This is the same device identifier that most of the ad networks use.
     *
     * Falls back to generating a UDID in the (extremely) unlikely case that MD5 is not available
     * or (more likely) that the ANDROID_ID setting becomes a privileged op.
     *
     * @return the device identifier
     */
    private static String getHashedDeviceId(Context context) {
        String deviceId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(deviceId.getBytes(), 0, deviceId.length());
            return String.format("%032X", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            Logging.warn("Tracking", "Error retrieving device identifier, using a UDID instead.");
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Opts the device out of all tracking / personalization by setting the device id to the constant
     * string OptedOut. This means that it is now impossible to distinguish this device from all
     * other opted out device.
     *
     * @param context
     */
    public static void optOut(Context context) {
        deviceId = OPTED_OUT_DEVICE_ID;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_TAPAD_DEVICE_ID, deviceId)
                .commit();
    }

    /**
     * Opts the device back in after an opt out.
     * @param context
     */
    public static void optIn(Context context) {
        deviceId = getHashedDeviceId(context);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_TAPAD_DEVICE_ID, deviceId)
                .commit();
    }

    private static void assertInitialized() {
        if (service == null) throw new IllegalStateException("Please call Tracking.init(context) to initialize the API first!");
    }

    /**
     * Gets device identifier locator used by the Tracking API.
     *
     * @return the identifier locator
     */
    public static DeviceIdentifier getDeviceId() {
        assertInitialized();
        return deviceIdLocator;
    }

    /**
     * Checks if the device is opted out of tracking. Note that the opt out is enforced by the API itself,
     * so this check is just for UI purposes (e.g, determine if the opt out checkbox should be checked or not).
     *
     * @return true if the device is opted out
     */
    public static boolean isOptedOut() {
        assertInitialized();
        return OPTED_OUT_DEVICE_ID.equals(deviceId);
    }

    public static TrackingService get() {
        assertInitialized();
        return service;
    }
}
