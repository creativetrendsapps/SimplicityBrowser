package com.creativetrends.app.simplicity.utils;

public class Cleaner {

    // "clean" and decode an url, all in one
    public static String cleanAndDecodeUrl(String url) {
        return decodeUrl(cleanUrl(url));
    }

    // "clean" an url and remove Facebook tracking redirection
    private static String cleanUrl(String url) {
        return url.replace("http://lm.facebook.com/l.php?u=", "")
                .replace("https://lm.facebook.com/l.php?u=", "")
                .replace("http://m.facebook.com/l.php?u=", "")
                .replace("https://m.facebook.com/l.php?u=", "")
                .replace("http://0.facebook.com/l.php?u=", "")
                .replace("https://0.facebook.com/l.php?u=", "")
                .replaceAll("&h=.*", "").replaceAll("\\?acontext=.*", "");
    }

    // url decoder, recreate all the special characters
    private static String decodeUrl(String url) {
        return url.replace("%3C", "<").replace("%3E", ">").replace("%23", "#").replace("%25", "%")
                .replace("%7B", "{").replace("%7D", "}").replace("%7C", "|").replace("%5C", "\\")
                .replace("%5E", "^").replace("%7E", "~").replace("%5B", "[").replace("%5D", "]")
                .replace("%60", "`").replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
                .replace("%3A", ":").replace("%40", "@").replace("%3D", "=").replace("%26", "&")
                .replace("%24", "$").replace("%2B", "+").replace("%22", "\"").replace("%2C", ",")
                .replace("%20", " ");
    }



}

