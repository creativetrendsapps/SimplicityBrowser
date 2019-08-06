package com.creativetrends.app.simplicity.utils;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pc on 07/16/2017.
 */

public class Utils {
    // utility function
    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // generate a hash
    public static String sha256(String s) {
        MessageDigest digest;
        String hash;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());

            hash = bytesToHexString(digest.digest());

            return hash;
        } catch (NoSuchAlgorithmException e1) {
            return s;
        }
    }

}
