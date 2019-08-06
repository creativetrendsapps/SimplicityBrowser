/*
 * *
 *  * Created by Jorell Rutledge on 1/11/19 1:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 1/8/19 12:09 PM
 *
 */

package com.creativetrends.app.simplicity.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.creativetrends.app.simplicity.activities.PhotoActivity;


/**
 * Created by Creative Trends Apps.
 */
public class ImageInterface {
    private Activity mActivity;

    public ImageInterface(Activity activity) {
        mActivity = activity;
    }

    private void loadPhoto(String image) {
        Intent intent = new Intent(mActivity, PhotoActivity.class);
        intent.putExtra("url", image);
        intent.putExtra("page", "simple");
        mActivity.startActivity(intent);
    }



    @JavascriptInterface
    public void getImage(String image) {
        loadPhoto(image);
    }


}
