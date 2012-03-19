package com.tapad.sample;

import android.app.Activity;
import android.os.Bundle;
import com.tapad.adserving.AdSize;
import com.tapad.adserving.ui.AdView;

public class AdViewActivity extends Activity {
    private AdView adViewTop;
    private AdView adViewBottom;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managed_ad_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adViewTop = (AdView) findViewById(R.id.ad_view_top);
        adViewTop.start("108", AdSize.S320x50, 10);

        adViewBottom = (AdView) findViewById(R.id.ad_view_bottom);
        adViewBottom.start("108", AdSize.S300x250, 10);
    }

    @Override
    protected void onPause() {
        adViewTop.stopRefreshTimer();
        adViewBottom.stopRefreshTimer();
        super.onPause();
    }
}