package com.tapad.tracking.deviceidentification;

/**
 * An identifier value packaged with a type
 */
public class TypedIdentifier {

    /**
     * List of known identifier types
     */
    public static final String TYPE_RANDOM_UUID = "100";
    public static final String TYPE_ANDROID_ID_SHA1 = "101";
    public static final String TYPE_ANDROID_ID_MD5 = "102";
    public static final String TYPE_PHONE_ID_SHA1 = "103";
    public static final String TYPE_PHONE_ID_MD5 = "104";
    public static final String TYPE_WIFI_MAC_SHA1 = "105";
    public static final String TYPE_WIFI_MAC_MD5 = "106";

    private String type = null;
    private String value = null;

    public TypedIdentifier(String type, String value) {
        this.type = type;
        this.value = value;
    }
    /**
     * @return the type of the identifier
     */
    public String getType() { return (this.type); }

    /**
     * @return the value of the identifier
     */
    public String getValue() { return (this.value); }

    /**
     * @return the single string representation
     */
    public String toString() { return (this.type + ":" + this.value); }
}
