package com.tapad.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.tapad.tracking.deviceidentification.*;
import com.tapad.util.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static String typedDeviceIds;
    private static IdentifierSource idCollector = new IdentifierSourceAggregator(defaultIdSources());
    private static DeviceIdentifier deviceIdLocator = new DeviceIdentifier() {
        @Override
        public String get() {
            return deviceId;
        }

        @Override
        public String getTypedIds() {
            return typedDeviceIds;
        }

        @Override
        public boolean isOptedOut() {
            return Tracking.isOptedOut();
        }
    };

    /**
     * Initializes the tracking API with application id as specified in AndroidManifest.xml:
     * <p/>
     * <application>
     * <meta-data android:name="tapad.APP_ID" android:value="INSERT_APP_ID_HERE"/>
     * ...
     * </application>
     * <p/>
     * The default id sources are AndroidId, PhoneId, and WifiMac, but
     * this can be configured to suit the developer's privacy policy through the AndroidManifest.xml:
     * <p/>
     * <application>
     * <meta-data android:name="tapad.ID_SOURCES" android:value="AndroidId,PhoneId,WifiMac"/>
     * ...
     * </application>
     *
     * @param context a context reference
     */
    public static void init(Context context) {
        init(context, null, null);
    }

    /**
     * Initializes the tracking API using the supplied application id. If the
     * supplied value is null or consist only of white space, then the AndroidManifest.xml
     * values are used (@see #init(android.content.Context)).
     * <p/>
     * If the idSources is null or empty, then the AndroidManifest.xml values are used (@see #init(android.content.Context)).
     * <p/>
     * One of the initialization functions must be called before TrackingService.get().
     *
     * @param context   a context reference
     * @param appId     the application identifier
     * @param idSources a list of identifier sources to use to collect ids
     * @see #init(android.content.Context)
     */
    public static void init(Context context, String appId, List<IdentifierSource> idSources) {
        setupAPI(context, appId, idSources);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // The install event may have been sent by the InstallReferrerReceiver,
        // so first-run and install are not always sent at the same time.
        // Since 3.x, the marketplace behavior has been to fire the INSTALL_REFERRER intent
        // after first launch.  So we are leaving FIRST_RUN here and letting the InstallReferrerReceiver
        // fire the INSTALL event.  Otherwise, we will either get two install events or one without the
        // referrer value, which is useful for determining proper attribution.
        if (!prefs.getBoolean(PREF_FIRST_RUN_SENT, false)) {
            get().onEvent(EVENT_FIRST_RUN);
            prefs.edit().putBoolean(PREF_FIRST_RUN_SENT, true).commit();
        }
    }

    /**
     * Configures the API.
     */
    protected static void setupAPI(Context context, String appId, List<IdentifierSource> idSources) {
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

                if (idSources == null || idSources.isEmpty()) {
                    try {
                        ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                        String[] idSourceClasses = ai.metaData.getString("tapad.ID_SOURCES").split(",");
                        idSources = new ArrayList<IdentifierSource>();
                        for (String className : idSourceClasses) {
                            try {
                                idSources.add((IdentifierSource) Class.forName("com.tapad.tracking.deviceidentification." + className.trim()).newInstance());
                            } catch (Exception e) {
                                Logging.warn("Tracking", "Unable to instantiate identifier source: " + className.trim());
                            }
                        }
                        if (idSources.isEmpty()) {
                            idSources = defaultIdSources();
                        }
                    } catch (Exception e) {
                        idSources = defaultIdSources();
                    }
                }

                idCollector = new IdentifierSourceAggregator(idSources);
                collectIds(context);

                service = new TrackingServiceImpl(
                        new EventDispatcher(new EventResource(appId, deviceIdLocator, DeviceInfo.getUserAgent(context)))
                );
            }
        }
    }

    /**
     * Creates the default identifier sources to use should none be specified.
     * The default is all.
     *
     * @return the list of default id sources
     */
    private static List<IdentifierSource> defaultIdSources() {
        return Arrays.asList(new AndroidId(), new PhoneId(), new WifiMac());
    }

    /**
     * Uses the idCollector to generate ids, if any.  This is not done if the user is already opted out through
     * preferences.  If there were no ids generated, a random UUID is generated and persisted through
     * preferences.
     *
     * @param context context object used to find/collect/persist ids
     */
    private static void collectIds(Context context) {
        deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TAPAD_DEVICE_ID, null);
        // do not attempt to collect any ids if the device is opted out
        if (OPTED_OUT_DEVICE_ID.equals(deviceId)) {
            typedDeviceIds = null;
        } else {
            // collect ids
            List<TypedIdentifier> ids = idCollector.get(context);
            // if no ids
            if (ids.isEmpty()) {
                // generate and store a new id if there is no saved id
                if (deviceId == null) {
                    Logging.warn("Tracking", "Unable to retrieve any device identifiers, using a UUID instead.");
                    deviceId = UUID.randomUUID().toString();
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_TAPAD_DEVICE_ID, deviceId).commit();
                }
                // ensure that typed id is set to null
                typedDeviceIds = null;
            } else {
                // set the deviceId to the first typed id, but don't save it in prefs because that space is reserved for the generated UUID/Opt-out
                deviceId = ids.get(0).getValue();
                // set the typedDeviceIds to the full string representation
                typedDeviceIds = TextUtils.join(",", ids);
            }
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
        typedDeviceIds = null;
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
        // we clear the saved preferences and run through id collection logic once more
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(PREF_TAPAD_DEVICE_ID)
                .commit();
        collectIds(context);
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
