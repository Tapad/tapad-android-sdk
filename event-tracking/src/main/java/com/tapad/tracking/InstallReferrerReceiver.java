package com.tapad.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.tapad.util.Logging;

import java.net.URLEncoder;

/**
 * Install referrer broadcast receiver for campaign tracking.
 * <p/>
 * <receiver android:name="com.tapad.tracking.InstallReferrerReceiver" android:exported="true">
 * <intent-filter>
 * <action android:name="com.android.vending.INSTALL_REFERRER" />
 * </intent-filter>
 * </receiver>
 *
 * To test referral:
 * <pre>
 *    adb shell
 *    am broadcast -a com.android.vending.INSTALL_REFERRER -n com.tapad.sample/com.tapad.tracking.InstallReferrerReceiver --es "referrer" "utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=test_content&utm_campaign=test_name"
 * </pre>
 */
public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getString("referrer") != null) {
            String referrerString = extras.getString("referrer");
            try {
                Tracking.setupAPI(context, null);
                Tracking.get().onEvent(Tracking.EVENT_INSTALL, "android_referrer=" + URLEncoder.encode(referrerString, "UTF-8"));
                // Register that the install event now has been sent
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(Tracking.PREF_INSTALL_SENT, true).commit();
            } catch (Exception e) {
                Logging.error("Tapad/InstallReferrerReceiver", "Error parsing referrer. Install event will not be sent. " + e.getMessage());
            }
        }
    }
}
