package com.tapad.adserving;

/**
 * Encapsulates the dimensions of the ad and definitions of the standard ad unit sizes.
 *
 * Please check with Tapad if you want to use ad sizes other than the ones defined
 * statically in this class.
 */
public class AdSize {

    public static final AdSize S320x50 = new AdSize(320, 50);
    public static final AdSize S300x50 = new AdSize(300, 50);
    public static final AdSize S300x250 = new AdSize(300, 250);

    private int width;
    private int height;

    public AdSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return getWidth() + "x" + getHeight();
    }
}
