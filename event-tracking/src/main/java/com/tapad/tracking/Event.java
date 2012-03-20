package com.tapad.tracking;

/**
 * A wrapper for all event information.
 */
class Event {
    private String id;
    private String extraParameters;

    protected Event(String id, String extraParameters) {
        this.id = id;
        this.extraParameters = extraParameters;
    }
    protected Event(String id) {
        this(id, null);
    }

    public String getId() {
        return id;
    }

    public String getExtraParameters() {
        return extraParameters;
    }

    @Override
    public String toString() {
        return "Event[id=" + id + "]";
    }
}
