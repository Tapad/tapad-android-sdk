package com.tapad.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.tapad.util.Logging;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Public entry-point to the tracking API.
 */
public class Tracking {
    protected static final String PREF_TAPAD_DEVICE_ID = "_tapad_device_id";
    protected static final String PREF_INSTALL_SENT = "_tapad_install_sent";
    protected static final String PREF_FIRST_RUN_SENT = "_tapad_first_run_sent";

    protected static final String EVENT_INSTALL = "install";
    protected static final String EVENT_FIRST_RUN = "first-run";

    public static final String OPTED_OUT_DEVICE_ID = "OptedOut";


    private static TrackingService service = null;
    private static String deviceId;
    private static DeviceIdentifier deviceIdLocator = new DeviceIdentifier() {
        @Override
        public String get() {
            return deviceId;
        }
    };

    /**
     * Initializes the tracking API with application id as specified in AndroidManifest.xml:
     * <p/>
     * <application>
     * <meta-data android:name="tapad.APP_ID" android:value="INSERT_APP_ID_HERE"/>
     * ...
     * </application>
     *
     * @param context a context reference
     */
    public static void init(Context context) {
        init(context, null);
    }

    /**
     * Initializes the tracking API using the supplied application id. If the
     * supplied value is null or consist only of white space, then the AndroidManifest.xml
     * values are used (@see #init(android.content.Context)).
     * <p/>
     * One of the initialization functions must be called before TrackingService.get().
     *
     * @param context a context reference
     * @param appId   the application identifier
     * @see #init(android.content.Context)
     *
     */
    public static void init(Context context, String appId) {
        setupAPI(context, appId);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // This event may have been sent by the InstallReferrerReceiver,
        // so first-run and install are not always sent at the same time.
        if (!prefs.getBoolean(PREF_INSTALL_SENT, false)) {
            get().onEvent(EVENT_INSTALL);
            prefs.edit().putBoolean(PREF_INSTALL_SENT, true).commit();
        }
        if (!prefs.getBoolean(PREF_FIRST_RUN_SENT, false)) {
            get().onEvent(EVENT_FIRST_RUN);
            prefs.edit().putBoolean(PREF_FIRST_RUN_SENT, true).commit();
        }
    }

    /**
     * Configures the API.
     */
    protected static void setupAPI(Context context, String appId) {
        synchronized (Tracking.class) {
            if (service == null) {

                if (appId == null || appId.trim().length() == 0) {
                    try {
                        ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                        appId = ai.metaData.getString("tapad.APP_ID");
                    } catch (Exception e) {
                        throw new RuntimeException("No app id specified and unable to read tapad.APP_ID from AndroidManifest.xml", e);
                    }
                }

                deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TAPAD_DEVICE_ID, null);
                if (deviceId == null) {
                    deviceId = getHashedDeviceId(context);
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
                }

                service = new TrackingServiceImpl(
                        new EventDispatcher(new EventResource(appId, deviceIdLocator, DeviceInfo.getUserAgent(context)))
                );
            }
        }
    }

    /**
     * Gets the Android system ID hashed with MD5 and formatted as a 32 byte hexadecimal number.
     * This is the same device identifier that most of the ad networks use.
     * <p/>
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
     * @param context a context reference
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
     *
     * @param context a context reference
     */
    public static void optIn(Context context) {
        deviceId = getHashedDeviceId(context);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_TAPAD_DEVICE_ID, deviceId)
                .commit();
    }

    private static void assertInitialized() {
        if (service == null)
            throw new IllegalStateException("Please call Tracking.init(context) to initialize the API first!");
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
