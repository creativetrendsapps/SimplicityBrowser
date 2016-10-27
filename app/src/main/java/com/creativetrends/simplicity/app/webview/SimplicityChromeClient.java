package com.creativetrends.simplicity.app.webview;// Created by Creative Trends Apps on 8/23/2016.

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.creativetrends.simplicity.app.R;

public class SimplicityChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private Activity activity;

    private boolean isVideoFullscreen;
    private FrameLayout videoViewContainer;
    private CustomViewCallback videoViewCallback;

    private AlertDialog customViewDialog;
    private BottomSheetDialog alertDialog;

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
                    if (Build.VERSION.SDK_INT >= 14)
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }).create();
            customViewDialog.show();

            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            activity.getWindow().setAttributes(attrs);
            if (Build.VERSION.SDK_INT >= 14)
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

    @Override
    public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        alertDialog = new BottomSheetDialog(activity);
        @SuppressLint("InflateParams") View v = activity.getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);
        ((TextView) v.findViewById(R.id.title)).setText(R.string.app_name);
        ((TextView) v.findViewById(R.id.content)).setText(message);

        v.findViewById(R.id.cancel).setVisibility(View.GONE);
        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.confirm();
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                result.cancel();
                dialog.dismiss();
            }
        });

        alertDialog.setContentView(v);
        alertDialog.show();
        return true;

    }




    @Override
    public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        alertDialog = new BottomSheetDialog(activity);
        @SuppressLint("InflateParams") View v = activity.getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);

        ((TextView) v.findViewById(R.id.title)).setText(R.string.app_name);
        ((TextView) v.findViewById(R.id.content)).setText(message);

        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.cancel();
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.confirm();
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                result.cancel();
                dialog.dismiss();
            }
        });

        alertDialog.setContentView(v);
        alertDialog.show();
        return true;

    }

    @Override
    public boolean onJsPrompt(WebView view, String url, final String message, final String defaultValue, final JsPromptResult result) {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        alertDialog = new BottomSheetDialog(activity);
        @SuppressLint("InflateParams") View v = activity.getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);

        ((TextView) v.findViewById(R.id.title)).setText(message);
        v.findViewById(R.id.content).setVisibility(View.GONE);
        v.findViewById(R.id.inputLayout).setVisibility(View.VISIBLE);

        EditText input = (EditText) v.findViewById(R.id.input);
        input.setHint(defaultValue);

        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.cancel();
                alertDialog.dismiss();
            }
        });

        v.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.confirm();
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                result.cancel();
                dialog.dismiss();
            }
        });

        alertDialog.setContentView(v);
        alertDialog.show();
        return true;

    }
}