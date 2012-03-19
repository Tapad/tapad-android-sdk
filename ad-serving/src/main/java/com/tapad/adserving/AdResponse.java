package com.tapad.adserving;

/**
 * Represents the result of 
 */
public class AdResponse {
    public static final int OK = 200;
    public static final int NO_AD_AVAILABLE = 204;
    public static final int OTHER_ERROR = 100;

    private int responseCode;
    private String markup;
    private String message;

    AdResponse(int responseCode, String markup, String message) {
        this.responseCode = responseCode;
        this.markup = markup;
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMarkup() {
        return markup;
    }

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
        return new AdResponse(OTHER_ERROR, null, message);
    }
}
