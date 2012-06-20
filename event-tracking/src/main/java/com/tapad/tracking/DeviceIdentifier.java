package com.tapad.tracking;

/**
 * Locator for a the device identifier. Used to ensure opt-out
 * is honored immediately while avoiding static coupling.
 */
public interface DeviceIdentifier {
    String get();
}

