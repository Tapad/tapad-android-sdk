package com.tapad.adserving;

/**
 * Encapsulates the request parameters needed to fetch an ad
 */
public abstract class AdRequest {

    private String placementId;
    private boolean wrapWithHtml = false;
    private AdSize size;

    /**
     * Constructs a new AdRequest.
     *
     * @param placementId the target placement id assigned by Tapad / Swappit.
     */
    public AdRequest(String placementId, AdSize size, Boolean wrapWithHtml) {
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
