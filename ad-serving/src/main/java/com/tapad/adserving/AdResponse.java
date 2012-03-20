package com.tapad.adserving;

/**
 * Represents the result of an ad request.
 */
public class AdResponse {
    /**
     * Ad was successfully returned.
     */
    public static final int OK = 200;
    /**
     * No ad available at this time.
     */
    public static final int NO_AD_AVAILABLE = 204;
    /**
     * An error (e.g network connectivity error) occurred when requesting the ad.
     */
    public static final int ERROR = 100;

    private int responseCode;
    private String markup;
    private String message;

    AdResponse(int responseCode, String markup, String message) {
        this.responseCode = responseCode;
        this.markup = markup;
        this.message = message;
    }

    /**
     * Returns the response code.
     * @return one of the defined response codes.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Returns the markup.
     * @return the markup if the responseCode is OK, or null otherwise.
     */
    public String getMarkup() {
        return markup;
    }

    /**
     * Returns the message associated with an error status.
     *
     * @return the error message if the responseCode is ERROR, or null otherwise.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "AdResponse[status=" + responseCode + ",markup=" + markup + "]";
    }

    static AdResponse success(String markup) {
        return new AdResponse(OK, markup, null);
    }

    static AdResponse noAdAvailable() {
        return new AdResponse(NO_AD_AVAILABLE, null, null);
    }
    
    static AdResponse error(String message) {
        return new AdResponse(ERROR, null, message);
    }
}
