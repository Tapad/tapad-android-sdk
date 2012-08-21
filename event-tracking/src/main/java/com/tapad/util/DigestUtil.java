package com.tapad.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Convenience methods for digest hashing data
 */
public class DigestUtil {

    public static String md5Hash(String source) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(source.getBytes(), 0, source.length());
        return String.format("%032X", new BigInteger(1, digest.digest()));
    }

    public static String sha1Hash(String source) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(source.getBytes(), 0, source.length());
        return String.format("%040X", new BigInteger(1, digest.digest()));
    }
}
