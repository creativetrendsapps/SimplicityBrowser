package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.webkit.MimeTypeMap;
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
import android.webkit.WebViewDatabase;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.palette.graphics.Palette;

import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.adapters.BookmarkItems;
import com.creativetrends.app.simplicity.suggestions.SuggestionsAdapterPrivate;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.ui.SimplicitySslCertificate;
import com.creativetrends.app.simplicity.utils.FileSize;
import com.creativetrends.app.simplicity.utils.SimplicityDownloader;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.app.simplicity.webview.PrivateWebview;
import com.creativetrends.simplicity.app.R;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT;


public class PrivateActivity extends BaseActivity{
    public static Activity priavteActivity;
    SharedPreferences mPreferences;
    public PrivateWebview mWebView;
    WebSettings mWebSettings;
    boolean isDesktop;
    boolean isIncognito = true;
    public SimpleAutoComplete mSearchView;
    Toolbar mToolbar;
    String UrlCleaner, defaultSearch, defaultProvider;
    EditText shortcutNameEditText;
    public static ImageView mHomebutton, mJump, mSecure, mAddress, mTabs, mOverflow, vSearch, bookmarkicon, mForward, mRefresh, mStop, mClose;
    //TextView mBadgeText;
    public static Bitmap favoriteIcon;
    AppBarLayout mAppbar;
    //BottomNavigationViewEx mTabs;
    HashMap<String, String> extraHeaders = new HashMap<>();
    CardView mCardView;
    LinearLayout bCardView, top, items;
    FrameLayout mHolder,customViewContainer;
    AppCompatCheckBox pri, desk;
    public static String homepage;
    //private boolean adBlockerEnabled;
    CoordinatorLayout background_color;
    private static final int STORAGE_PERMISSION_CODE = 2284, REQUEST_STORAGE = 1;
    private String urlToGrab;
    AnimatedProgressBar mProgress;
    private long back_pressed;
    public static String webViewTitle;

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
    ArrayList<BookmarkItems> listBookmarks = new ArrayList<>();
    BookmarkItems bookmark;
    boolean TopTabs;
    boolean isLoading;
    public static SslCertificate sslCertificate;
    public int scrollPosition = 0;
    public static String touchIcon;
    String filename1;
    public static String file_size_main;
    //drawer
    DrawerLayout mDrawer;
    //navigation
    NavigationView mNavigation;
    boolean isTablet;
    RelativeLayout desk_rel;
    private static String userAgent;
    public static RelativeLayout mStop_rel, mRefresh_rel;
    LinearLayout linearLayout;



    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_menu_holder:
                    hideMenu();
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
                        Cardbar.snackBar(getApplicationContext(), mWebView.getTitle() + " " + getResources().getString(R.string.already_to_bookmarks), true).show();

                    } else {
                        String getWebTitle = webViewTitle;
                        String setLetter = getWebTitle.substring(0,1);
                        listBookmarks = UserPreferences.getBookmarks();
                        bookmark = new BookmarkItems();
                        bookmark.setTitle(mWebView.getTitle());
                        bookmark.setUrl(mWebView.getUrl());
                        bookmark.setLetter(setLetter);
                        bookmark.setImage(Palette.from(favoriteIcon).generate().getVibrantColor(Palette.from(favoriteIcon).generate().getMutedColor(ContextCompat.getColor(PrivateActivity.this, R.color.md_blue_600))));
                        listBookmarks.add(bookmark);
                        UserPreferences.saveBookmarks(listBookmarks);
                        Cardbar.snackBar(getApplicationContext(), mWebView.getTitle()+ " " + getResources().getString(R.string.added_to_bookmarks), true).show();

                    }
                    return;


                case R.id.sim_download:
                    try {
                        hideMenu();
                        Intent downloadIntent = new Intent(PrivateActivity.this, FilePickerActivity.class);
                        startActivity(downloadIntent);
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                    } catch (NullPointerException ignored) {

                    }
                    return;


                case R.id.sim_downloads:
                    try {
                        hideMenu();
                        StaticUtils.openDownloads(PrivateActivity.this);
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                    } catch (NullPointerException ignored) {

                    }
                    return;


                case R.id.sim_new_window:
                    hideMenu();
                    Intent tab = new Intent(PrivateActivity.this, MainActivity.class);
                    tab.setData(Uri.parse("about:blank"));
                    startActivity(tab);
                    return;

                case R.id.sim_private_mode:
                    Intent new_pri = new Intent(PrivateActivity.this, PrivateActivity.class);
                    new_pri.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                    new_pri.setData(Uri.parse("about:blank"));
                    startActivity(new_pri);
                    return;

                case R.id.sim_history:
                    hideMenu();
                    Intent history = new Intent(PrivateActivity.this, HistoryActivity.class);
                    startActivity(history);
                    finish();
                    return;

                case R.id.sim_books:
                    hideMenu();
                    Intent settingsIntent = new Intent(PrivateActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                    finish();
                    return;

                case R.id.sim_find:
                    hideMenu();
                    //noinspection deprecation
                    mWebView.showFindDialog(null, true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    return;

                case R.id.sim_share:
                    hideMenu();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("*/*");
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
                        createFileName();
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
                        mWebView.getSettings().setUserAgentString(null);
                        mWebView.getSettings().setLoadWithOverviewMode(true);
                        mWebView.getSettings().setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = false;
                    } else {
                        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Simplicity/57.0.3098.116");
                        mWebView.getSettings().setLoadWithOverviewMode(true);
                        mWebView.getSettings().setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = true;
                    }
                    return;

                case R.id.sim_settings:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    Intent Intent = new Intent(PrivateActivity.this, SettingsActivity.class);
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
                        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                        mWebView.getSettings().setLoadWithOverviewMode(true);
                        mWebView.getSettings().setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = false;
                    } else {
                        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Simplicity/57.0.3098.116");
                        mWebView.getSettings().setLoadWithOverviewMode(true);
                        mWebView.getSettings().setUseWideViewPort(true);
                        mWebView.reload();
                        isDesktop = true;
                    }
                    return;

                case R.id.sim_private_check:

                    return;

                case R.id.voice_button:
                    promptSpeechInput();
                    return;

                case R.id.sim_close:
                    hideMenu();
                    try{
                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask();
                            if(!mSearchView.getText().toString().contains("about:blank"))
                                deleteCookies(mWebView.getUrl());
                        } else {
                            finish();
                            if(!mSearchView.getText().toString().contains("about:blank"))
                            deleteCookies(mWebView.getUrl());
                        }
                    }catch (NullPointerException z){
                        z.printStackTrace();
                    }catch (Exception p){
                        p.printStackTrace();
                    }
                    return;

                case R.id.sim_home_tabs:
                    mAppbar.setExpanded(true, true);
                    mWebView.loadUrl(homepage, extraHeaders);
                    break;

                case R.id.jump_rel:
                case R.id.sim_jump_tabs:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    if(scrollPosition >= 10){
                        scrollToTop(mWebView);
                        mAppbar.setExpanded(true, true);
                    }
                    break;

                case R.id.search_rel:
                case R.id.sim_address:
                    mSearchView.requestFocus();
                    mSearchView.selectAll();
                    try{
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }catch (Exception in){
                        in.printStackTrace();
                    }
                    break;


                case R.id.tabs_rel:
                case R.id.sim_tabs_tabs:
                    Intent intent = new Intent(PrivateActivity.this, PrivateActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.setData(Uri.parse("about:blank"));
                    startActivity(intent);
                    break;


                case R.id.overflow_rel:
                case R.id.sim_over_button:
                    showMenu();
                    break;

                case R.id.home_rel:
                case R.id.sim_refresh:
                    mWebView.reload();
                    return;

                case R.id.home2_rel:
                case R.id.sim_refresh2:
                    if(isLoading) {
                        mWebView.stopLoading();
                        isLoading = false;
                    }
                    return;
                case R.id.sim_stop:
                    hideMenu();
                    try {
                        viewSslCertificate(mWebView);
                    }catch (Exception g){
                        g.printStackTrace();
                    }
                    return;



                default:

            }

        }

    };


    @SuppressLint({"SetJavaScriptEnabled", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        priavteActivity = this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        TopTabs = UserPreferences.getInstance(this).getTabs().equals("top");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_private);
        isIncognito = true;
        linearLayout = findViewById(R.id.lin_about);
        mJump = findViewById(R.id.sim_jump_tabs);
        mHomebutton = findViewById(R.id.sim_home_tabs);
        customViewContainer = findViewById(R.id.customViewContainer);
        background_color = findViewById(R.id.background_color);
        mSecure = findViewById(R.id.secure_site);
        bookmarkicon = findViewById(R.id.sim_bookmark);
        mOverflow = findViewById(R.id.sim_over_button);
        mRefresh = findViewById(R.id.sim_refresh);
        mRefresh_rel = findViewById(R.id.home_rel);
        mStop = findViewById(R.id.sim_refresh2);
        mStop_rel = findViewById(R.id.home2_rel);

        mProgress = findViewById(R.id.progressBar);
        vSearch = findViewById(R.id.voice_button);
        mForward = findViewById(R.id.sim_go_forward);
        top = findViewById(R.id.root_overflow);
        items = findViewById(R.id.sub_overflow);
        mAppbar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        bCardView = findViewById(R.id.root_bottom_tabs);
        mAddress = findViewById(R.id.sim_address);
        mTabs = findViewById(R.id.sim_tabs_tabs);
        desk_rel = findViewById(R.id.desk_rel);
        if(isTablet){
            desk_rel.setVisibility(View.GONE);
        }
        //mBadgeText = findViewById(R.id.tabs_badge);
        //relative layouts start

        //relative layouts end
        mDrawer = findViewById(R.id.drawer);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigation = findViewById(R.id.nav_tabs);
        if (getSupportActionBar() != null) {
            setSupportActionBar(mToolbar);
        }
        mSearchView = findViewById(R.id.search_box);
        mWebView = findViewById(R.id.webView);
        userAgent = System.getProperty("http.agent");
        linearLayout.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);



        forClicks();
        mWebSettings = mWebView.getSettings();
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        if(isTablet){
            mWebSettings.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Simplicity/57.0.3098.116");
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
        }else{
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
        }
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setAppCacheEnabled(false);
        mWebSettings.setDatabaseEnabled(false);
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }
        if(UserPreferences.getBoolean("lite_mode", false)){
            mWebSettings.setLoadsImagesAutomatically(false);
        }
        mWebSettings.setAllowFileAccessFromFileURLs(false);
        mWebSettings.setAllowUniversalAccessFromFileURLs(false);
        mWebView.isPrivateBrowsingEnabled();
        WebViewDatabase.getInstance(this).clearFormData();
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setDatabaseEnabled(false);
        mWebView.getSettings().setDomStorageEnabled(false);
        setColor(ContextCompat.getColor(PrivateActivity.this, R.color.md_grey_900));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //mWebSettings.setSupportMultipleWindows(true);

        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("DNT", "1");

        homepage = "about:blank";
        defaultSearch = "https://duckduckgo.com/?q=";
        webViewTitle = getString(R.string.app_name);

        mWebView.setOnScrollChangedCallback((l, t) -> {
            scrollPosition = t;
            if(scrollPosition >= 10){
                mJump.setClickable(true);
            }else{
                mJump.setClickable(false);
            }
        });

        mWebView.setOnLongClickListener(view1 -> {

            if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || mWebView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_TYPE) {
                urlToGrab = mWebView.getHitTestResult().getExtra();
                new FileSize(urlToGrab).execute();
                BottomSheetMenuDialog dialog = new BottomSheetBuilder(PrivateActivity.this, null)
                        .setMode(BottomSheetBuilder.MODE_LIST)
                        .setMenu(R.menu.menu_image_private)
                        .addTitleItem(R.string.image_actions)
                        .setItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.image_save:
                                    filename1 = URLUtil.guessFileName(urlToGrab, null, getMimeType(urlToGrab));
                                    if (UserPreferences.getBoolean("rename", false)) {
                                        //new FileSize(urlToGrab).execute();
                                        createDownload();
                                    }else {
                                        requestStoragePermission();
                                    }
                                    break;

                                case R.id.image_open:
                                    Intent intent13 = new Intent(PrivateActivity.this, PrivateActivity.class);
                                    intent13.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                                    intent13.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                    startActivity(intent13);
                                    break;

                                case R.id.image_copy:
                                    ClipboardManager clipboard = (ClipboardManager) PrivateActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
                BottomSheetMenuDialog dialog = new BottomSheetBuilder(PrivateActivity.this, null)
                        .setMode(BottomSheetBuilder.MODE_LIST)
                        .setMenu(R.menu.menu_link_private)
                        .setItemClickListener(item -> {

                            switch (item.getItemId()) {

                                case R.id.link_open:
                                    Intent intent12 = new Intent(PrivateActivity.this, PrivateActivity.class);
                                    intent12.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                                    intent12.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                                    startActivity(intent12);
                                    break;

                                case R.id.link_copy:
                                    ClipboardManager clipboard = (ClipboardManager) PrivateActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
            } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.PHONE_TYPE) {
                Intent intent14 = new Intent(Intent.ACTION_DIAL);
                intent14.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                startActivity(intent14);
                return true;
            } else {
                Log.d("MainActivity", "Show webview context");
                return false;
            }
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
        mWebView.loadUrl(UrlCleaner, extraHeaders);

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


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("market://")
                        || url.contains("mailto:")
                        || url.contains("play.google")
                        || url.contains("tel:")
                        || url.contains("intent:")
                        || url.contains("geo:")
                        || url.contains("streetview:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {

                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("http://") || url.startsWith("https://")) {
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
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
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
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isLoading = true;
                if(mWebView!= null && mWebView.getVisibility() == View.GONE && url != null && !url.contains("about:blank")){
                    linearLayout.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
            }


            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
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
                Cardbar.snackBar(getApplicationContext(), "Unsafe web page blocked.", true).show();

            }


            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return false;
                }
                super.onRenderProcessGone(view, detail);
                if (!detail.didCrash()) {
                    Log.d("Simplicity", "System killed the WebView rendering process " + "to reclaim memory. Recreating...");

                    if (view != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                        view.destroy();
                    }
                    return true;
                }
                Log.d("Simplicity", "The WebView rendering process crashed!");
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                if (!PrivateActivity.this.isDestroyed()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrivateActivity.this);
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
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                String str;
                if (view.getUrl().contains("https://")) {
                    str = view.getUrl().replace("https://", "https://");
                    if (!mSearchView.hasFocus()) {
                        mSearchView.setText(Html.fromHtml(str), TextView.BufferType.SPANNABLE);
                    }
                    mSecure.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_secure_white));
                    mSecure.setVisibility(View.VISIBLE);
                } else {
                    str = view.getUrl().replace("http://", "http://");
                    if (!mSearchView.hasFocus()) {
                        mSearchView.setText(Html.fromHtml(str), TextView.BufferType.SPANNABLE);
                    }
                    mSecure.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unsecure_white));
                    mSecure.setVisibility(View.VISIBLE);
                }

                if (isLoading) {
                    mStop_rel.setVisibility(View.VISIBLE);
                    mRefresh_rel.setVisibility(View.GONE);
                    mRefresh.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
                    mStop.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
                } else {
                    mStop_rel.setVisibility(View.GONE);
                    mRefresh_rel.setVisibility(View.VISIBLE);
                    mRefresh.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
                    mStop.setColorFilter(ContextCompat.getColor(SimplicityApplication.getContextOfApplication(), R.color.white), PorterDuff.Mode.SRC_IN);
                }


                if (view.getUrl() != null) {
                    onAmpPage(view.getUrl().contains("/amp/"));
                }

                if (mWebView.canGoForward()) {
                    mForward.setAlpha(0.9f);
                } else {
                    mForward.setAlpha(0.2f);
                }


            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isLoading = !(mWebView != null && mWebView.getProgress() == 100);
                if (mWebView.getTitle().equals("No Connection")) {
                    mSearchView.setText(mWebView.getTitle());
                } else {
                    mSearchView.setText(mWebView.getUrl());
                }
                sslCertificate = view.getCertificate();
            }

        });



        mWebChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            filename1 = URLUtil.guessFileName(url, contentDisposition, getMimeType(url));
            urlToGrab = url;
            new FileSize(url).execute();
            if (StaticUtils.isMarshmallow()) {
                if (ActivityCompat.checkSelfPermission(PrivateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrivateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                } else {
                    try {
                        if (UserPreferences.getBoolean("rename", false)) {
                            createDownload();
                        } else {
                            Cardbar.snackBar(getApplicationContext(), getString(R.string.downloading), true).show();
                            new SimplicityDownloader(this).execute(urlToGrab);
                        }
                    } catch (Exception exc) {
                        Cardbar.snackBar(getApplicationContext(), exc.toString(), true).show();


                    }
                }
            }
        });

        mSearchView.setAdapter(new SuggestionsAdapterPrivate(this));
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                StaticUtils.hideKeyboard(mSearchView);
                if(mSearchView.getText().toString().contains("simplicity://flags")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent Intent = new Intent(PrivateActivity.this, ExperimentalActivity.class);
                    startActivity(Intent);
                }else if(mSearchView.getText().toString().contains("simplicity://history")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent history = new Intent(PrivateActivity.this, HistoryActivity.class);
                    startActivity(history);

                }else if(mSearchView.getText().toString().contains("simplicity://bookmarks")){
                    mSearchView.setText(mWebView.getUrl());
                    Intent settingsIntent = new Intent(PrivateActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                }else{
                    loadUrlFromTextBox();
                }
                mSearchView.clearFocus();
                return true;
            }
            return false;
        });
        mSearchView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                StaticUtils.hideKeyboard(mSearchView);
                if(mSearchView.getText().toString().contains("simplicity://flags")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent Intent = new Intent(PrivateActivity.this, ExperimentalActivity.class);
                    startActivity(Intent);
                }else if(mSearchView.getText().toString().contains("simplicity://history")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent history = new Intent(PrivateActivity.this, HistoryActivity.class);
                    startActivity(history);

                }else if(mSearchView.getText().toString().contains("simplicity://bookmarks")){
                    mSearchView.setText(mWebView.getUrl());
                    Intent settingsIntent = new Intent(PrivateActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                }else{
                    loadUrlFromTextBox();
                }
                mSearchView.clearFocus();
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
            StaticUtils.hideKeyboard(mSearchView);
            mSearchView.clearFocus();
            loadUrlFromTextBox();
        });



        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            bCardView.setTranslationY(Math.round(-verticalOffset));
            View view = findViewById(R.id.fake_shadow);
            view.setTranslationY(Math.round(-verticalOffset));
        });

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isKeyboardOpen();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
            mWebView.loadUrl(UrlCleaner, extraHeaders);
        }else{
            mWebView.loadUrl("about:blank");
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        applyAppSettings();

    }



    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        CookieManager.getInstance().flush();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
        applyHomeButton();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }

    }


    @Override
    public void onBackPressed() {
        try {
            if (inCustomView())
                hideCustomView();
            if (mCardView.getVisibility() == View.VISIBLE) {
                hideMenu();
            } else if (mWebView.canGoBack()) {
                goBack();
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception p){
            p.printStackTrace();
        }
    }

    private void goBack() {
        mWebView.goBack();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if (Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
                if(!mSearchView.getText().toString().contains("about:blank"))
                    deleteCookies(mWebView.getUrl());
            } else {
                finish();
                if(!mSearchView.getText().toString().contains("about:blank"))
                    deleteCookies(mWebView.getUrl());
            }
        }catch (NullPointerException z){
            z.printStackTrace();
        }catch (Exception p){
            p.printStackTrace();
        }

    }

    private void loadUrlFromTextBox() {
        String unUrlCleaner = mSearchView.getText().toString();
        if (unUrlCleaner.startsWith("www") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".com") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".gov") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".net") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".org") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".mil") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".edu") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".int") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".ly") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".de") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".uk") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".it") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".jp") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".ru") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".gl") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.endsWith(".me") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else if (unUrlCleaner.contains(".") && !unUrlCleaner.contains(" ") || URLUtil.isValidUrl(unUrlCleaner)) {
            if (!URLUtil.isValidUrl(unUrlCleaner)) unUrlCleaner = URLUtil.guessUrl(unUrlCleaner);
            mWebView.loadUrl(unUrlCleaner, extraHeaders);
        } else {
            mWebView.loadUrl(defaultSearch + unUrlCleaner, extraHeaders);
        }

    }



    private void setColor(int color) {
        color = isIncognito ? ContextCompat.getColor(this, R.color.private_mode) : color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.colorNav(color));
            colorAnimation.setDuration(100);
            colorAnimation.addUpdateListener(animator -> getWindow().setStatusBarColor((int) animator.getAnimatedValue()));
            colorAnimation.start();
        }
        int colorFrom = ContextCompat.getColor(this, !isIncognito ? R.color.private_mode : R.color.no_fav);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable backgroundFrom = mToolbar.getBackground();
            if (backgroundFrom instanceof ColorDrawable)
                colorFrom = ((ColorDrawable) backgroundFrom).getColor();
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
            colorAnimation.setDuration(100);
            colorAnimation.addUpdateListener(animator -> {
                mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                mProgress.setBackgroundColor((int) animator.getAnimatedValue());
                bCardView.setBackgroundColor((int) animator.getAnimatedValue());
                mSearchView.setTextColor(ContextCompat.getColor(this, R.color.white));
                mSecure.setColorFilter(ContextCompat.getColor(this, R.color.white));
                vSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mHomebutton.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mJump.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mOverflow.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mAddress.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mTabs.setColorFilter(ContextCompat.getColor(this, R.color.white));
                mAddress.setBackground(ContextCompat.getDrawable(this, R.drawable.search_bar_dark));
                mRefresh.setColorFilter(ContextCompat.getColor(this, R.color.white));
            });
            colorAnimation.start();
        }

    }

    protected void getBookmarkIcon() {
        try {
            if (bookmarkicon != null && UserPreferences.isStarred(mWebView.getUrl())) {
                bookmarkicon.setColorFilter(ContextCompat.getColor(this, R.color.md_blue_600), PorterDuff.Mode.SRC_IN);
            } else if (bookmarkicon != null) {
                bookmarkicon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
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
                if (keypadHeight > screenHeight * 0.15) {
                    bCardView.setVisibility(View.GONE);
                    View view = findViewById(R.id.fake_shadow);
                    view.setVisibility(View.GONE);
                    if (mPreferences.getBoolean("full_screen", false)) {
                        hideNavBar();
                    }
                } else {
                    bCardView.setVisibility(View.VISIBLE);
                    View view = findViewById(R.id.fake_shadow);
                    view.setVisibility(View.VISIBLE);
                    if (mPreferences.getBoolean("full_screen", false)) {
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



   /* public String getUrlDomainName(String url) {
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
    }*/


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
                        Intent intent = new Intent(PrivateActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.setData(Uri.parse(homepage));
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(PrivateActivity.this, MainActivity.class);
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
        findViewById(R.id.sim_refresh2).setOnClickListener(onClickListener);
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
        findViewById(R.id.sim_over_button).setOnClickListener(onClickListener);
        findViewById(R.id.sim_reader).setOnClickListener(onClickListener);
        findViewById(R.id.voice_button).setOnClickListener(onClickListener);
        findViewById(R.id.sim_close).setOnClickListener(onClickListener);
        findViewById(R.id.sim_download).setOnClickListener(onClickListener);
        findViewById(R.id.sim_downloads).setOnClickListener(onClickListener);
        findViewById(R.id.sim_jump_tabs).setOnClickListener(onClickListener);
        findViewById(R.id.sim_address).setOnClickListener(onClickListener);
        findViewById(R.id.sim_tabs_tabs).setOnClickListener(onClickListener);
        //relative layouts
        findViewById(R.id.home_rel).setOnClickListener(onClickListener);
        findViewById(R.id.home2_rel).setOnClickListener(onClickListener);
        findViewById(R.id.jump_rel).setOnClickListener(onClickListener);
        findViewById(R.id.search_rel).setOnClickListener(onClickListener);
        findViewById(R.id.tabs_rel).setOnClickListener(onClickListener);
        findViewById(R.id.overflow_rel).setOnClickListener(onClickListener);
        findViewById(R.id.sim_home_tabs).setOnClickListener(onClickListener);
        //

    }


    private void showMenu() {
        //mCardView.setVisibility(View.VISIBLE);
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
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(PrivateActivity.this, R.anim.translate_from_top), 0.3f);
        items.setLayoutAnimation(lac);
        items.post(() -> {

        });
        LayoutAnimationController lac2 = new LayoutAnimationController(AnimationUtils.loadAnimation(PrivateActivity.this, R.anim.first_row_animation), 0.3f);
        top.setLayoutAnimation(lac2);
        top.post(() -> {

        });

    }


    private void hideMenu() {
        mCardView.setVisibility(View.GONE);
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
        defaultSearch = "https://duckduckgo.com/?q=";
        defaultProvider = mPreferences.getString("search_suggestions", "");
        homepage = mPreferences.getString("homepage", "");
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(View.GONE);
        }
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
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

        defaultSearch = "https://duckduckgo.com/?q=";
        defaultProvider = mPreferences.getString("search_suggestions", "");
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(View.GONE);
        }
        if (mPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
            mWebSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }

    }




    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else if (urlToGrab != null){
            new SimplicityDownloader(this).execute(urlToGrab);
        }
    }


    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (urlToGrab != null)
                    new SimplicityDownloader(this).execute(urlToGrab);
            } else {
                Cardbar.snackBar(getApplicationContext(), "Permission denied.", true).show();


            }
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
                    loadUrlFromTextBox();
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


    private void createFileName() {
        LayoutInflater inflater = getLayoutInflater();
        Bitmap resizedBitmap = Bitmap.createBitmap(favoriteIcon);
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.layout_shortcut, null);
        shortcutNameEditText = alertLayout.findViewById(R.id.shortcut_name_edittext);
        AlertDialog alertDialog = createExitDialog();
        alertDialog.setTitle(R.string.add_home);
        alertDialog.setView(alertLayout);
        alertDialog.show();
        shortcutNameEditText.setHint(webViewTitle);
        ImageView imageShortcut = alertDialog.findViewById(R.id.fav_imageView);
        if (imageShortcut != null) {
            imageShortcut.setImageBitmap(StaticUtils.getCircleBitmap(resizedBitmap));
        }else{
            Log.d("Null", "");
        }
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.md_blue_600));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);
    }

    private AlertDialog createExitDialog() {
        return new AlertDialog.Builder(PrivateActivity.this)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    intent1.setAction(Intent.ACTION_MAIN);
                    intent1.setData(Uri.parse(mSearchView.getText().toString()));
                    ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(PrivateActivity.this, shortcutNameEditText.getText().toString())
                            .setShortLabel(shortcutNameEditText.getText().toString())
                            .setIcon(IconCompat.createWithBitmap(StaticUtils.createScaledBitmap(StaticUtils.getCroppedBitmap(favoriteIcon), 300, 300)))
                            .setIntent(intent1)
                            .build();
                    ShortcutManagerCompat.requestPinShortcut(PrivateActivity.this, pinShortcutInfo, null);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    // nothing to do here
                })
                .setCancelable(true)
                .create();
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
            Cardbar.snackBar(getApplicationContext(), getString(R.string.error), true).show();

        }
    }


    @SuppressLint("SetTextI18n")
    private void createDownload() {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.layout_download, null);
        shortcutNameEditText = alertLayout.findViewById(R.id.file_name_edit_text);
        EditText path = alertLayout.findViewById(R.id.file_path_edit_text);
        TextView download_size = alertLayout.findViewById(R.id.down_text);
        TextView download_warn = alertLayout.findViewById(R.id.down_text_warn);
        AlertDialog alertDialog = createDialog();
        alertDialog.setView(alertLayout);
        alertDialog.setCancelable(false);
        alertDialog.show();
        if (UserPreferences.isDangerousFileExtension(filename1)) {
            download_warn.setVisibility(View.VISIBLE);
            download_warn.setText(String.format(getString(R.string.download_warning), filename1));
        }
        shortcutNameEditText.setHint(filename1);
        path.setText(Environment.DIRECTORY_DOWNLOADS);
        download_size.setText(getResources().getString(R.string.download_file) + " " +file_size_main);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.md_blue_600));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);
    }

    private AlertDialog createDialog() {
        return new AlertDialog.Builder(PrivateActivity.this)
                .setPositiveButton(getString(R.string.download), (dialog, which) -> {
                    if (!shortcutNameEditText.getText().toString().isEmpty()) {
                        UserPreferences.putString("file_name_new", shortcutNameEditText.toString());
                    } else {
                        UserPreferences.putString("file_name_new", "");
                    }
                    requestStoragePermission();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .setCancelable(true)
                .create();
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
            Cardbar.snackBar(getApplicationContext(), "Failed to print", true).show();

        }

    }


    public void viewSslCertificate(View view) {
        DialogFragment viewSslCertificateDialogFragment = new SimplicitySslCertificate();
        viewSslCertificateDialogFragment.show(getSupportFragmentManager(), getString(R.string.view_ssl_certificate));
    }


    private class MyWebChromeClient extends WebChromeClient {

        /*@Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onCloseWindow(WebView window) {

            super.onCloseWindow(window);
        }*/


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }

        @Override
        public boolean onJsAlert(WebView view, final String url, final String message, final JsResult result) {
            if(!isDestroyed()) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PrivateActivity.this);
                builder1.setTitle(R.string.app_name);
                builder1.setMessage(message);
                builder1.setCancelable(true);
                builder1.setPositiveButton(R.string.ok, (dialog, id) -> {
                    result.confirm();
                    dialog.dismiss();
                });
                builder1.setNegativeButton(R.string.cancel, (dialog, id) -> {
                    result.cancel();
                    dialog.dismiss();
                });
                androidx.appcompat.app.AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            return true;

        }




        @Override
        public boolean onJsConfirm(WebView view, final String url, final String message, final JsResult result) {
            if (!isDestroyed()) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PrivateActivity.this);
                builder1.setTitle(R.string.app_name);
                builder1.setMessage(message);
                builder1.setCancelable(true);
                builder1.setPositiveButton(R.string.ok, (dialog, id) -> {
                    result.confirm();
                    dialog.dismiss();
                });
                builder1.setNegativeButton(R.string.cancel, (dialog, id) -> {
                    result.cancel();
                    dialog.dismiss();
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            return true;

        }

        @Override
        public boolean onJsPrompt(WebView view, String url, final String message, final String defaultValue, final JsPromptResult result) {
            if (!isDestroyed()) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PrivateActivity.this);
                builder1.setTitle(message);
                builder1.setCancelable(true);
                builder1.setPositiveButton(R.string.ok, (dialog, id) -> {
                    result.confirm();
                    dialog.dismiss();
                });
                builder1.setNegativeButton(R.string.cancel, (dialog, id) -> {
                    result.cancel();
                    dialog.dismiss();
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
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
            super.onProgressChanged(view, progress);
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }



        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            favoriteIcon = icon;
            if (icon != null && StaticUtils.isLollipop()) {
                setColor(Palette.from(icon).generate().getVibrantColor(Palette.from(icon).generate().getMutedColor(ContextCompat.getColor(PrivateActivity.this, R.color.no_fav))));
            }else{
                setColor(ContextCompat.getColor(PrivateActivity.this, R.color.no_fav));

            }
            try {
                int color = StaticUtils.fetchColorPrimary(PrivateActivity.this);
                ActivityManager.TaskDescription description;
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    //noinspection deprecation
                    description = new ActivityManager.TaskDescription("Simplicity - " + mWebView.getTitle(), favoriteIcon, color);
                } else {
                    //noinspection deprecation
                    description = new ActivityManager.TaskDescription("Simplicity - " + mWebView.getTitle(), favoriteIcon, color);
                }
                setTaskDescription(description);
            }catch (Exception i){
                i.printStackTrace();
            }
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
            touchIcon = url;
            Log.d("Touch icon", url);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            try {
                webViewTitle = title;
                mSearchView.setText(mWebView.getUrl());
                mPreferences.edit().putString("last_page_reminder", mWebView.getUrl()).apply();
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onReceivedTitle(view, title);

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
            contentSelectionIntent.setType("*/*");
            contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "*/*"});

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose file");
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

        @SuppressLint("RestrictedApi")
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
            Intent intent = new Intent(PrivateActivity.this, ReadingActivity.class);
            intent.putExtra("text", text);
            intent.putExtra("title",title);
            startActivity(intent);
        }
    }



    private String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }


    private void onAmpPage(boolean isAmped) {

    }




    public static Activity getPriavteActivity() {
        return priavteActivity;
    }

    private void removeCookie(){
        CookieManager mCookieManager = CookieManager.getInstance();
        @SuppressWarnings("deprecation")
        CookieSyncManager mCookieSyncManager = CookieSyncManager.createInstance(this);
        clearCookieByUrl(mWebView.getUrl(), mCookieManager, mCookieSyncManager);
    }


    @SuppressWarnings("deprecation")
    public static void clearCookieByUrl(String url, CookieManager pCookieManager, CookieSyncManager pCookieSyncManager) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        clearCookieByUrlInternal(url, pCookieManager, pCookieSyncManager);
        clearCookieByUrlInternal("http://." + host, pCookieManager, pCookieSyncManager);
        clearCookieByUrlInternal("https://." + host, pCookieManager, pCookieSyncManager);
    }

    @SuppressWarnings("deprecation")
    private static void clearCookieByUrlInternal(String url, CookieManager pCookieManager, CookieSyncManager pCookieSyncManager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String cookieString = pCookieManager.getCookie(url);
        Vector<String> cookie = getCookieNamesByUrl(cookieString);
        if (cookie == null || cookie.isEmpty()) {
            return;
        }
        int len = cookie.size();
        for (int i = 0; i < len; i++) {
            pCookieManager.setCookie(url, cookie.get(i) + "=-1");
        }
        pCookieSyncManager.sync();
    }

    private static Vector<String> getCookieNamesByUrl(String cookie) {
        if (TextUtils.isEmpty(cookie)) {
            return null;
        }
        String[] cookieField = cookie.split(";");
        int len = cookieField.length;
        for (int i = 0; i < len; i++) {
            cookieField[i] = cookieField[i].trim();
        }
        Vector<String> allCookieField = new Vector<>();
        for (String aCookieField : cookieField) {
            if (TextUtils.isEmpty(aCookieField)) {
                continue;
            }
            if (!aCookieField.contains("=")) {
                continue;
            }
            String[] singleCookieField = aCookieField.split("=");
            allCookieField.add(singleCookieField[0]);
        }
        if (allCookieField.isEmpty()) {
            return null;
        }
        return allCookieField;
    }


    public static void deleteCookies(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        try {
            String domainName = StaticUtils.getDomainName(url);
            String cookiesString = cookieManager.getCookie(url);
            String[] cookies = cookiesString.split("; ");
            for (String cookie : cookies) {
                if (Strings.isNullOrEmpty(cookie))
                    continue;
                int equalCharIndex = cookie.indexOf('=');
                if (equalCharIndex == -1)
                    continue;
                String cookieString = cookie.substring(0, equalCharIndex) + '='
                        + "; Domain=" + domainName;
                cookieManager.setCookie(url, cookieString);
            }
        } catch (URISyntaxException e) {
            Log.e("TAG", "Message", e);
        }
    }

}
