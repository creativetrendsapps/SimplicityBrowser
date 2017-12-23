package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.suggestions.SuggestionsAdapter;
import com.creativetrends.app.simplicity.utils.Bookmark;
import com.creativetrends.app.simplicity.utils.CreateShortcut;
import com.creativetrends.app.simplicity.utils.History;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.app.simplicity.webview.NestedWebView;
import com.creativetrends.simplicity.app.R;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity implements CreateShortcut.CreateHomeScreenSchortcutListener {
    SharedPreferences mPreferences;
    public NestedWebView mWebView;
    WebSettings mWebSettings;
    boolean isDesktop;
    boolean isIncognito;
    AutoCompleteTextView mSearchView;
    Toolbar mToolbar;
    String UrlCleaner;
    String defaultSearch, defaultProvider;

    ImageView mHomebutton, mSecure, mOverflow, vSearch;
    public static Bitmap favoriteIcon;
    AppBarLayout mAppbar;
    BottomNavigationViewEx mTabs;
    HashMap<String, String> extraHeaders = new HashMap<>();
    CardView mCardView;
    ScrollView mScroll;
    FrameLayout mHolder;
    AppCompatCheckBox pri, desk;
    public static String homepage;
    public static CookieManager cookieManager;
    private boolean adBlockerEnabled;
    CoordinatorLayout background_color;
    private static final int STORAGE_PERMISSION_CODE = 2284, REQUEST_STORAGE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String urlToGrab;
    AnimatedProgressBar mProgress;
    private long back_pressed;
    public static String webViewTitle;

    // fullscreen videos
    private MyWebChromeClient mWebChromeClient;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout customViewContainer;
    private View mCustomView;
    private int previousUiVisibility;

    // variables for camera and choosing files methods
    private static final int FILECHOOSER_RESULTCODE = 1;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_menu_holder:
                    hideMenu();
                    return;

                case R.id.overflow_button:
                    showMenu();
                    return;

                case R.id.sim_go_forward:
                    hideMenu();
                    AlertDialog.Builder info = new AlertDialog.Builder(MainActivity.this);
                    info.setTitle(mWebView.getUrl());
                    if (mWebView.getUrl().contains("https://")) {
                        info.setMessage(getResources().getString(R.string.private_info));
                    } else {
                        info.setMessage(getResources().getString(R.string.none_private_info));
                    }
                    info.setPositiveButton("OKAY", (arg0, arg1) -> {

                    });
                    info.setNeutralButton(null, null);
                    info.show();
                    return;

                case R.id.sim_bookmark:
                    hideMenu();
                    ArrayList<Bookmark> listBookmarks = UserPreferences.getBookmarks();
                    Bookmark bookmark = new Bookmark();
                    bookmark.setTitle(mWebView.getTitle());
                    bookmark.setUrl(mWebView.getUrl());
                    listBookmarks.add(bookmark);
                    UserPreferences.saveBookmarks(listBookmarks);
                    Toast.makeText(MainActivity.this, mWebView.getTitle().replace("", "") +" " +getResources().getString(R.string.added_to_bookmarks), Toast.LENGTH_SHORT).show();
                    return;

                case R.id.sim_refresh:
                    hideMenu();
                    mWebView.reload();
                    return;

                case R.id.sim_stop:
                    hideMenu();
                    mWebView.stopLoading();
                    return;

                case R.id.sim_new_window:
                    hideMenu();
                    if (mPreferences.getBoolean("merge_windows", false)) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.setData(Uri.parse(homepage));
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }
                    return;

                case R.id.sim_private_mode:
                    hideMenu();
                    isIncognito = !isIncognito;
                    pri.setChecked(isIncognito);
                    if (!isIncognito) {
                        //clearPrivate();
                        CookieManager.getInstance().setAcceptCookie(true);
                        mWebView.getSettings().setAppCacheEnabled(true);
                        mWebView.getSettings().setSavePassword(true);
                        mWebView.getSettings().setSaveFormData(true);
                        mWebView.getSettings().setDatabaseEnabled(true);
                        mWebView.getSettings().setDomStorageEnabled(true);
                        mWebView.reload();
                        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
                        CookieSyncManager.createInstance(MainActivity.this);
                        CookieSyncManager.getInstance().startSync();
                    } else {
                        //showPrivateNotification();
                        mWebView.isPrivateBrowsingEnabled();
                        mWebView.getSettings().setSavePassword(false);
                        mWebView.getSettings().setSaveFormData(false);
                        mWebView.getSettings().setDatabaseEnabled(false);
                        mWebView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(MainActivity.this, R.color.md_grey_900));

                    }
                    hideMenu();
                    return;

                case R.id.sim_history:
                    hideMenu();
                    Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(history);
                    return;

                case R.id.sim_books:
                    hideMenu();
                    Intent settingsIntent = new Intent(MainActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                    return;

                case R.id.sim_find:
                    hideMenu();
                    mWebView.showFindDialog(null, true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    return;

                case R.id.sim_share:
                    hideMenu();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Share current page");
                    i.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                    startActivity(Intent.createChooser(i, "Share with"));
                    return;

                case R.id.sim_print:
                    hideMenu();
                    pagePrint(mWebView);
                    return;

                case R.id.sim_home_screen:
                    hideMenu();
                    try {
                        if (!StaticUtils.isOreo()) {
                            AppCompatDialogFragment createHomeScreenShortcutDialogFragment = new CreateShortcut();
                            createHomeScreenShortcutDialogFragment.show(getSupportFragmentManager(), getString(R.string.create_shortcut));
                        } else {
                            createShortcut();
                        }
                    }catch(NullPointerException ignored) {
                    }catch(Exception p){
                        p.printStackTrace();
                    }
                    return;

                case R.id.sim_desktop:
                    hideMenu();
                    if (desk.isChecked()) {
                        desk.setChecked(false);
                    } else {
                        desk.setChecked(true);
                    }
                    if (isDesktop) {
                        mWebSettings.setUserAgentString("");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebView.reload();
                        isDesktop = false;

                    } else {
                        mWebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.41 Safari/537.36");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebSettings.setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = true;

                    }
                    return;

                case R.id.sim_settings:
                    hideMenu();
                    Intent Intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(Intent);
                    return;

                case R.id.sim_reader:
                    hideMenu();
                    startReaderMode();
                    return;


                case R.id.sim_desktop_check:
                    hideMenu();
                    if (desk.isChecked()) {
                        desk.setChecked(false);
                    } else {
                        desk.setChecked(true);
                    }
                    if (isDesktop) {
                        mWebSettings.setUserAgentString("");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebView.reload();
                        isDesktop = false;

                    } else {
                        mWebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.41 Safari/537.36");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebSettings.setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = true;

                    }
                    return;

                case R.id.sim_private_check:
                    hideMenu();
                    isIncognito = !isIncognito;
                    pri.setChecked(isIncognito);
                    if (!isIncognito) {
                        //clearPrivate();
                        CookieManager.getInstance().setAcceptCookie(true);
                        mWebView.getSettings().setAppCacheEnabled(true);
                        mWebView.getSettings().setSavePassword(true);
                        mWebView.getSettings().setSaveFormData(true);
                        mWebView.getSettings().setDatabaseEnabled(true);
                        mWebView.getSettings().setDomStorageEnabled(true);
                        mWebView.reload();
                        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
                        CookieSyncManager.createInstance(MainActivity.this);
                        CookieSyncManager.getInstance().startSync();
                    } else {
                        //showPrivateNotification();
                        mWebView.isPrivateBrowsingEnabled();
                        mWebView.getSettings().setSavePassword(false);
                        mWebView.getSettings().setSaveFormData(false);
                        mWebView.getSettings().setDatabaseEnabled(false);
                        mWebView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(MainActivity.this, R.color.md_grey_900));

                    }
                    hideMenu();
                    return;

                case R.id.voice_button:
                    promptSpeechInput();
                    return;

                default:
                    hideMenu();

            }

        }

    };


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mHomebutton = findViewById(R.id.toolbar_home);
        customViewContainer = findViewById(R.id.customViewContainer);
        background_color = findViewById(R.id.background_color);
        mSecure = findViewById(R.id.secure_site);
        mOverflow = findViewById(R.id.overflow_button);
        mTabs = findViewById(R.id.bottom_navigation);
        mProgress = findViewById(R.id.progressBar);
        vSearch = findViewById(R.id.voice_button);
        if(mPreferences.getBoolean("show_bottom", false)){
            setBottomTabs();
        }else{
            mTabs.setVisibility(View.GONE);
        }
        mAppbar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            setSupportActionBar(mToolbar);

        }
        mSearchView = findViewById(R.id.search_box);
        mWebView = findViewById(R.id.webView);
        // set webview clients

        forClicks();
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            //noinspection deprecation
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }
        mWebView.addJavascriptInterface(new ReaderHandler(MainActivity.this), "simplicity_reader");
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        extraHeaders.put("DNT", "1");
        homepage = mPreferences.getString("homepage", "");
        defaultSearch = mPreferences.getString("search_engine", "");
        // Initialize `webViewTitle`.
        webViewTitle = getString(R.string.app_name);
        // Initialize `favoriteIconBitmap`.  We have to use `ContextCompat` until API >= 21.

        final Intent intent = getIntent();

        if (intent.getData() != null) {
            final Uri intentUriData = intent.getData();
            UrlCleaner = intentUriData.toString();
        }


        if (UrlCleaner == null && mPreferences.getBoolean("remember_page", false)) {
            UrlCleaner = mPreferences.getString("last_page_reminder", "");
        }else if(UrlCleaner == null && !mPreferences.getBoolean("remember_page", false)){
            UrlCleaner = homepage;
        }
        if(mPreferences.getBoolean("no_track", false)) {
            mWebView.loadUrl(UrlCleaner, extraHeaders);
        }else{
            mWebView.loadUrl(UrlCleaner);
        }

        final Set<String> adServersSet = new HashSet<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("ad_block.txt")));

            String adServer;

            while ((adServer = bufferedReader.readLine()) != null) {
                adServersSet.add(adServer);
            }

            bufferedReader.close();
        } catch (IOException ioException) {
            // `IOException.
        }



        mWebView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("market:")
                        || url.startsWith("https://play.google.com") || url.startsWith("magnet:")
                        || url.startsWith("mailto:") || url.startsWith("intent:")
                        || url.startsWith("geo:") || url.startsWith("google.streetview:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception exc) {
                        Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                }
                mWebView.setOnLongClickListener(view1 -> {

                    // Click on image
                    if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                            || mWebView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_ANCHOR_TYPE
                            || mWebView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_TYPE) {
                        BottomSheetMenuDialog dialog = new BottomSheetBuilder(MainActivity.this, null)
                                .setMode(BottomSheetBuilder.MODE_LIST)
                                .setMenu(R.menu.menu_image)
                                .delayDismissOnItemClick(false)
                                .setItemClickListener(item -> {
                                    switch (item.getItemId()) {
                                        case R.id.image_save:
                                            urlToGrab = mWebView.getHitTestResult().getExtra();
                                            requestStoragePermission();
                                            break;

                                        case R.id.image_open:
                                            if (mPreferences.getBoolean("merge_windows", false)) {
                                                Intent intent13 = new Intent(MainActivity.this, MainActivity.class);
                                                intent13.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                intent13.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                                intent13.putExtra("isNewTab", true);
                                                startActivity(intent13);
                                            } else {
                                                Intent intent13 = new Intent(MainActivity.this, MainActivity.class);
                                                intent13.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                                intent13.putExtra("isNewTab", true);
                                                startActivity(intent13);
                                            }
                                            break;

                                        case R.id.image_copy:
                                            ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newUri(getContentResolver(), "URI", Uri.parse(mWebView.getHitTestResult().getExtra()));
                                            if (clipboard != null) {
                                                clipboard.setPrimaryClip(clip);
                                            }
                                            break;


                                        case R.id.image_share:
                                            Intent share = new Intent(Intent.ACTION_SEND);
                                            share.setType("text/plain");
                                            share.putExtra(Intent.EXTRA_TEXT, mWebView.getHitTestResult().getExtra());
                                            startActivity(Intent.createChooser(share, "Share via"));
                                            break;
                                    }

                                })
                                .createDialog();

                        dialog.show();
                        return true;
                    } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.ANCHOR_TYPE // Link click
                            || mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                        BottomSheetMenuDialog dialog = new BottomSheetBuilder(MainActivity.this, null)
                                .setMode(BottomSheetBuilder.MODE_LIST)
                                .setMenu(R.menu.menu_link)
                                .delayDismissOnItemClick(false)
                                .setItemClickListener(item -> {

                                    switch (item.getItemId()) {

                                        case R.id.link_open:
                                            if (mPreferences.getBoolean("merge_windows", false)) {
                                                Intent intent12 = new Intent(MainActivity.this, MainActivity.class);
                                                intent12.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                intent12.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                                intent12.putExtra("isNewTab", true);
                                                startActivity(intent12);
                                            } else {
                                                Intent intent12 = new Intent(MainActivity.this, MainActivity.class);
                                                intent12.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                                intent12.putExtra("isNewTab", true);
                                                startActivity(intent12);
                                            }
                                            break;

                                        case R.id.link_copy:
                                            ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newUri(getContentResolver(), "URI", Uri.parse(mWebView.getHitTestResult().getExtra()));
                                            if (clipboard != null) {
                                                clipboard.setPrimaryClip(clip);
                                            }
                                            break;


                                        case R.id.link_share:
                                            Intent share = new Intent(Intent.ACTION_SEND);
                                            share.setType("text/plain");
                                            share.putExtra(Intent.EXTRA_TEXT, Uri.parse(mWebView.getHitTestResult().getExtra()));
                                            startActivity(Intent.createChooser(share, "Share via"));
                                            break;


                                    }

                                })
                                .createDialog();
                        dialog.show();
                        return  true;
                    } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.PHONE_TYPE) { // Phone number click
                        Intent intent14 = new Intent(Intent.ACTION_DIAL);
                        intent14.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                        startActivity(intent14);
                        return true;
                    } else {
                        return false;
                    }
                });
                return false;
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
                            if (adServersSet.contains(requestHost)) {
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
                mSearchView.setText(mWebView.getUrl());
                try {
                    if (view.getUrl().contains("https://")) {
                        mSecure.setVisibility(View.VISIBLE);
                    }else{
                        mSecure.setVisibility(View.GONE);
                    }
                    findViewById(R.id.sim_stop).setVisibility(View.VISIBLE);
                    findViewById(R.id.sim_refresh).setVisibility(View.GONE);
                }catch (NullPointerException ignored){
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mSearchView.setText(mWebView.getUrl());
                try {
                    if (view.getUrl().contains("https://")) {
                        mSecure.setVisibility(View.VISIBLE);
                    }else{
                        mSecure.setVisibility(View.GONE);
                    }
                    findViewById(R.id.sim_stop).setVisibility(View.GONE);
                    findViewById(R.id.sim_refresh).setVisibility(View.VISIBLE);
                    //mWebView.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText,document.title);");
                }catch (NullPointerException ignored){
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });



        mWebChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            final String filename1 = URLUtil.guessFileName(url, contentDisposition, mimeType);
            Snackbar snackbar = Snackbar.make(mWebView, "Download " + filename1 + "?", Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.parseColor("#1e88e5"));
            snackbar.setAction("DOWNLOAD", view -> {
                if (Build.VERSION.SDK_INT >= M) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    } else {
                        try {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                            String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            if (dm != null) {
                                dm.enqueue(request);
                            }
                            Intent intent1;
                            intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent1.addCategory(Intent.CATEGORY_OPENABLE);
                            intent1.setType("*/*");


                        } catch (Exception exc) {
                            Toast.makeText(MainActivity.this, exc.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                        String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        if (dm != null) {
                            dm.enqueue(request);
                        }
                        Intent intent1;
                        intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent1.addCategory(Intent.CATEGORY_OPENABLE);
                        intent1.setType("*/*");


                    } catch (Exception exc) {
                        Toast.makeText(MainActivity.this, exc.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            snackbar.show();
        });

        mSearchView.setAdapter(new SuggestionsAdapter(this));

        mSearchView.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                try {
                    loadUrlFromTextBox();
                    mSearchView.setCursorVisible(false);
                    mSearchView.clearFocus();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        });

        mSearchView.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(mWebView, 0);
            }
            mSearchView.setCursorVisible(true);
            mSearchView.selectAll();
            mSearchView.hasFocus();

        });

        mSearchView.setOnItemClickListener((parent, view, position, rowId) -> {
            try {
                loadUrlFromTextBox();
                mSearchView.setCursorVisible(false);
                mSearchView.clearFocus();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });


        mHomebutton.setOnClickListener(v -> {
            if(mPreferences.getBoolean("no_track", false)) {
                mWebView.loadUrl(homepage, extraHeaders);
            }else{
                mWebView.loadUrl(homepage);
            }

        });



        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(mPreferences.getBoolean("show_bottom", false)) {
                mTabs.setTranslationY((float) (verticalOffset * -1.1));
            }
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (intent.getData() != null) {
            final Uri intentUriData = intent.getData();
            UrlCleaner = intentUriData.toString();
        }
        if(mPreferences.getBoolean("no_track", false)) {
            mWebView.loadUrl(UrlCleaner, extraHeaders);
        }else{
            mWebView.loadUrl(UrlCleaner);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        applyAppSettings();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getDataString() != null) {
            shortcutSwitch(getIntent().getDataString());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        applyHomeButton();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        mWebView.resumeTimers();
        mWebView.onResume();
        registerForContextMenu(mWebView);
    }


    @Override
    public void onPause() {
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        unregisterForContextMenu(mWebView);
        mWebView.pauseTimers();
        mWebView.onPause();
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (inCustomView())
            hideCustomView();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }else if (mCustomView == null && mWebView.canGoBack()) {
            mWebView.stopLoading();
            goBack();
        } else {
           finishActivity();
        }
    }

    private void goBack() {
        mWebView.goBack();
    }

    private void finishActivity() {
        if (getIntent().getBooleanExtra("isNewTab", false)) {
            finish();
        }else if (mPreferences.getBoolean("confirm_close", false)) {
            if (back_pressed + 2000 > System.currentTimeMillis())
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            else
                Toast.makeText(getBaseContext(), R.string.simplicity_close, Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        } else {
            finish();
            if(mPreferences.getBoolean("clear_data", false)){
                StaticUtils.deleteCache(SimplicityApplication.getContextOfApplication());
                ArrayList<History> listBookmarks = UserPreferences.getHistory();
                listBookmarks.clear();
                UserPreferences.saveHistory(listBookmarks);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inCustomView()) {
            hideCustomView();
        }
    }

    @Override
    protected void onDestroy() {
        if(mPreferences.getBoolean("clear_data", false)){
            try {
                StaticUtils.deleteCache(SimplicityApplication.getContextOfApplication());
                ArrayList<History> listBookmarks = UserPreferences.getHistory();
                listBookmarks.clear();
                UserPreferences.saveHistory(listBookmarks);
            }catch(NullPointerException ignored){
            }catch(Exception i){
             i.printStackTrace();
            }
        }
        // just in case, it should be GCed anyway
        if (mWebChromeClient != null)
            mWebChromeClient = null;
        //clearPrivate();
        super.onDestroy();

    }

    private void loadUrlFromTextBox() throws UnsupportedEncodingException {
        String unUrlCleaner = mSearchView.getText().toString();
        URL unformattedUrl = null;
        Uri.Builder formattedUri = new Uri.Builder();
        if (Patterns.WEB_URL.matcher(unUrlCleaner).matches()) {
            if (!unUrlCleaner.startsWith("http")) {
                unUrlCleaner = "http://" + unUrlCleaner;
            }
            try {
                unformattedUrl = new URL(unUrlCleaner);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            final String scheme = unformattedUrl != null ? unformattedUrl.getProtocol() : null;
            final String authority = unformattedUrl != null ? unformattedUrl.getAuthority() : null;
            final String path = unformattedUrl != null ? unformattedUrl.getPath() : null;
            final String query = unformattedUrl != null ? unformattedUrl.getQuery() : null;
            final String fragment = unformattedUrl != null ? unformattedUrl.getRef() : null;

            formattedUri.scheme(scheme).authority(authority).path(path).query(query).fragment(fragment);
            UrlCleaner = formattedUri.build().toString();
        } else {

            final String encodedUrlString = URLEncoder.encode(unUrlCleaner, "UTF-8");


            UrlCleaner = defaultSearch + encodedUrlString;
        }
        if(mPreferences.getBoolean("no_track", false)) {
            mWebView.loadUrl(UrlCleaner, extraHeaders);
        }else{
            mWebView.loadUrl(UrlCleaner);
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
        }
    }



    private void setColor(int color) {
        color = isIncognito ? ContextCompat.getColor(this, R.color.md_grey_900) : color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.darkColor(color));
            colorAnimation.setDuration(90);
            colorAnimation.addUpdateListener(animator -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor((int) animator.getAnimatedValue());
                    if (mPreferences.getBoolean("nav_color", false)) {
                        getWindow().setNavigationBarColor((int) animator.getAnimatedValue());
                    }else{
                        getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.black));

                    }
                }

            });
            colorAnimation.start();
        }
        int colorFrom = ContextCompat.getColor(this, !isIncognito ? R.color.md_grey_900 : R.color.colorPrimary);
        Drawable backgroundFrom = mToolbar.getBackground();
        if (backgroundFrom instanceof ColorDrawable)
            colorFrom = ((ColorDrawable) backgroundFrom).getColor();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(100);
        colorAnimation.addUpdateListener(animator -> {
            mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
            mProgress.setBackgroundColor((int) animator.getAnimatedValue());
            if(mPreferences.getBoolean("show_bottom", false)) {
                mTabs.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

    }


    public void setBottomTabs(){
        mTabs.enableAnimation(false);
        mTabs.enableShiftingMode(false);
        mTabs.enableItemShiftingMode(false);
        mTabs.setTextSize(11);
        mTabs.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.goBack:
                    mAppbar.setExpanded(true, true);
                    if(mWebView.canGoBack()){
                        goBack();
                    }
                    break;

                case R.id.goForward:
                    mAppbar.setExpanded(true, true);
                    if(mWebView.canGoForward()){
                        mWebView.goForward();
                    }
                    break;

                case R.id.goWindow:
                    mAppbar.setExpanded(true, true);
                    if (mPreferences.getBoolean("merge_windows", false)) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.setData(Uri.parse(homepage));
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }

                    break;

                case R.id.goJump:
                    mAppbar.setExpanded(true, true);
                    try {
                        if (mWebView != null && mWebView.getScrollY() > 10) {
                            scrollToTop(mWebView);
                        }
                    }catch(Exception ignored) {

                    }
                    break;
            }
            // do other
            return true;
        });

    }



    private void forClicks() {
        mCardView = findViewById(R.id.sim_menu);
        mScroll = findViewById(R.id.scroller);
        mHolder = findViewById(R.id.main_menu_holder);
        pri = findViewById(R.id.sim_private_check);
        desk = findViewById(R.id.sim_desktop_check);
        mHolder.setOnClickListener(onClickListener);
        mHolder.setClickable(false);
        mHolder.setFocusable(false);
        findViewById(R.id.sim_go_forward).setOnClickListener(onClickListener);
        findViewById(R.id.sim_bookmark).setOnClickListener(onClickListener);
        findViewById(R.id.sim_history).setOnClickListener(onClickListener);
        findViewById(R.id.sim_refresh).setOnClickListener(onClickListener);
        findViewById(R.id.sim_stop).setOnClickListener(onClickListener);
        findViewById(R.id.sim_new_window).setOnClickListener(onClickListener);
        findViewById(R.id.sim_private_mode).setOnClickListener(onClickListener);
        findViewById(R.id.sim_books).setOnClickListener(onClickListener);
        findViewById(R.id.sim_find).setOnClickListener(onClickListener);
        findViewById(R.id.sim_share).setOnClickListener(onClickListener);
        findViewById(R.id.sim_print).setOnClickListener(onClickListener);
        findViewById(R.id.sim_home_screen).setOnClickListener(onClickListener);
        findViewById(R.id.sim_desktop).setOnClickListener(onClickListener);
        findViewById(R.id.sim_settings).setOnClickListener(onClickListener);
        findViewById(R.id.sim_desktop_check).setOnClickListener(onClickListener);
        findViewById(R.id.sim_private_check).setOnClickListener(onClickListener);
        findViewById(R.id.overflow_button).setOnClickListener(onClickListener);
        findViewById(R.id.sim_reader).setOnClickListener(onClickListener);
        findViewById(R.id.voice_button).setOnClickListener(onClickListener);
    }


    private void showMenu() {
        mScroll.setScrollY(0);
        Animation grow = AnimationUtils.loadAnimation(this, R.anim.grow_menu);
        grow.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mCardView.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mCardView.startAnimation(grow);
        mHolder.setClickable(true);
        mHolder.setFocusable(true);
        mCardView.setSoundEffectsEnabled(false);
        mHolder.setSoundEffectsEnabled(false);
    }


    private void hideMenu() {
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade_menu);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mCardView.setVisibility(View.GONE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mCardView.startAnimation(fade);
        mHolder.setClickable(false);
        mHolder.setFocusable(false);
        mHolder.setSoundEffectsEnabled(false);
    }

    public static void scrollToTop(WebView mWebView) {
        ObjectAnimator anim = ObjectAnimator.ofInt(mWebView, "scrollY", mWebView.getScrollY(), 0);
        anim.setDuration(350);
        anim.start();
    }

    private void applyAppSettings() {
        mWebSettings.setTextZoom(Integer.parseInt(UserPreferences.getInstance(this).getFont()));
        defaultSearch = mPreferences.getString("search_engine", "");
        defaultProvider = mPreferences.getString("search_suggestions", "");
        homepage = mPreferences.getString("homepage", "");
        adBlockerEnabled = mPreferences.getBoolean("no_ads", true);
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(View.GONE);
        }
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            //noinspection deprecation
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }

        isKeyBoardShowing();
    }

    private void applyHomeButton() {
        if(!mPreferences.getBoolean("show_home", false)){
            mHomebutton.setVisibility(View.GONE);
        }else{
            mHomebutton.setVisibility(View.VISIBLE);
        }
        if(!mPreferences.getBoolean("show_bottom", false)){
            mTabs.setVisibility(View.GONE);
        }else{
            mTabs.setVisibility(View.VISIBLE);
            setBottomTabs();
        }
        defaultSearch = mPreferences.getString("search_engine", "");
        defaultProvider = mPreferences.getString("search_suggestions", "");
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(View.GONE);
        }
        adBlockerEnabled = mPreferences.getBoolean("no_ads", true);
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            //noinspection deprecation
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }
        isKeyBoardShowing();
    }


    void isKeyBoardShowing() {
        try {
            background_color.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Rect r = new Rect();
                background_color.getWindowVisibleDisplayFrame(r);
                int screenHeight = background_color.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    mTabs.setVisibility(View.GONE);
                } else {
                    if(!mPreferences.getBoolean("show_bottom", false)) {
                        mTabs.setVisibility(View.GONE);
                    }else{
                        mTabs.setVisibility(View.VISIBLE);
                    }
                }

            });

        } catch (Exception ignored) {

        }
    }


    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            if (urlToGrab != null)
                saveImageToDisk(urlToGrab);
        }
    }


    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (urlToGrab != null)
                        saveImageToDisk(urlToGrab);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // return here when file selected from camera or from SD Card
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    mSearchView.setText(spokenText);
                    try {
                        loadUrlFromTextBox();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    if (mCameraPhotoPath != null) {
                        results = new Uri[] {Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[] {Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }
    }


    @SuppressWarnings("Range")
    private void saveImageToDisk(final String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String filename = URLUtil.guessFileName(url, null, null);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            try {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name), filename);
            } catch (Exception exc) {
                Toast.makeText(MainActivity.this, exc.toString(), Toast.LENGTH_LONG).show();
            }
            request.setVisibleInDownloadsUi(true);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
            }
            Intent intent;
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
        } catch (Exception exc) {
            Toast.makeText(MainActivity.this, exc.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private  void createShortcut(){
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse(mSearchView.getText().toString()));
        intent1.putExtra("duplicate", false);
        ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, webViewTitle)
                .setShortLabel(webViewTitle)
                .setIcon(IconCompat.createWithBitmap(favoriteIcon))
                .setIntent(intent1)
                .build();
        ShortcutManagerCompat.requestPinShortcut(MainActivity.this, pinShortcutInfo, null);
    }
    @Override
    public void onCreateHomeScreenShortcut(AppCompatDialogFragment dialogFragment) {
        EditText shortcutNameEditText = dialogFragment.getDialog().findViewById(R.id.shortcut_name_edittext);
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        intent1.setAction(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse(mSearchView.getText().toString()));
        intent1.putExtra("duplicate", false);
        ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, webViewTitle)
                .setShortLabel(shortcutNameEditText.getText().toString())
                .setIcon(IconCompat.createWithBitmap(StaticUtils.getCircleBitmap(favoriteIcon)))
                .setIntent(intent1)
                .build();
        ShortcutManagerCompat.requestPinShortcut(MainActivity.this, pinShortcutInfo, null);
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.action_search));
        try {
            startActivityForResult(intent, 22);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.error),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        previousUiVisibility = background_color.getSystemUiVisibility();
        background_color.setPadding(0, 0, 0, 0);

        background_color.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // keep screen on when in fullscreen mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showSystemUI() {
        // disable keep screen on flag when leaving fullscreen mode
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        background_color.setSystemUiVisibility(previousUiVisibility);
        // fake a configuration change to set the right padding
        onConfigurationChanged(getResources().getConfiguration());
    }


    @SuppressWarnings("deprecation")
    public  void pagePrint(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        String jobName = (webViewTitle) + " Document";
        PrintDocumentAdapter printAdapter;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = webView.createPrintDocumentAdapter(jobName);
        }else {
            printAdapter = webView.createPrintDocumentAdapter();
        }
        assert printManager != null;
        PrintJob printJob = printManager.print(jobName, printAdapter,  new PrintAttributes.Builder().build());
        //see if print failed
        if(printJob.isFailed()){
            Toast.makeText(getApplicationContext(), "Failed to print", Toast.LENGTH_LONG).show();
        }

    }

    private void shortcutSwitch(String dataString) {
        switch (dataString) {
            case "new_tab":
                try {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setData(Uri.parse(homepage));
                    intent.putExtra("isNewTab" , false);
                    intent.removeExtra("new_tab");
                    startActivity(intent);
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "new_incognito_tab":
                try {
                    isIncognito = true;
                    //showPrivateNotification();
                    mWebView.isPrivateBrowsingEnabled();
                    //noinspection deprecation
                    mWebView.getSettings().setSavePassword(false);
                    mWebView.getSettings().setSaveFormData(false);
                    mWebView.getSettings().setDatabaseEnabled(false);
                    mWebView.getSettings().setDomStorageEnabled(false);
                    mWebView.loadUrl(homepage);
                    setColor(ContextCompat.getColor(MainActivity.this, R.color.md_grey_900));
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress < 100 && mProgress.getVisibility() == ProgressBar.GONE)
                mProgress.setVisibility(ProgressBar.VISIBLE);
            mProgress.setProgress(progress);
            if (progress == 100)
                mProgress.setVisibility(ProgressBar.GONE);
        }
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            try {
                favoriteIcon = icon;
                if (icon != null && StaticUtils.isLollipop()) {
                    setColor(Palette.from(icon).generate().getVibrantColor(Palette.from(icon).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))));

                }else{
                    setColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                }

            } catch (Exception ignored) {

            }

        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            try {
                webViewTitle = title;
                mSearchView.setText(mWebView.getUrl());
                mPreferences.edit().putString("last_page_reminder", mWebView.getUrl()).apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setTaskDescription(new ActivityManager.TaskDescription("Simplicity - " + mWebView.getTitle(), null, StaticUtils.fetchColorPrimary(MainActivity.this)));
                }
                if(!isIncognito) {
                    ArrayList<History> listBookmarks = UserPreferences.getHistory();
                    History bookmark = new History();
                    bookmark.setTitle(mWebView.getTitle());
                    bookmark.setUrl(mWebView.getUrl());
                    listBookmarks.add(bookmark);
                    UserPreferences.saveHistory(listBookmarks);
                }
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // for >= Lollipop, all in one
        public boolean onShowFileChooser(
                WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            requestStoragePermission();
            if (!hasStoragePermission())
                return false;

            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                // create the file where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // continue only if the file was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

            return true;
        }

        // creating image files (Lollipop only)
        private File createImageFile() throws IOException {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));

            if (!imageStorageDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }

            // create an image file name
            imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            return imageStorageDir;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;

            // hide mWebView and swipeRefreshLayout
            mWebView.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
            mAppbar.setVisibility(View.GONE);
            mTabs.setVisibility(View.GONE);



            // show customViewContainer
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;

            // activate immersive mode
            hideSystemUI();
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;

            // hide and remove customViewContainer
            mCustomView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.GONE);
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            // show swipeRefreshLayout and mWebView
                mWebView.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            mAppbar.setVisibility(View.VISIBLE);
            if(!mPreferences.getBoolean("show_bottom", false)) {
                mTabs.setVisibility(View.GONE);
            }else{
                mTabs.setVisibility(View.VISIBLE);
            }

            mCustomView = null;

            // deactivate immersive mode
            showSystemUI();
        }


    }



    // is a video played in fullscreen mode
    private boolean inCustomView() {
        return (mCustomView != null);
    }

    // deactivate fullscreen for video playback
    private void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }


    public void startReaderMode(){
        mWebView.loadUrl("javascript:window.simplicity_reader.processReaderMode(document.getElementsByTagName('body')[0].innerText,document.title);");

    }

    private class ReaderHandler  {
        Context context;

        ReaderHandler (Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void processReaderMode(String text, String title) {
            Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
            intent.putExtra("text", text);
            intent.putExtra("title",title);
            startActivity(intent);
        }
    }
}
