package com.tapad.tracking;

/**
 * Implementation of the tracking service.
 */
class TrackingServiceImpl implements TrackingService {

    private EventDispatcher dispatcher;

    protected TrackingServiceImpl(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void onEvent(String eventId) {
        dispatcher.dispatch(new Event(eventId));
    }
}
