package com.tapad.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.tapad.adserving.AdRequest;
import com.tapad.adserving.AdResponse;
import com.tapad.adserving.AdServing;
import com.tapad.adserving.AdSize;

public class ManualMarkupActivity extends Activity {
    private TextView markupView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_ad_markup);

        markupView = (TextView) findViewById(R.id.markup);

        Button adMarkup = (Button) findViewById(R.id.pull_ad_markup);
        adMarkup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                loadMarkup();
            }
        });
    }

    private void loadMarkup() {
        AdServing.get().requestAd(new AdRequest(SampleConstants.PLACEMENT_ID, AdSize.S320x50, false) {
            @Override
            protected void onResponse(final AdResponse response) {
                markupView.post(new Runnable() {
                    @Override
                    public void run() {
                        handleResponse(response);
                    }
                });
            }
        });
    }

    private void handleResponse(AdResponse response) {
        switch (response.getResponseCode()) {
            case AdResponse.OK:
                markupView.setText(response.getMarkup());
                break;
            case AdResponse.NO_AD_AVAILABLE:
                markupView.setText("No markup available for this publisher, placement and / or size.");
                break;
            case AdResponse.ERROR:
                markupView.setText("Unable to load markup: " + response.getMessage());
        }
    }
}