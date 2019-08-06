package com.creativetrends.app.simplicity.suggestions;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.creativetrends.app.simplicity.utils.FileUtils;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Creative Trends Apps.
 */

abstract class SuggestionProvider {
    private static final String TAG = "SuggestionProvider";
    private static final long INTERVAL_DAY = TimeUnit.DAYS.toSeconds(1);
    private static final String DEFAULT_LANGUAGE = "en";
    @NonNull
    private final String mEncoding;
    @NonNull
    private final String mLanguage;

    SuggestionProvider(@NonNull String encoding) {
        mEncoding = encoding;
        mLanguage = getLanguage();
    }

    @NonNull
    private static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (TextUtils.isEmpty(language)) {
            language = DEFAULT_LANGUAGE;
        }
        return language;
    }


    @NonNull
    protected abstract String createQueryUrl(@NonNull String query, @NonNull String language);


    private void parseResults(@NonNull String content,
                              @NonNull ResultCallback callback) throws Exception {
        JSONArray respArray = new JSONArray(content);
        JSONArray jsonArray = respArray.getJSONArray(1);
        for (int n = 0, size = jsonArray.length(); n < size; n++) {
            String suggestion = jsonArray.getString(n);
            if (!callback.addResult(suggestion)) {
                break;
            }
        }
    }


    @NonNull
    final List<String> fetchResults(@NonNull final String rawQuery) {
        List<String> filter = new ArrayList<>(10);

        String query;
        try {
            query = URLEncoder.encode(rawQuery, mEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to encode the URL", e);

            return filter;
        }

        String content = downloadSuggestionsForQuery(query, mLanguage);
        if (content == null) {
            // There are no suggestions for this query, return an empty list.
            return filter;
        }
        try {
            parseResults(content, suggestion -> {
                filter.add(suggestion);
                return filter.size() < 10;
            });
        } catch (Exception e) {
            Log.e(TAG, "Unable to parse results", e);
        }

        return filter;
    }


    @Nullable
    private String downloadSuggestionsForQuery(@NonNull String query,
                                               @NonNull String language) {
        try {
            URL url = new URL(createQueryUrl(query, language));

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Cache-Control",
                    "max-age=" + INTERVAL_DAY + ", max-stale=" + INTERVAL_DAY);
            urlConnection.addRequestProperty("Accept-Charset", mEncoding);
            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                return FileUtils.readStringFromStream(in, getEncoding(urlConnection));
            } finally {
                urlConnection.disconnect();
                // ignored
            }
        } catch (IOException ignored) {

        }

        return null;
    }

    private String getEncoding(HttpURLConnection connection) {
        String contentEncoding = connection.getContentEncoding();
        if (contentEncoding != null) {
            return contentEncoding;
        }

        String contentType = connection.getContentType();
        for (String value : contentType.split(";")) {
            value = value.trim();
            if (value.toLowerCase(Locale.US).startsWith("charset=")) {
                return value.substring(8);
            }
        }

        return mEncoding;
    }

    interface ResultCallback {
        boolean addResult(String suggestion);
    }
}