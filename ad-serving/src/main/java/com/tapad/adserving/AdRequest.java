package com.tapad.adserving;

/**
 * Encapsulates the request parameters needed to fetch an ad.
 */
public abstract class AdRequest {

    private String placementId;
    private boolean wrapWithHtml = false;
    private AdSize size;

    /**
     * Constructs a new AdRequest.
     *
     * @param placementId  the placement specific id assigned by Tapad / Swappit.
     * @param size         the requested ad dimensions
     * @param wrapWithHtml true if the markup should be wrapped with valid HTML document markup.
     *                     If false, only a HTML fragment will be returned.
     */
    public AdRequest(String placementId, AdSize size, boolean wrapWithHtml) {
        this.placementId = placementId;
        this.size = size;
        this.wrapWithHtml = wrapWithHtml;
    }

    public AdRequest(String placementId, AdSize size) {
        this(placementId, size, true);
    }

    public AdSize getSize() {
        return size;
    }

    public String getPlacementId() {
        return placementId;
    }

    public boolean isWrapWithHtml() {
        return wrapWithHtml;
    }

    protected abstract void onResponse(AdResponse response);

    @Override
    public String toString() {
        return "AdRequest[placementId=" + placementId + ", wrapWithHtml=" + wrapWithHtml + "]";
    }
}
