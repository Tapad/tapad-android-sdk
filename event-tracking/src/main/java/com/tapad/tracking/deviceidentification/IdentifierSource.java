package com.tapad.tracking.deviceidentification;

import android.content.Context;

import java.util.List;

/**
 * Produces identifiers
 */
public interface IdentifierSource {
    /**
     * @return An collection of identifiers this source is able to produce
     */
    List<TypedIdentifier> get(Context context);
}
