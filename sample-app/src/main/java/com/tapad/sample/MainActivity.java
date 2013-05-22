package com.tapad.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.tapad.adserving.AdServing;
import com.tapad.tracking.Tracking;

public class MainActivity extends Activity {

    private TextView deviceId;
    private TextView typedDeviceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // If your app has only one Activity or can guarantee that this one is always started before any other
        // Activities, then you can initialize the SDKs here instead of declaring an Application
        // AdServing.init(this); // if you are using the AdServing SDK or AdServing + EventTracking SDKs
        // Tracking.init(this); // if you are only using the EventTracking SDK

        deviceId = (TextView) findViewById(R.id.device_id);
        typedDeviceId = (TextView) findViewById(R.id.typed_device_id);
        updateDeviceId();

        Button custom = (Button) findViewById(R.id.custom_event);
        custom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Tracking.get().onEvent("custom1");
            }
        });

        Button adMarkup = (Button) findViewById(R.id.pull_ad_markup);
        adMarkup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(MainActivity.this, ManualMarkupActivity.class));
            }
        });

        Button managedView = (Button) findViewById(R.id.managed_ad_view);
        managedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdViewActivity.class));
            }
        });

        Button optInOut = (Button) findViewById(R.id.opt_in_out);
        optInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tracking.isOptedOut())
                    Tracking.optIn(MainActivity.this);
                else
                    Tracking.optOut(MainActivity.this);

                updateDeviceId();
            }
        });
    }

    private void updateDeviceId() {
        deviceId.setText("Device ID: " + Tracking.getDeviceId().get());
        typedDeviceId.setText("Typed Device ID(s): " + Tracking.getDeviceId().getTypedIds());
    }
}
