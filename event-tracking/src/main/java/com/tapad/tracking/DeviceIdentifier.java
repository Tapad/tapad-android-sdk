package com.tapad.tracking;

import java.util.List;

import com.tapad.tracking.deviceidentification.TypedIdentifier;

/**
 * Locator for a the device identifier. Used to ensure opt-out
 * is honored immediately while avoiding static coupling.
 */
public interface DeviceIdentifier {

    /**
     * Get the primary id for the device.
     */
    String get();

    /**
     * Get the full list of all collected device identifiers with type information.
     */
    String getTypedIds();

    /**
     * Check if the user has opted out of personalization.
     */
    boolean isOptedOut();
}

