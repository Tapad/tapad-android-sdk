package com.tapad.tracking.deviceidentification;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of identifier sources and asks each for identifiers in turn.
 */
public class IdentifierSourceAggregator implements IdentifierSource {
    private List<IdentifierSource> sourceDelegates = new ArrayList<IdentifierSource>();

    public IdentifierSourceAggregator() { }
    public IdentifierSourceAggregator(List<IdentifierSource> sources) { this.sourceDelegates.addAll(sources); }

    public void addIdentifierSource(IdentifierSource source) { this.sourceDelegates.add(source); }

    @Override
    public List<TypedIdentifier> get(Context context) {
        List<TypedIdentifier> aggregateIds = new ArrayList<TypedIdentifier>();
        for (int i=0; i<this.sourceDelegates.size(); i++) {
            aggregateIds.addAll(this.sourceDelegates.get(i).get(context));
        }
        return (aggregateIds);
    }
}
