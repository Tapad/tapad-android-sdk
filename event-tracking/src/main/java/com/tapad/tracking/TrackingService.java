package com.tapad.tracking;

/**
 * Tapad event tracking interface.
 */
public interface TrackingService {

    /**
     * Default event for application installs.
     */
    static final String EVENT_INSTALL = "install";

    /**
     * Default event for application launch.
     */
    static final String EVENT_LAUNCH = "launch";

    /**
     * Registers the specified event with the Tapad event tracking service. The
     * actual event dispatch is done asynchronously, so this method will never
     * block.
     *
     * @param eventId the id of the event, either one of the predefined constants, or developer specific ones
     */
    void onEvent(String eventId);
}
