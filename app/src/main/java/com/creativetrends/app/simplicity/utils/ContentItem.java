package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/23/2018.
 */
public class ContentItem {
    // Used to signify an image content type
    public static final int CONTENT_TYPE_IMAGE = 0;
    // Used to signify a text/string content type
    public static final int CONTENT_TYPE_TEXT = 1;

    public final int contentType;
    public final int contentResourceId;
    public final String contentAssetFilePath;

    /**
     * Creates a ContentItem with the specified type, referencing a resource id.
     *
     * @param type - One of {@link #CONTENT_TYPE_IMAGE} or {@link #CONTENT_TYPE_TEXT}
     * @param resourceId - Resource ID to use for this item's content
     */
    public ContentItem(int type, int resourceId) {
        contentType = type;
        contentResourceId = resourceId;
        contentAssetFilePath = null;
    }

    /**
     * Creates a ContentItem with the specified type, referencing an asset file path.
     *
     * @param type - One of {@link #CONTENT_TYPE_IMAGE} or {@link #CONTENT_TYPE_TEXT}
     * @param assetFilePath - File path from the application's asset for this item's content
     */
    public ContentItem(int type, String assetFilePath) {
        contentType = type;
        contentAssetFilePath = assetFilePath;
        contentResourceId = 0;
    }

    /**
     * @return Uri to the content
     */
    public Uri getContentUri() {
        if (!TextUtils.isEmpty(contentAssetFilePath)) {
            // If this content has an asset, then return a AssetProvider Uri
            return Uri.parse("content://" + AssetProvider.CONTENT_URI + "/" + contentAssetFilePath);
        } else {
            return null;
        }
    }

    /**
     * Returns an {@link android.content.Intent} which can be used to share this item's content with other
     * applications.
     *
     * @param context - Context to be used for fetching resources if needed
     * @return Intent to be given to a ShareActionProvider.
     */
    public Intent getShareIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        switch (contentType) {
            case CONTENT_TYPE_IMAGE:
                intent.setType("image/jpg");
                // Bundle the asset content uri as the EXTRA_STREAM uri
                intent.putExtra(Intent.EXTRA_STREAM, getContentUri());
                break;

            case CONTENT_TYPE_TEXT:
                intent.setType("text/plain");
                // Get the string resource and bundle it as an intent extra
                intent.putExtra(Intent.EXTRA_TEXT, context.getString(contentResourceId));
                break;
        }

        return intent;
    }

}
