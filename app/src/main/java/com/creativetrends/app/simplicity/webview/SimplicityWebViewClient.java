package com.creativetrends.app.simplicity.webview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.utils.Cleaner;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

import java.io.ByteArrayInputStream;

import static com.creativetrends.app.simplicity.activities.MainActivity.adBlockerEnabled;
import static com.creativetrends.app.simplicity.activities.MainActivity.isLoading;
import static com.creativetrends.app.simplicity.activities.MainActivity.mForward;
import static com.creativetrends.app.simplicity.activities.MainActivity.mJump;
import static com.creativetrends.app.simplicity.activities.MainActivity.mRefresh;
import static com.creativetrends.app.simplicity.activities.MainActivity.mRefresh_rel;
import static com.creativetrends.app.simplicity.activities.MainActivity.mSecure;
import static com.creativetrends.app.simplicity.activities.MainActivity.mStop;
import static com.creativetrends.app.simplicity.activities.MainActivity.mStop_rel;
import static com.creativetrends.app.simplicity.activities.MainActivity.scrollPosition;

/**
 * Created by Creative Trends Apps.
 */
public class SimplicityWebViewClient extends WebViewClient {
    private NestedWebview mainView;

    SimplicityWebViewClient(SimpleAutoComplete textView, AppCompatActivity ac, NestedWebview view) {
        this.mainView = view;
        view.setOnScrollChangedCallback((l, t) -> {
            scrollPosition = t;
            if(scrollPosition >= 10){
                mJump.setClickable(true);
            }else{
                mJump.setClickable(false);

            }
        });
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        url = Cleaner.cleanAndDecodeUrl(url);
        if (url.contains("market://")
                || url.contains("mailto:")
                || url.contains("play.google")
                || url.contains("tel:")
                || url.contains("intent:")
                || url.contains("geo:")
                || url.contains("google.com/maps")
                || url.contains("streetview:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            try {
                view.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {

                e.printStackTrace();
            }
            return true;
        } else if (url.startsWith("http://") || url.startsWith("https://")){
            return false;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (Exception e) {
                Log.i("", "shouldOverrideUrlLoading Exception:" + e);
                return true;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url){
        if (adBlockerEnabled) {
            Uri requestUri = Uri.parse(url);
            String requestHost = requestUri.getHost();
            boolean requestHostIsAdServer = false;
            if (requestHost != null) {
                while (requestHost.contains(".") && !requestHostIsAdServer) {
                    if (MainActivity.adServersSet.contains(requestHost)) {
                        requestHostIsAdServer = true;
                    }
                    requestHost = requestHost.substring(requestHost.indexOf(".") + 1);
                }
            }

            if (requestHostIsAdServer) {
                return new WebResourceResponse("text/plain", "utf8", new ByteArrayInputStream("".getBytes()));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isLoading = true;
        try {
            if(UserPreferences.getBoolean("dark_mode", false)) {
                ((MainActivity) MainActivity.getMainActivity()).setColor(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.darcula));
            }else{
                ((MainActivity) MainActivity.getMainActivity()).setColor(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.no_fav));

            }
            StaticUtils.fixURL(url);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        super.onFormResubmission(view, dontResend, resend);
        try {
            Activity mActivity = mainView.getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Form resubmission?");
            builder.setMessage("Do you want to resubmit?")
                    .setCancelable(true)
                    .setPositiveButton(mActivity.getString(android.R.string.ok),
                            (dialog, id) -> resend.sendToTarget())
                    .setNegativeButton(mActivity.getString(android.R.string.cancel),
                            (dialog, id) -> dontResend.sendToTarget());
            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception i){
            i.printStackTrace();
        }

    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        if(!mainView.getActivity().isDestroyed()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mainView.getActivity());
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }
            message += " Do you want to continue anyway?";

            builder.setTitle("SSL Certificate Error");
            builder.setMessage(message);
            builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
            builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public void onLoadResource(WebView view, String url){
        super.onLoadResource(view, url);
        if (view != null && view.getUrl().contains("https://")) {
            mSecure.setImageDrawable(ContextCompat.getDrawable(mainView.getContext(), R.drawable.ic_secure_white));
            mSecure.setVisibility(View.VISIBLE);
        }else{
            mSecure.setImageDrawable(ContextCompat.getDrawable(mainView.getContext(), R.drawable.ic_unsecure_white));
            mSecure.setVisibility(View.VISIBLE);
        }
        if (mRefresh!= null || mStop !=null) {
            if (UserPreferences.getBoolean("dark_mode", false)) {
                mRefresh.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
                mStop.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                mRefresh.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.dark), PorterDuff.Mode.SRC_IN);
                mStop.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.dark), PorterDuff.Mode.SRC_IN);
            }
            if (isLoading) {
                mStop_rel.setVisibility(View.VISIBLE);
                mRefresh_rel.setVisibility(View.GONE);
            } else {
                mStop_rel.setVisibility(View.GONE);
                mRefresh_rel.setVisibility(View.VISIBLE);
            }
        }
        if (view != null && view.canGoForward()) {
            mForward.setAlpha(0.9f);
        }else{
            mForward.setAlpha(0.2f);
        }

        if (view != null && view.getUrl() != null) {
            (((MainActivity) MainActivity.getMainActivity())).onAmpPage(view.getUrl().contains("/amp/"));
        }

    }



    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mainView.isCurrentTab()) {
            isLoading = false;
            if (view != null && view.getTitle().equals("No Connection")) {
                ((MainActivity) MainActivity.getMainActivity()).mSearchView.setText(view.getTitle());
            } else if (view != null){
                ((MainActivity) MainActivity.getMainActivity()).mSearchView.setText(view.getUrl());
            }
            if (view != null)
                MainActivity.sslCertificate = view.getCertificate();
        }
        try {
            if (UserPreferences.getBoolean("instagram_downloads", false) && url != null && url.contains("instagram.com")) {
                CSSInjection.injectInstaTheme(SimplicityApplication.getContextOfApplication(), view);
            }
            if (UserPreferences.getBoolean("facebook_photos", false) && view != null &&  url != null && url.contains("facebook.com")) {
                CSSInjection.injectPhotos(view);
            }
            if (UserPreferences.getBoolean("dark_mode_web", false)) {
                CSSInjection.injectDarkMode(SimplicityApplication.getContextOfApplication(), view);
            }
            //MainActivity.colorLightWhite();
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

