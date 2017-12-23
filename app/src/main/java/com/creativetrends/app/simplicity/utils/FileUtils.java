package com.creativetrends.app.simplicity.utils;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Creative Trends Apps.
 */

public class FileUtils {
    @NonNull
    public static String readStringFromStream(@NonNull InputStream inputStream,  @NonNull String encoding) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}