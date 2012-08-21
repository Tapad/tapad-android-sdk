package com.tapad.adserving;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.webkit.WebView;
import com.tapad.tracking.Tracking;

/**
 * Main entry point for ad serving API users. Provides API initialization as well
 * as access to the ad serving service which allows for fetching raw HTML markup.
 *
 * The init method must be invoked regardless of whether the managed ad views
 * are used or if the API user want to access the markup directly.
 */
public class AdServing {
    private static AdServingService service;

    /**
     * Initializes the ad serving API with publisher and property ids as specified in
     * AndroidManifest.xml:
     *
     * <application>
     *  <meta-data android:name="swappit.PUBLISHER_ID" android:value="INSERT_PUBLISHER_ID_HERE"/>
     *  <meta-data android:name="swappit.PROPERTY_ID" android:value="INSERT_PROPERTY_ID_HERE"/>
     *  ...
     * </application>
     *
     * @param context     a context reference
     */
    public static void init(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object publisherId = ai.metaData.get("swappit.PUBLISHER_ID");
            if (publisherId == null) throw new RuntimeException("swappit.PUBLISHER_ID is not set in AndroidManifest.xml");

            Object propertyId = ai.metaData.get("swappit.PROPERTY_ID");
            if (propertyId== null) throw new RuntimeException("swappit.PROPERTY_ID is not set in AndroidManifest.xml");

            init(context, publisherId.toString(), propertyId.toString());
        } catch (Exception e) {
            throw new RuntimeException("Unable to read swappit.PUBLISHER_ID and swappit.PROPERTY_ID from AndroidManifest.xml");
        }

    }

    /**
    * Initializes the ad serving API with a publisherId and propertyId.
    *
    * @param context     a context reference
    * @param publisherId the publisher specific id as specified by Tapad
    * @param propertyId  the app specific id as specified by Tapad
    */
    public static void init(Context context, String publisherId, String propertyId) {
        WebView wv = new WebView(context);
        String userAgent = wv.getSettings().getUserAgentString();
        wv.destroy();

        Tracking.init(context, propertyId, null);
        AdResource resource = new AdResource(Tracking.getDeviceId(), publisherId, propertyId, userAgent);
        service = new AdServingServiceImpl(new AdRequestDispatcher(resource, 2));
    }

    /**
     * Get a reference to the ad serving service. Please invoke AdServing.init() first to initialize the ad
     * serving API.
     *
     * @return the ad serving service
     * @throws IllegalStateException if the API has not been initialized.
     */
    public static AdServingService get() {
        if (service == null)
            throw new IllegalStateException("Please call AdServing.init(context, publisherId, propertyId) to initialize the API first!");
        else return service;
    }
}
