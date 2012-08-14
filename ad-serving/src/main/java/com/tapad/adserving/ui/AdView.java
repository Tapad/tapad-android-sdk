package com.tapad.adserving.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.tapad.adserving.AdRequest;
import com.tapad.adserving.AdResponse;
import com.tapad.adserving.AdServing;
import com.tapad.adserving.AdSize;

/**
 * A view for automatic ad display. Call AdView.start() to start loading
 * ads.
 */
public class AdView extends LinearLayout {

    private WebView webView;

    private String placementId;
    private AdSize size;
    private CountDownTimer timer;
    private int refreshIntervalSeconds;

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        // Create the nested WebView
        webView = new WebView(context);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setScrollContainer(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // Configure it
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLightTouchEnabled(true);
        settings.setNeedInitialFocus(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Intercept page loads so we do not show the ad until all resources
                // are locally available.
                onMarkupLoaded();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Intercept location updates to trigger OS browser.
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        };
        webView.setWebViewClient(client);

        // Set up focus listener which makes sure that we don't refresh
        // an ad while the user is interacting with it.
        webView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (view == webView && focus) {
                    stopRefreshTimer();
                } else {
                    startRefreshTimer();
                }
            }
        });
        addView(webView);
    }

    /**
     * Gets the nested WebView which is used for rendering the ad markup.
     *
     * @return the WebView
     */
    public WebView getWebView() {
        return webView;
    }

    /**
     * Start loading ads. Should be called from Activity.onResume().
     *
     * @param placementId            the placement id as provided by Tapad
     * @param size                   the placement size in device independent pixels
     * @param refreshIntervalSeconds the number of seconds between each ad refresh
     */
    public void start(String placementId, AdSize size, int refreshIntervalSeconds) {
        this.placementId = placementId;
        this.size = size;
        this.refreshIntervalSeconds = refreshIntervalSeconds;

        startRefreshTimer();
    }

    /**
     * Stop the refresh timer. Should be called from Activity.onPause().
     */
    public void stopRefreshTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Updates the WebView markup. This method is automatically invoked on the UI thread
     * when ad markup has been loaded and is ready for rendering in the web view.
     * This method can be overridden to inject custom tags into the markup.
     *
     * @param markup the markup to render
     */
    protected void showMarkup(final String markup) {
        webView.loadDataWithBaseURL(null, markup, "text/html", "utf-8", null);
    }

    /**
     * Called when the markup is fully loaded. This method can be override to
     * for instance, insert custom animation when the load is done.
     */
    protected void onMarkupLoaded() {
        setVisibility(VISIBLE);
    }

    /**
     * Invoked whenever there is no markup available to render. This is usually
     * due to lack of network connectivity or other errors. The default
     * behaviour is to hide the view completely.
     */
    protected void onNoMarkupAvailable() {
        setVisibility(GONE);
    }

    /**
     * Start the refresh timer.
     */
    protected void startRefreshTimer() {
        if (timer != null) stopRefreshTimer();
        timer = new CountDownTimer(Long.MAX_VALUE, refreshIntervalSeconds * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                loadAd();
            }

            @Override
            public void onFinish() {
            }
        };
        timer.start();
    }


    /**
     * Invokes the actual ad load.
     */
    protected void loadAd() {
        AdServing.get().requestAd(new AdRequest(placementId, size) {
            @Override
            protected void onResponse(final AdResponse response) {

                Runnable action = null;

                switch (response.getResponseCode()) {
                    case AdResponse.OK:
                        action = new Runnable() {
                            @Override
                            public void run() {
                                showMarkup(response.getMarkup());
                            }
                        };
                        break;
                    default:
                        action = new Runnable() {
                            @Override
                            public void run() {
                                onNoMarkupAvailable();
                            }
                        };
                }

                webView.post(action);

            }
        });
    }

}
