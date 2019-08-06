package com.creativetrends.app.simplicity.adapters;

import android.net.Uri;

/**
 * Created by Tushar on 9/10/2017.
 */

public class Files {
    private String name;
    private Uri uri;
    private String path;
    private String filename;

    public Files() {
    }

    public Files(String name, Uri uri, String path, String filename) {
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
