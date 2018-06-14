package com.creativetrends.app.simplicity.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/23/2018.
 */
public class AssetProvider extends ContentProvider {

    public static String CONTENT_URI = "com.creativetrends.simplicity.app.shareactionprovider";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Do not support delete requests.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // Do not support returning the data type
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Do not support insert requests.
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Do not support query requests.
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Do not support update requests.
        return 0;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        // The asset file name should be the last path segment
        final String assetName = uri.getLastPathSegment();

        // If the given asset name is empty, throw an exception
        if (TextUtils.isEmpty(assetName)) {
            throw new FileNotFoundException();
        }

        try {
            // Try and return a file descriptor for the given asset name
            AssetManager am = getContext().getAssets();
            return am.openFd(assetName);
        } catch (IOException e) {
            e.printStackTrace();
            return super.openAssetFile(uri, mode);
        }
    }
}
