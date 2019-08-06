/*
 * *
 *  * Created by Jorell Rutledge on 2/25/19 9:52 AM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/1/17 9:57 AM
 *
 */

package com.creativetrends.app.simplicity.webview;// Created by Creative Trends Apps on 8/23/2016.

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;


public class SimplicityChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private Activity activity;

    private boolean isVideoFullscreen;
    private FrameLayout videoViewContainer;
    private CustomViewCallback videoViewCallback;

    private AlertDialog customViewDialog;

    protected SimplicityChromeClient(Activity activity) {
        this.activity = activity;

        isVideoFullscreen = false;
    }

    public boolean isVideoFullscreen() {
        return isVideoFullscreen;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (view instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) view;
            View focusedChild = frameLayout.getFocusedChild();

            isVideoFullscreen = true;
            videoViewContainer = frameLayout;
            videoViewCallback = callback;

            if (customViewDialog != null && customViewDialog.isShowing())
                customViewDialog.dismiss();

            customViewDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).setView(videoViewContainer).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }).create();
            customViewDialog.show();

            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

            if (focusedChild instanceof VideoView) {
                VideoView videoView = (VideoView) focusedChild;

                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if (isVideoFullscreen) {
            if (customViewDialog != null && customViewDialog.isShowing())
                customViewDialog.dismiss();

            if (videoViewCallback != null && !videoViewCallback.getClass().getName().contains(".chromium.")) {
                videoViewCallback.onCustomViewHidden();
            }

            isVideoFullscreen = false;
            videoViewContainer = null;
            videoViewCallback = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onHideCustomView();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean onBackPressed() {
        if (isVideoFullscreen) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }


}