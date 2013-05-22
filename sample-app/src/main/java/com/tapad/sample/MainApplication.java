package com.tapad.sample;

import android.app.Application;
import com.tapad.adserving.AdServing;

/**
 * If your app has multiple Activities and there is no guarantee that a particular one will always be started
 * before the others, this is the recommended place to initialize the AdServing or EventTracking SDK
 */
public class MainApplication extends Application {
    public void onCreate() {
        super.onCreate();
        AdServing.init(this);
        // AdServing.init calls the Tracking init code. If you are just using the Tracking API,
        // call Tracking.init(this) instead of the above.
    }
}
