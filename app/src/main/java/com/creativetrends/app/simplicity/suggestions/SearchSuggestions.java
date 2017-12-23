package com.creativetrends.app.simplicity.suggestions;

import android.support.annotation.NonNull;

/**
 * Created by Creative Trends Apps.
 */

public class SearchSuggestions extends SuggestionProvider {
    SearchSuggestions() {
        super("UTF-8");
    }

    @NonNull
    protected String createQueryUrl(@NonNull String query,  @NonNull String language) {
        return "https://www.google.com/complete/search?client=android&oe=utf8&ie=utf8"
                + "&hl=" + language + "&q=" + query;
    }
}