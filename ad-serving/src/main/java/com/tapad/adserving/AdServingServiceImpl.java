package com.tapad.adserving;

/**
 * Default implementation of the AdServingService.
 */
class AdServingServiceImpl implements AdServingService {
    private AdRequestDispatcher dispatcher;

    AdServingServiceImpl(AdRequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void requestAd(AdRequest adRequest) {
        dispatcher.dispatch(adRequest);
    }
}