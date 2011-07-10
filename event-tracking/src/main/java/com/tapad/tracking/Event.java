package com.tapad.tracking;

/**
 * A wrapper for all event information. As of now, just an id.
 */
class Event {
    private String id;

    protected Event(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Event[id=" + id + "]";
    }
}
