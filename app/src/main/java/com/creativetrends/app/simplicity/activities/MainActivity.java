package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.adapters.Bookmark;
import com.creativetrends.app.simplicity.suggestions.SuggestionsAdapter;
import com.creativetrends.app.simplicity.ui.CustomBehavior;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.ui.ViewSslCertificate;
import com.creativetrends.app.simplicity.utils.CreateShortcut;
import com.creativetrends.app.simplicity.utils.CustomGestureDetector;
import com.creativetrends.app.simplicity.utils.History;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.app.simplicity.webview.NestedWebView;
import com.creativetrends.simplicity.app.R;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity implements CreateShortcut.CreateHomeScreenSchortcutListener, SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences mPreferences;
    public NestedWebView mWebView;
    WebSettings mWebSettings;
    boolean isDesktop;
    boolean isIncognito;
    SimpleAutoComplete mSearchView;
    Toolbar mToolbar;
    String UrlCleaner, defaultSearch, defaultProvider;
    FloatingActionButton jump;

    ImageView mHomebutton, mSecure, mOverflow, vSearch, bookmarkicon, mForward, mRefresh;
    public static Bitmap favoriteIcon;
    AppBarLayout mAppbar;
    //BottomNavigationViewEx mTabs;
    HashMap<String, String> extraHeaders = new HashMap<>();
    CardView mCardView;
    LinearLayout top, items;
    ScrollView mScroll;
    FrameLayout mHolder,customViewContainer;
    AppCompatCheckBox pri, desk;
    public static String homepage;
    public static CookieManager cookieManager;
    private boolean adBlockerEnabled;
    CoordinatorLayout background_color;
    private static final int STORAGE_PERMISSION_CODE = 2284, REQUEST_STORAGE = 1;
    private String urlToGrab;
    AnimatedProgressBar mProgress;
    private long back_pressed;
    public static String webViewTitle;
    SwipeRefreshLayout swipeRefreshLayout;

    // fullscreen videos
    private MyWebChromeClient mWebChromeClient;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private int previousUiVisibility;

    // variables for camera and choosing files methods
    private static final int FILECHOOSER_RESULTCODE = 1;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    ArrayList<Bookmark> listBookmarks = new ArrayList<>();
    Bookmark bookmark;
    boolean TopTabs;
    boolean isLoading;
    private BottomSheetDialog alertDialog;
    public static final String EXTRA_URL = "extra_url";
    public static final String ACTION_URL_RESOLVED = "com.creativetrends.simplicity.app.URL_RESOLVED";
    public static String faviconUrl;
    public static SslCertificate sslCertificate;
    private ShareActionProvider mShareActionProvider;
    public static Palette.Swatch swatch;
    public int scrollPosition = 0;
    public static int titleColor;
    String getFaviconUrl;
    Document doc;
    private final BroadcastReceiver mUrlResolvedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent resolvedIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (TextUtils.equals(getPackageName(), resolvedIntent.getPackage())) {
                String url = intent.getStringExtra(EXTRA_URL);
                mWebView.loadUrl(url);
            } else {
                startActivity(resolvedIntent);
            }
            ResultReceiver receiver = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                receiver = intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER);
            }
            if (receiver != null) {
                receiver.send(RESULT_CANCELED, new Bundle());
            }
        }
    };




    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
                    if (mWebView != null && !mWebView.canGoForward()) {
                        return;
                    }
                    if (mWebView != null && mWebView.canGoForward()) {
                        hideMenu();
                        mWebView.goForward();
                    }
                    return;

                case R.id.sim_bookmark:
                    hideMenu();
                    if (UserPreferences.isStarred(mWebView.getUrl())) {
                        Toast.makeText(MainActivity.this, mWebView.getTitle().replace("", "") + " " + getResources().getString(R.string.already_to_bookmarks), Toast.LENGTH_SHORT).show();
                    } else {
                        String getWebTitle = webViewTitle;
                        String setLetter = getWebTitle.substring(0,1);
                        listBookmarks = UserPreferences.getBookmarks();
                        bookmark = new Bookmark();
                        bookmark.setTitle(mWebView.getTitle());
                        bookmark.setUrl(mWebView.getUrl());
                        bookmark.setLetter(setLetter);
                        bookmark.setImage(Palette.from(favoriteIcon).generate().getVibrantColor(Palette.from(favoriteIcon).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))));
                        listBookmarks.add(bookmark);
                        UserPreferences.saveBookmarks(listBookmarks);
                        Toast.makeText(MainActivity.this, mWebView.getTitle().replace("", "") + " " + getResources().getString(R.string.added_to_bookmarks), Toast.LENGTH_SHORT).show();
                    }
                    return;


                case R.id.sim_downloads:
                    try {
                        hideMenu();
                        Intent downloadManagerIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                        downloadManagerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(downloadManagerIntent);
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                    }catch (NullPointerException ignored){

                    }
                    return;

                case R.id.sim_refresh:
                    hideMenu();
                    if(isLoading){
                        mWebView.stopLoading();
                    }else{
                        mWebView.reload();
                    }
                    return;

                case R.id.sim_stop:
                    hideMenu();
                    try {
                        viewSslCertificate(mWebView);
                    }catch (Exception i){
                        i.printStackTrace();
                    }
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
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    } else {
                        //showPrivateNotification();
                        mWebView.isPrivateBrowsingEnabled();
                        mWebView.getSettings().setSavePassword(false);
                        mWebView.getSettings().setSaveFormData(false);
                        mWebView.getSettings().setDatabaseEnabled(false);
                        mWebView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(MainActivity.this, R.color.md_grey_900));
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

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
                    isDesktop = !isDesktop;
                    desk.setChecked(isDesktop);
                    if (!isDesktop) {
                        mWebSettings.setUserAgentString("");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebSettings.setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = false;
                    } else {
                        mWebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
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
                    isDesktop = !isDesktop;
                    desk.setChecked(isDesktop);
                    if (!isDesktop) {
                        mWebSettings.setUserAgentString("");
                        mWebSettings.setLoadWithOverviewMode(true);
                        mWebSettings.setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = false;
                    } else {
                        mWebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
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
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    } else {
                        //showPrivateNotification();
                        mWebView.isPrivateBrowsingEnabled();
                        mWebView.getSettings().setSavePassword(false);
                        mWebView.getSettings().setSaveFormData(false);
                        mWebView.getSettings().setDatabaseEnabled(false);
                        mWebView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(MainActivity.this, R.color.md_grey_900));
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    }
                    hideMenu();
                    return;

                case R.id.voice_button:
                    promptSpeechInput();
                    return;

                case R.id.sim_close:
                    hideMenu();
                    finish();
                    if(mPreferences.getBoolean("clear_data", false) && getIntent().getBooleanExtra("isNewTab", false)){
                        StaticUtils.deleteCache(SimplicityApplication.getContextOfApplication());
                        ArrayList<History> listHistory = UserPreferences.getHistory();
                        listHistory.clear();
                        UserPreferences.saveHistory(listHistory);
                        mPreferences.edit().putString("last_page_reminder", homepage).apply();
                    }
                    return;
                case R.id.jumpTop:
                    if(scrollPosition >= 10){
                        scrollToTop(mWebView);
                        mAppbar.setExpanded(true, true);
                    }
                    return;

                default:
                    hideMenu();

            }

        }

    };


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TopTabs = UserPreferences.getInstance(this).getTabs().equals("top");
        super.onCreate(savedInstanceState);
        if (TopTabs) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_bottom);
        }
        jump = findViewById(R.id.jumpTop);
        mHomebutton = findViewById(R.id.toolbar_home);
        customViewContainer = findViewById(R.id.customViewContainer);
        background_color = findViewById(R.id.background_color);
        mSecure = findViewById(R.id.secure_site);
        bookmarkicon = findViewById(R.id.sim_bookmark);
        mOverflow = findViewById(R.id.overflow_button);
        mRefresh = findViewById(R.id.sim_refresh);
        mProgress = findViewById(R.id.progressBar);
        vSearch = findViewById(R.id.voice_button);
        mForward = findViewById(R.id.sim_go_forward);
        top = findViewById(R.id.root_overflow);
        items = findViewById(R.id.sub_overflow);
        mAppbar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (getSupportActionBar() != null) {
            setSupportActionBar(mToolbar);
        }
        if (!TopTabs) {
            CustomBehavior.FABbehavior = true;
        }
        mSearchView = findViewById(R.id.search_box);
        mWebView = findViewById(R.id.webView);


        forClicks();

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            //noinspection deprecation
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        //noinspection deprecation
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.addJavascriptInterface(new ReaderHandler(MainActivity.this), "simplicity_reader");
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        extraHeaders.put("DNT", "1");
        homepage = mPreferences.getString("homepage", "");
        defaultSearch = mPreferences.getString("search_engine", "");
        webViewTitle = getString(R.string.app_name);

        mWebView.setOnScrollChangedCallback((l, t) -> {
            scrollPosition = t;
            if(scrollPosition >= 10){
                jump.setVisibility(View.VISIBLE);
            }else{
                jump.setVisibility(View.GONE);
            }
        });

        mWebView.setOnLongClickListener(view1 -> {


            if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || mWebView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_TYPE) {
                BottomSheetMenuDialog dialog = new BottomSheetBuilder(MainActivity.this, null)
                        .setMode(BottomSheetBuilder.MODE_LIST)

                        .setMenu(R.menu.menu_image)
                        .addTitleItem(R.string.image_actions)
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
            } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
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
                return true;
            } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.PHONE_TYPE) { // Phone number click
                Intent intent14 = new Intent(Intent.ACTION_DIAL);
                intent14.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                startActivity(intent14);
                return true;
            } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.UNKNOWN_TYPE) {
                return false;
            }
            return true;
        });

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
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if ((url.contains("market://"))
                            || url.contains("mailto:")
                            || url.contains("play.google")
                            || url.contains("tel:")
                            || url.contains("intent:")
                            || url.contains("geo:")
                            || url.contains("streetview:")
                            || url.contains("ebay:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        try {
                            view.getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {

                            e.printStackTrace();
                        }
                        return true;
                    }
                    return false;
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    return true;
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
                isLoading = true;
            }


            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                super.onUnhandledKeyEvent(view, event);
            }


            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                continueMsg.sendToTarget();
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                super.onSafeBrowsingHit(view, request, threatType, callback);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    callback.backToSafety(true);
                }
                Toast.makeText(view.getContext(), "Unsafe web page blocked.", Toast.LENGTH_LONG).show();
            }


            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return false;
                }
                super.onRenderProcessGone(view, detail);
                if (!detail.didCrash()) {
                    // Renderer was killed because the system ran out of memory.
                    // The app can recover gracefully by creating a new WebView instance
                    // in the foreground.
                    Log.e("MY_APP", "System killed the WebView rendering process " +
                            "to reclaim memory. Recreating...");

                    if (view != null) {
                        ((ViewGroup)view.getParent()).removeView(view);
                        view.destroy();
                    }
                    // By this point, the instance variable "view" is guaranteed
                    // to be null, so it's safe to reinitialize it.
                    return true; // The app continues executing.
                }
                // Renderer crashed because of an internal error, such as a memory
                // access violation.
                Log.e("MY_APP", "The WebView rendering process crashed!");

                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

            @Override
            public void onLoadResource(WebView view, String url){
                super.onLoadResource(view, url);
                String str;
                if (view.getUrl().contains("https://")) {
                    str = view.getUrl().replace("https://", "<font color='#0b8043'>https</font>"+"<font color='#9D9D9D'>://</font>");
                    if(!mSearchView.hasFocus()) {
                        mSearchView.setText(Html.fromHtml(str), TextView.BufferType.SPANNABLE);
                    }
                    mSecure.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_secure_white));
                    mSecure.setVisibility(View.VISIBLE);
                }else{
                    str = view.getUrl().replace("http://", "<font color='#d32f2f'>http</font>"+"<font color='#9D9D9D'>://</font>");
                    if(!mSearchView.hasFocus()) {
                        mSearchView.setText(Html.fromHtml(str), TextView.BufferType.SPANNABLE);
                    }
                    mSecure.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unsecure_white));
                    mSecure.setVisibility(View.VISIBLE);
                }

                if(mRefresh != null) {
                    if (isLoading) {
                        mRefresh.setImageDrawable(ContextCompat.getDrawable(SimplicityApplication.getContextOfApplication(), R.drawable.ic_stop_loading));
                    } else {
                        mRefresh.setImageDrawable(ContextCompat.getDrawable(SimplicityApplication.getContextOfApplication(), R.drawable.ic_refresh_page));
                    }
                }


                if (mWebView.canGoForward()) {
                    mForward.setAlpha(0.9f);
                }else{
                    mForward.setAlpha(0.2f);
                }

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
                isLoading = !(mWebView != null && mWebView.getProgress() == 100);
                if(mWebView.getTitle().equals("No Connection")) {
                    mSearchView.setText(mWebView.getTitle());
                }else {
                    mSearchView.setText(mWebView.getUrl());
                }
                sslCertificate = view.getCertificate();
            }
        });



        mWebChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

            final String filename1 = URLUtil.guessFileName(url, contentDisposition, mimeType);
            if (UserPreferences.isDownloadableFile(filename1)) {
                if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                alertDialog = new BottomSheetDialog(this);
                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);
                ((TextView) v.findViewById(R.id.title)).setText(R.string.app_name);
                ((TextView) v.findViewById(R.id.content)).setText(String.format(getString(R.string.download_warning), filename1));
                v.findViewById(R.id.confirm).setOnClickListener(v1 -> {
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
                        alertDialog.dismiss();

                    }
                });

                v.findViewById(R.id.cancel).setOnClickListener(v12 -> alertDialog.dismiss());

                alertDialog.setOnDismissListener(DialogInterface::dismiss);

                alertDialog.setContentView(v);
                alertDialog.show();
            }else {
                if (Build.VERSION.SDK_INT >= M) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    } else {
                        try {
                            Toast.makeText(MainActivity.this, getString(R.string.downloading), Toast.LENGTH_SHORT).show();
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
                }
            }

        });

        mSearchView.setAdapter(new SuggestionsAdapter(this));
        if (isIncognito) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mSearchView.setImeOptions(mSearchView.getImeOptions() | EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING);
            }
        }
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                StaticUtils.hideKeyboard(mSearchView);
                try {
                if(mSearchView.getText().toString().contains("simplicity://flags")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent Intent = new Intent(MainActivity.this, ExperimentalActivity.class);
                    startActivity(Intent);
                }else if(mSearchView.getText().toString().contains("simplicity://history")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(history);

                }else if(mSearchView.getText().toString().contains("simplicity://bookmarks")){
                    mSearchView.setText(mWebView.getUrl());
                    Intent settingsIntent = new Intent(MainActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                }else{
                    loadUrlFromTextBox();
                }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mSearchView.clearFocus();
                return true;
            }
            return false;
        });
        mSearchView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                StaticUtils.hideKeyboard(mSearchView);
                try {
                    if(mSearchView.getText().toString().contains("simplicity://flags")) {
                        mSearchView.setText(mWebView.getUrl());
                        Intent Intent = new Intent(MainActivity.this, ExperimentalActivity.class);
                        startActivity(Intent);
                    }else if(mSearchView.getText().toString().contains("simplicity://history")) {
                        mSearchView.setText(mWebView.getUrl());
                        Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivity(history);

                    }else if(mSearchView.getText().toString().contains("simplicity://bookmarks")){
                        mSearchView.setText(mWebView.getUrl());
                        Intent settingsIntent = new Intent(MainActivity.this, BookmarksActivity.class);
                        startActivity(settingsIntent);
                    }else{
                        loadUrlFromTextBox();
                    }
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

        });

        mSearchView.setOnItemClickListener((parent, view, position, rowId) -> {
            try {
                StaticUtils.hideKeyboard(mSearchView);
                mSearchView.clearFocus();
                loadUrlFromTextBox();
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

        });


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isKeyboardOpen();
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
        registerReceiver(mUrlResolvedReceiver, new IntentFilter(ACTION_URL_RESOLVED));
        if (getIntent().getDataString() != null) {
            shortcutSwitch(getIntent().getDataString());
        }
    }


    @Override
    protected void onStop() {
        CookieManager.getInstance().flush();
        unregisterReceiver(mUrlResolvedReceiver);
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        applyHomeButton();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        CookieManager.getInstance().setAcceptCookie(!isIncognito);

    }

    @Override
    public void onPause() {
        mWebView.onPause();
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (inCustomView())
            hideCustomView();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }else if (mWebView.canGoBack()) {
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
                mPreferences.edit().putString("last_page_reminder", homepage).apply();
            }
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
        if (unUrlCleaner.startsWith("www") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".com") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".gov") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".net") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".org") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".mil") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".edu") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".int") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".ly") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".de") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".uk") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".it") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".jp") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".ru") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".gl") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.endsWith(".me") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else if (unUrlCleaner.contains(".") && !unUrlCleaner.contains(" ") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner);
        } else {
            mWebView.loadUrl(defaultSearch + unUrlCleaner);
        }

    }



    private void setColor(int color) {
        color = isIncognito ? ContextCompat.getColor(this, R.color.md_grey_900) : color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.darkColor(color));
            colorAnimation.setDuration(90);
            colorAnimation.addUpdateListener(animator -> {
                getWindow().setStatusBarColor((int) animator.getAnimatedValue());
                if (mPreferences.getBoolean("nav_color", false)) {
                    getWindow().setNavigationBarColor((int) animator.getAnimatedValue());
                }else{
                    getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.black));

                }

            });
            colorAnimation.start();
        }
        int colorFrom = ContextCompat.getColor(this, !isIncognito ? R.color.md_grey_900 : R.color.no_fav);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable backgroundFrom = mToolbar.getBackground();
            if (backgroundFrom instanceof ColorDrawable)
                colorFrom = ((ColorDrawable) backgroundFrom).getColor();
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
            colorAnimation.setDuration(100);
            colorAnimation.addUpdateListener(animator -> {
                mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                mProgress.setBackgroundColor((int) animator.getAnimatedValue());
                jump.setBackgroundTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
                jump.setRippleColor(ContextCompat.getColor(getBaseContext(), R.color.colorSemiWhite));
                swipeRefreshLayout.setColorSchemeColors((int) animator.getAnimatedValue());

            });
            colorAnimation.start();
        }

    }

    protected void getBookmarkIcon() {
        try {
            if (bookmarkicon != null && UserPreferences.isStarred(mWebView.getUrl())) {
                bookmarkicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_full));
            } else if (bookmarkicon != null) {
                bookmarkicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_star_border));
            }
        }catch(Exception i){
            i.printStackTrace();
        }
    }

    public void isKeyboardOpen(){
        try {
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {

                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;
                //Log.d(TAG, "keypadHeight = " + keypadHeight);
                if (keypadHeight > screenHeight * 0.15) {
                    //Keyboard is opened
                    if (!TopTabs && !mSearchView.hasFocus()) {
                        mAppbar.setVisibility(View.GONE);
                    }
                    if (TopTabs && mPreferences.getBoolean("full_screen", false)) {
                        hideNavBar();
                    }
                } else {
                    if (!TopTabs) {
                        mAppbar.setVisibility(View.VISIBLE);
                    }
                    if (TopTabs && mPreferences.getBoolean("full_screen", false)) {
                        hideNavBar();
                    }

                }

            });
        }catch(Exception ignored){

        }
    }

    private void hideNavBar() {
        View v = getWindow().getDecorView();
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }



    public String getUrlDomainName(String url) {
        String domainName = url;
        int index = domainName.indexOf("://");
        if (index != -1) {
            domainName = domainName.substring(index + 3);
        }
        index = domainName.indexOf('/');
        if (index != -1) {
            domainName = domainName.substring(0, index);
        }
        return domainName;
    }


   /* public void setBottomTabs(){
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

    }*/



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
        findViewById(R.id.sim_close).setOnClickListener(onClickListener);
        findViewById(R.id.sim_downloads).setOnClickListener(onClickListener);
        findViewById(R.id.jumpTop).setOnClickListener(onClickListener);
    }


    private void showMenu() {
        mScroll.setScrollY(0);
        getBookmarkIcon();
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.grow_menu);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                mCardView.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mCardView.startAnimation(fade);
        mHolder.setClickable(true);
        mHolder.setFocusable(true);
        mCardView.setSoundEffectsEnabled(false);
        mHolder.setSoundEffectsEnabled(false);
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(MainActivity.this, R.anim.translate_from_top), 0.3f);
        items.setLayoutAnimation(lac);
        items.post(() -> {

        });
        LayoutAnimationController lac2 = new LayoutAnimationController(AnimationUtils.loadAnimation(MainActivity.this, R.anim.first_row_animation), 0.3f);
        top.setLayoutAnimation(lac2);
        top.post(() -> {

        });

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
        if(mPreferences.getBoolean("gestures_ok", false)){
            //noinspection deprecation
            mWebView.setGestureDetector(new GestureDetector(new CustomGestureDetector(mWebView, this)));
        }

        if(TopTabs && mPreferences.getBoolean("full_screen", false)){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            }
    }

    private void applyHomeButton() {
        if(!mPreferences.getBoolean("show_home", false)){
            mHomebutton.setVisibility(View.GONE);
        }else{
            mHomebutton.setVisibility(View.VISIBLE);
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
        if(mPreferences.getBoolean("gestures_ok", false)){
            //noinspection deprecation
            //mWebView.setGestureDetector(new GestureDetector(new CustomGestureDetector(mWebView, this)));
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

    public void viewSslCertificate(View view) {
        // Show the `ViewSslCertificateDialog` `AlertDialog` and name this instance `@string/view_ssl_certificate`.
        DialogFragment viewSslCertificateDialogFragment = new ViewSslCertificate();
        viewSslCertificateDialogFragment.show(getFragmentManager(), getString(R.string.view_ssl_certificate));
    }



    @Override
    public void onRefresh() {
        if(mWebView != null){
            mWebView.reload();
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 200);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }

        @Override
        public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
            try {
                if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                alertDialog = new BottomSheetDialog(MainActivity.this);
                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);
                ((TextView) v.findViewById(R.id.title)).setText(R.string.app_name);
                ((TextView) v.findViewById(R.id.content)).setText(message);

                v.findViewById(R.id.cancel).setVisibility(View.GONE);
                v.findViewById(R.id.confirm).setOnClickListener(v1 -> {
                    result.confirm();
                    alertDialog.dismiss();
                });

                alertDialog.setOnDismissListener(dialog -> {
                    result.cancel();
                    dialog.dismiss();
                });

                alertDialog.setContentView(v);
                alertDialog.show();
            }catch(Exception i){
                i.printStackTrace();
            }
            return true;

        }




        @Override
        public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
            if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
            alertDialog = new BottomSheetDialog(MainActivity.this);
            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);

            ((TextView) v.findViewById(R.id.title)).setText(R.string.app_name);
            ((TextView) v.findViewById(R.id.content)).setText(message);

            v.findViewById(R.id.cancel).setOnClickListener(v1 -> {
                result.cancel();
                alertDialog.dismiss();
            });

            v.findViewById(R.id.confirm).setOnClickListener(v12 -> {
                result.confirm();
                alertDialog.dismiss();
            });

            alertDialog.setOnDismissListener(dialog -> {
                result.cancel();
                dialog.dismiss();
            });

            alertDialog.setContentView(v);
            alertDialog.show();
            return true;

        }

        @Override
        public boolean onJsPrompt(WebView view, String url, final String message, final String defaultValue, final JsPromptResult result) {
            if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
            alertDialog = new BottomSheetDialog(MainActivity.this);
            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.activity_bottomsheet, null, false);

            ((TextView) v.findViewById(R.id.title)).setText(message);
            v.findViewById(R.id.content).setVisibility(View.GONE);
            v.findViewById(R.id.inputLayout).setVisibility(View.VISIBLE);

            EditText input = v.findViewById(R.id.input);
            input.setHint(defaultValue);

            v.findViewById(R.id.cancel).setOnClickListener(v1 -> {
                result.cancel();
                alertDialog.dismiss();
            });

            v.findViewById(R.id.confirm).setOnClickListener(v12 -> {
                result.confirm();
                alertDialog.dismiss();
            });

            alertDialog.setOnDismissListener(dialog -> {
                result.cancel();
                dialog.dismiss();
            });

            alertDialog.setContentView(v);
            alertDialog.show();
            return true;

        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            mProgress.setProgress(progress);
            if (progress < 100) {
                mProgress.setVisibility(View.VISIBLE);
            } else {
                mProgress.setVisibility(View.GONE);
            }
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);

        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            try {
                favoriteIcon = icon;
                if (icon != null && StaticUtils.isLollipop()) {
                    setColor(Palette.from(icon).generate().getVibrantColor(Palette.from(icon).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav))));
                }else{
                    setColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav));

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

                if(!isIncognito && !mSearchView.getText().toString().equals(homepage)) {
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
                File photoFile;
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);

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
        private File createImageFile() {
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
            jump.setVisibility(View.GONE);

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
