package com.tapad.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.tapad.adserving.AdRequest;
import com.tapad.adserving.AdResponse;
import com.tapad.adserving.AdService;
import com.tapad.adserving.AdSize;
import com.tapad.tracking.Logging;
import com.tapad.tracking.Tracking;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Or use Tracking.init(this) to use the application package id
        Tracking.init(this, "AndroidEventSDK_testapp");
        AdService.init(this, "18", null);

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
                AdService.getAd(new AdRequest("110", AdSize.S320x50) {
                    @Override
                    protected void onResponse(final AdResponse response) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Logging.info("ADS", response.getMarkup());
                            }
                        });
                    }
                });
            }
        });

        Button managedView = (Button) findViewById(R.id.managed_ad_view);
        managedView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdViewActivity.class));
            }
        });
    }
}
