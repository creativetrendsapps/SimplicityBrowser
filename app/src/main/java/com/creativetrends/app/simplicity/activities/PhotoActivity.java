package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.creativetrends.app.simplicity.helpers.Helpers;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.SimplicityDownloader;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;


/**
 * Created by Creative Trends Apps (Jorell Rutledge) 4/8/2018.
 */
public class PhotoActivity extends BaseActivity {
    private static String appDirectoryName;
    @SuppressLint("StaticFieldLeak")
    public Toolbar toolbar;
    SharedPreferences preferences;
    EditText pt;
    RelativeLayout pic_back;
    View saveButton;
    View shareButton;
    View viewButtons;
    ProgressBar mProgressbar;
    WebView mWebView;
    static long MAX_TOUCH_DURATION = 200;
    long m_DownTime;
    boolean isLoaded = false;
    String genericImage;
    String path;
    String shareRandom;
    CoordinatorLayout coordinatorLayout;

    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoviewer);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTranslucentStatus();
        saveButton = findViewById(R.id.save_image);
        shareButton = findViewById(R.id.share_image);
        viewButtons = findViewById(R.id.len);
        toolbar = findViewById(R.id.toolbar);
        coordinatorLayout = findViewById(R.id.root);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(null);
            Drawable drawable = toolbar.getNavigationIcon();
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        pt = new EditText(this);

        appDirectoryName = getString(R.string.app_name);

        mProgressbar = findViewById(R.id.progress_photo);
        genericImage = getIntent().getStringExtra("url");
        pic_back = findViewById(R.id.rel_pic);
        shareRandom = StaticUtils.getRandomString(10) + ".png";
        mWebView = findViewById(R.id.web_photo);
        mWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.loadUrl(genericImage);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(mWebView, true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.setVisibility(View.VISIBLE);
                mProgressbar.setVisibility(View.GONE);
                viewButtons.setVisibility(View.VISIBLE);
                isLoaded = true;
                super.onPageFinished(view, url);

            }
        });


        mWebView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    m_DownTime = event.getEventTime(); //init time
                    break;

                case MotionEvent.ACTION_UP:
                    if (event.getEventTime() - m_DownTime <= MAX_TOUCH_DURATION)
                        if (toolbar.getVisibility() == View.VISIBLE || saveButton.getVisibility() == View.VISIBLE || shareButton.getVisibility() == View.VISIBLE) {
                            toolbar.setVisibility(View.INVISIBLE);
                            saveButton.setVisibility(View.INVISIBLE);
                            shareButton.setVisibility(View.INVISIBLE);
                            viewButtons.setVisibility(View.INVISIBLE);
                            hideSystemUI();
                        } else {
                            toolbar.setVisibility(View.VISIBLE);
                            saveButton.setVisibility(View.VISIBLE);
                            shareButton.setVisibility(View.VISIBLE);
                            viewButtons.setVisibility(View.VISIBLE);
                            showSystemUI();
                        }
                    break;

                default:
                    break;

            }
            return false;
        });
        saveButton.setOnClickListener(v -> {
            try {
                if (!Helpers.hasStoragePermission(PhotoActivity.this)) {
                    Helpers.requestStoragePermission(PhotoActivity.this);
                } else {
                    new SimplicityDownloader(this).execute(genericImage);
                }
            } catch (Exception i) {
                i.printStackTrace();
            }

        });
        shareButton.setOnClickListener(v -> {
            shareImage();
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;


            case R.id.photo_copy:
                try {
                    if (genericImage != null) {
                        StaticUtils.copyTextToClipboard(PhotoActivity.this, mWebView, "Copied to clipboard", genericImage);
                    } else {
                        Cardbar.snackBar(getApplicationContext(), getString(R.string.error), true).show();

                    }
                } catch (NullPointerException ignored) {
                } catch (Exception i) {
                    i.printStackTrace();
                    Cardbar.snackBar(getApplicationContext(), i.toString(), true).show();


                }
                return true;


            case R.id.photo_browser:
                try {
                    if (genericImage != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(genericImage));
                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Cardbar.snackBar(getApplicationContext(), e.toString(), true).show();

                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }





    @TargetApi(19)
    private void setTranslucentStatus() {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        winParams.flags |= bits;
        win.setAttributes(winParams);
    }




    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }



    private void shareImage() {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, genericImage);
            startActivity(Intent.createChooser(share, getResources().getString(R.string.share)));
        } catch (ActivityNotFoundException ignored) {
        } catch (Exception p) {
            p.printStackTrace();
            Cardbar.snackBar(getApplicationContext(), p.toString(), true).show();
        }
    }


}
