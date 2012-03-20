package com.tapad.adserving;

/**
 * Public interface to the ad serving service for developers who want access to the
 * raw HTML markup. If a managed ad view is preferred, please use an AdView instance instead.
 * <p/>
 * An instance of this service can be retrieved byt invoking AdService.get().
 *
 * @see AdServing
 * @see com.tapad.adserving.ui.AdView
 */
public interface AdServingService {
    /**
     * Asynchronously request markup for an ad. This method may be called from the UI thread,
     * but the adRequest.onResponse() method will be invoked on a background thread.
     *
     * @param adRequest the request to invoke
     */
    public void requestAd(AdRequest adRequest);
}
