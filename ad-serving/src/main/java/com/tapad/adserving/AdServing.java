package com.tapad.adserving;

import android.content.Context;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import com.tapad.tracking.Tracking;

import java.util.UUID;

/**
 * Main entry point for ad serving API users. Provides API initialization as well
 * as access to the ad serving service which allows for fetching raw HTML markup.
 *
 * The init method must be invoked regardless of whether the managed ad views
 * are used or if the API user want to access the markup directly.
 */
public class AdServing {
    private static final String PREF_TAPAD_DEVICE_ID = "_tapad_device_id";
    private static AdServingService service;

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

        Tracking.init(context, propertyId);
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
