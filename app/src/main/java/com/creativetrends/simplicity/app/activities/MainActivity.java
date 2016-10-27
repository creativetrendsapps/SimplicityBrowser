package com.creativetrends.simplicity.app.activities;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.creativetrends.simplicity.app.behavior.BottomBehavior;
import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.ui.BottomNavigationViewHelper;
import com.creativetrends.simplicity.app.ui.SimplicityOmniBox;
import com.creativetrends.simplicity.app.ui.WebViewScroll;
import com.creativetrends.simplicity.app.utils.AdBlock;
import com.creativetrends.simplicity.app.utils.AppTheme;
import com.creativetrends.simplicity.app.utils.Bookmark;
import com.creativetrends.simplicity.app.utils.Connectivity;
import com.creativetrends.simplicity.app.utils.Downloader;
import com.creativetrends.simplicity.app.utils.SimplicityAdapter;
import com.creativetrends.simplicity.app.utils.OnlineStatus;
import com.creativetrends.simplicity.app.utils.PreferencesUtility;
import com.creativetrends.simplicity.app.utils.StaticUtils;
import com.creativetrends.simplicity.app.webview.SimplicityChromeClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements ShortcutActivity.CreateHomeScreenSchortcutListener, SimplicityAdapter.onBookmarkSelected {
    AppBarLayout appbar;
    private boolean refreshed;
    public int scrollPosition = 0;
    public static final String PREFS_NAME = "SimplicityPrefs";
    public static final String PREFS_SEARCH_HISTORY = "SimplicityHistory";
    private Set<String> history;
    NavigationView bookmarkFavs;
    public static Bitmap favoriteIcon;
    public static WebViewScroll webView;
    private RelativeLayout header;
    private static final int REQUEST_SELECT_FILE = 234;
    private ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> uploadMessagePreLollipop;
    private static final int FILE_CHOOSER_RESULT_CODE = 41285;
    private static final int STORAGE_PERMISSION_CODE = 2284;
    private static final int REQUEST_STORAGE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2562617;
    private static final int ID_CONTEXT_MENU_SHARE_IMAGE = 2562618;
    private static final int ID_CONTEXT_MENU_COPY_IMAGE = 2562619;
    private String urlToGrab;
    private static String appDirectoryName;
    public static Menu mainMenu;
    public static CookieManager cookieManager;
    private SharedPreferences preferences;
    public static boolean javaScriptEnabled;
    public static boolean firstPartyCookiesEnabled;
    public static boolean thirdPartyCookiesEnabled;
    public static String defaultSearch;
    @SuppressLint("StaticFieldLeak")
    public static DrawerLayout bookmarksDrawer;
    private ImageView secure;
    private MenuItem stopPage;
    private MenuItem refreshPage;
    public static String homepage;
    SwipeRefreshLayout swipeToRefresh;
    private Toolbar toolbar;
    private String UrlCleaner;
    private SimplicityOmniBox omniBox;
    private boolean isPrivate;
    private boolean computerMode;
    private FrameLayout addressBarFrameLayout;
    private static long back_pressed;
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerBookmarks;
    private ArrayList<Bookmark> listBookmarks = new ArrayList<>();
    private SimplicityAdapter adapterBookmarks;


    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        history = settings.getStringSet(PREFS_SEARCH_HISTORY, new HashSet<String>());
        setAutoCompleteSource();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        appDirectoryName = getString(R.string.app_name).replace(" ", " ");
        toolbar = (Toolbar) findViewById(R.id.appBar);
        secure = (ImageView) findViewById(R.id.favoriteIcon);
        header = (RelativeLayout) findViewById(R.id.bookmarks_header);
        bookmarksDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        bookmarkFavs = (NavigationView) findViewById(R.id.simplicity_bookmarks);
        addressBarFrameLayout = (FrameLayout) findViewById(R.id.addressBarFrameLayout);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        listBookmarks = PreferencesUtility.getBookmarks();
        recyclerBookmarks = (RecyclerView) findViewById(R.id.recycler_bookmarks);
        recyclerBookmarks.setLayoutManager(new LinearLayoutManager(this));
        adapterBookmarks = new SimplicityAdapter(this, listBookmarks, this);
        recyclerBookmarks.setAdapter(adapterBookmarks);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        assert swipeToRefresh != null;
        swipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeToRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.white));
        swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.md_blue_600));
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        if (preferences.getBoolean("no_ads", false)) {
            AdBlock.init(this);
        }

        webView = (WebViewScroll) findViewById(R.id.mainWebView);
        assert webView != null;
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //noinspection deprecation
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        if (OnlineStatus.getInstance(this).isOnline()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webView.getSettings().setDatabaseEnabled(true);
        //noinspection deprecation
        webView.getSettings().setDatabasePath(this.getFilesDir().getPath() + getPackageName() + "/databases/");
        webView.getSettings().setDomStorageEnabled(true);
        //noinspection deprecation
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setOnScrollChangedCallback(new WebViewScroll.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                scrollPosition = t;
            }
        });
        try {
            AppTheme.fontSize(webView, this);
        } catch (Exception ignored) {

        }


        omniBox = (SimplicityOmniBox) findViewById(R.id.omniBox);
        omniBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        loadUrlFromTextBox();
                        omniBox.setCursorVisible(false);
                        webView.hasFocus();
                        history.add(webView.getUrl());
                        addSearchInput(omniBox.getText().toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        omniBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omniBox.setCursorVisible(true);
                omniBox.selectAll();
            }
        });


        if (preferences.getBoolean("show_home", false)) {
            toolbar.setLogo(R.drawable.home);
        } else {
            toolbar.setLogo(null);
        }

        if (preferences.getBoolean("show_home", false)) {
            View view = toolbar.getChildAt(2);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.loadUrl(homepage);
                    webView.isFocused();
                }
            });

        }
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
                switch (item.getItemId()) {
                    case R.id.action_back:
                        if(webView.canGoBack()){
                            webView.goBack();
                        }
                        break;
                    case R.id.action_forward:
                        if(webView.canGoForward()){
                            webView.goForward();
                        }
                        break;
                    case R.id.action_bookmarks:
                        bookmarksDrawer.openDrawer(GravityCompat.END);
                        webView.isFocused();
                        break;

                    case R.id.action_new:
                        if(preferences.getBoolean("merge_windows", false)) {
                            Intent windowIntent = new Intent(MainActivity.this, NewWindow.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                windowIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            }
                            startActivity(windowIntent);
                        }else{
                            Intent windowIntent = new Intent(MainActivity.this, NewWindow.class);
                            startActivity(windowIntent);
                        }
                        break;

                    case R.id.action_jump:
                        scrollToTop();
                        break;
                }
                return false;
            }
        });


        ImageView add_button = (ImageView) findViewById(R.id.add_bookmark);
        add_button.setClickable(true);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(webView.getTitle());
                bookmark.setUrl(webView.getUrl());
                adapterBookmarks.addItem(bookmark);
                bookmarksDrawer.closeDrawers();
                Toast.makeText(getBaseContext(), "Added: " + webView.getTitle().replace("", "") + " to bookmarks.", Toast.LENGTH_LONG).show();
            }
        });


        webView.setWebViewClient(new WebClient());
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
                final String filename1 = URLUtil.guessFileName(url, contentDisposition, mimeType);

                Snackbar snackbar = Snackbar.make(webView, "Download " + filename1 + "?", Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.parseColor("#1e88e5"));
                snackbar.setAction("DOWNLOAD", new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {

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
                                    dm.enqueue(request);

                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("*/*");

                                    Toast.makeText(MainActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
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
                                dm.enqueue(request);

                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("*/*");

                                Toast.makeText(MainActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
                            } catch (Exception exc) {
                                Toast.makeText(MainActivity.this, exc.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                snackbar.show();
            }
        });

        SimplicityChromeClient webChromeClient = new SimplicityChromeClient(this) {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    if (title != null && title.contains("about:blank")) {
                        omniBox.setText(R.string.no_connection);
                    }
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                try {
                    favoriteIcon = icon;
                    Palette.from(icon).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            setColor(palette.getVibrantColor(ContextCompat.getColor(MainActivity.this, R.color.md_blue_600)));
                            refreshPage.setIcon(R.drawable.ic_refresh_page_white);
                            stopPage.setIcon(R.drawable.ic_stop_loading_white);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setTaskDescription(new ActivityManager.TaskDescription("Simplicity", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), palette.getVibrantColor(ContextCompat.getColor(MainActivity.this, R.color.md_blue_600))));

                            }

                        }
                    });


                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        webView.setWebChromeClient(webChromeClient);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (preferences.getBoolean("scroll_toolbar", false)) {
                    webView.setCanScrollVertically((appBarLayout.getHeight() - appBarLayout.getBottom()) != 0);
                    BottomBehavior.behaviorTranslationEnabled = true;
                } else {
                    BottomBehavior.behaviorTranslationEnabled = false;

                }
            }
        });


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences savedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        javaScriptEnabled = savedPreferences.getBoolean("javascript_enabled", true);
        webView.getSettings().setJavaScriptEnabled(javaScriptEnabled);
        cookieManager = CookieManager.getInstance();
        firstPartyCookiesEnabled = savedPreferences.getBoolean("first_party_cookies_enabled", true);
        cookieManager.setAcceptCookie(firstPartyCookiesEnabled);
        if (Build.VERSION.SDK_INT >= 21) {
            thirdPartyCookiesEnabled = savedPreferences.getBoolean("third_party_cookies_enabled", true);
            cookieManager.setAcceptThirdPartyCookies(webView, thirdPartyCookiesEnabled);
        }

        if (preferences.getBoolean("enable_location", false)) {
            webView.getSettings().setGeolocationEnabled(true);
            //noinspection deprecation
            webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            webView.getSettings().setGeolocationEnabled(false);
        }
        if (OnlineStatus.getInstance(this).isOnline()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        defaultSearch = savedPreferences.getString("search_engine", "https://www.google.com/search?q=");
        homepage = savedPreferences.getString("homepage", "");


        final Intent intent = getIntent();

        if (intent.getData() != null) {
            final Uri intentUriData = intent.getData();
            UrlCleaner = intentUriData.toString();
        }


        if (UrlCleaner == null) {
            UrlCleaner = homepage;
        }
        webView.loadUrl(UrlCleaner);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessagePreLollipop == null)
                return;
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            uploadMessagePreLollipop.onReceiveValue(result);
            uploadMessagePreLollipop = null;
        } else {
            Snackbar.make(webView, R.string.error, Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (intent.getData() != null) {
            final Uri intentUriData = intent.getData();
            UrlCleaner = intentUriData.toString();
        }
        webView.loadUrl(UrlCleaner);
        webView.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        MenuItem home_button = menu.findItem(R.id.simplicityHome);
        refreshPage = menu.findItem(R.id.startLoading);
        stopPage = menu.findItem(R.id.stopLoading);
        if (preferences.getBoolean("show_home", false)) {
            home_button.setVisible(false);
        } else {
            home_button.setVisible(true);
        }


        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {


            case R.id.stopLoading:
                webView.stopLoading();
                break;

            case R.id.startLoading:
                webView.reload();
                break;

            case R.id.actionClose:
                if (preferences.getBoolean("clear_data", false)) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        cookieManager.removeAllCookies(null);
                    } else {
                        //noinspection deprecation
                        cookieManager.removeAllCookie();
                    }
                    WebStorage domStorage = WebStorage.getInstance();
                    domStorage.deleteAllData();
                    WebViewDatabase formData = WebViewDatabase.getInstance(this);
                    formData.clearFormData();
                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.destroy();
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
                break;


            case R.id.request_desktop:
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                if (computerMode) {
                    webView.getSettings().setUserAgentString("");
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.reload();
                    computerMode = false;

                } else {
                    webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.41 Safari/537.36");
                    webView.getSettings().setLoadWithOverviewMode(false);
                    webView.reload();
                    computerMode = true;

                }
                break;

            case R.id.settingsAction:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;


            case R.id.privateM:
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                isPrivate = !isPrivate;
                WebSettings webSettings = webView.getSettings();
                CookieManager.getInstance().setAcceptCookie(isPrivate);
                webSettings.setAppCacheEnabled(!isPrivate);
                webView.clearHistory();
                webView.clearCache(isPrivate);
                webView.clearFormData();
                webView.getSettings().setSavePassword(!isPrivate);
                webView.getSettings().setSaveFormData(!isPrivate);
                webView.isPrivateBrowsingEnabled();
                webView.reload();
                if (!isPrivate) {
                    setColor(ContextCompat.getColor(MainActivity.this, R.color.md_blue_600));
                    CookieManager.getInstance().setAcceptCookie(!isPrivate);
                }
                break;

            case R.id.simplicityHome:
                webView.loadUrl(homepage);
                break;

            case R.id.findM:
                webView.showFindDialog(null, true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;


            case R.id.share:
                String shareBody = webView.getUrl();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, webView.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share link"));
                break;

            case R.id.addToHomescreen:
                AppCompatDialogFragment shortcutDialog = new ShortcutActivity();
                shortcutDialog.show(getSupportFragmentManager(), "createShortcut");
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onCreateHomeScreenShortcutCancel(DialogFragment dialog) {

    }

    @Override
    public void onCreateHomeScreenShortcutCreate(DialogFragment dialog) {
        EditText shortcutName = (EditText) dialog.getDialog().findViewById(R.id.shortcutNameEditText);
        shortcutName.setText(webView.getTitle());
        Intent bookmarkShortcut = new Intent();
        bookmarkShortcut.setAction(Intent.ACTION_VIEW);
        bookmarkShortcut.setData(Uri.parse(UrlCleaner));
        Intent shortcutMaker = new Intent();
        shortcutMaker.putExtra("duplicate", false);
        shortcutMaker.putExtra("android.intent.extra.shortcut.INTENT", bookmarkShortcut);
        shortcutMaker.putExtra("android.intent.extra.shortcut.NAME", shortcutName.getText().toString());
        shortcutMaker.putExtra("android.intent.extra.shortcut.ICON", favoriteIcon);
        shortcutMaker.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(shortcutMaker);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (preferences.getBoolean("confirm_close", false)) {
                if (back_pressed + 2000 > System.currentTimeMillis())
                    super.onBackPressed();
                else
                    Toast.makeText(getBaseContext(), "Press back again to exit " + getResources().getString(R.string.app_name) + "!", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
            if (preferences.getBoolean("clear_data", false)) {
                if (Build.VERSION.SDK_INT >= 21) {
                    cookieManager.removeAllCookies(null);
                } else {
                    //noinspection deprecation
                    cookieManager.removeAllCookie();
                }
                WebStorage domStorage = WebStorage.getInstance();
                domStorage.deleteAllData();
                WebViewDatabase formData = WebViewDatabase.getInstance(this);
                formData.clearFormData();
                webView.clearCache(true);
                webView.clearHistory();
                webView.destroy();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
        }
        PreferencesUtility.saveBookmarks(adapterBookmarks.getListBookmarks());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerForContextMenu(webView);
        webView.onResume();
        webView.requestFocus();
        listBookmarks = PreferencesUtility.getBookmarks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferences.getBoolean("clear_data", false)) {
            if (Build.VERSION.SDK_INT >= 21) {
                cookieManager.removeAllCookies(null);
            } else {
                //noinspection deprecation
                cookieManager.removeAllCookie();
            }
            WebStorage domStorage = WebStorage.getInstance();
            domStorage.deleteAllData();
            WebViewDatabase formData = WebViewDatabase.getInstance(this);
            formData.clearFormData();
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        } else {
            webView.destroy();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePrefs();
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            Log.e(TAG, "No storage permission at the moment. Requesting...");
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            Log.e(TAG, "We already have storage permission. Yay!");
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        WebView.HitTestResult result = webView.getHitTestResult();
        if (result != null) {
            int type = result.getType();

            if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                showLongPressedImageMenu(menu, result.getExtra());
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_CONTEXT_MENU_SAVE_IMAGE:
                requestStoragePermission();
                break;
            case ID_CONTEXT_MENU_SHARE_IMAGE:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, urlToGrab);
                startActivity(Intent.createChooser(share, "Share via"));
                break;
            case ID_CONTEXT_MENU_COPY_IMAGE:
                ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(this.getContentResolver(), "URI", Uri.parse(urlToGrab));
                clipboard.setPrimaryClip(clip);
                Snackbar.make(webView, "Copied to clipboard", Snackbar.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }


    private void showLongPressedImageMenu(ContextMenu menu, String imageUrl) {
        urlToGrab = imageUrl;
        menu.setHeaderTitle(webView.getUrl());
        menu.add(0, ID_CONTEXT_MENU_SAVE_IMAGE, 0, "Save Image");
        menu.add(0, ID_CONTEXT_MENU_SHARE_IMAGE, 1, "Share Image");
        menu.add(0, ID_CONTEXT_MENU_COPY_IMAGE, 2, "Copy Image Url");
    }

    @SuppressWarnings("Range")
    private void saveImageToDisk(String imageUrl) {
        if (!Downloader.resolve(this)) {
            urlToGrab = null;
            return;
        }

        try {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);

            if (!imageStorageDir.exists()) {

                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }


            String imgex = ".jpg";

            if (imageUrl.contains(".gif"))
                imgex = ".gif";
            else if (imageUrl.contains(".png"))
                imgex = ".png";

            String file = "IMG_" + System.currentTimeMillis() + imgex;
            DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(imageUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, file)
                    .setTitle(file).setDescription("")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            dm.enqueue(request);

            Snackbar.make(webView, "Downloading", Snackbar.LENGTH_LONG).show();
        } catch (IllegalStateException ex) {
            Snackbar.make(webView, "Permission denied", Snackbar.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Snackbar.make(webView, "Something went wrong", Snackbar.LENGTH_SHORT).show();
        } finally {
            urlToGrab = null;
        }
    }


    private void loadUrlFromTextBox() throws UnsupportedEncodingException {
        String unUrlCleaner = omniBox.getText().toString();
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


            if (javaScriptEnabled) {
                UrlCleaner = defaultSearch + encodedUrlString;
            } else {
                UrlCleaner = "https://www.google.com/search?q=" + encodedUrlString;
            }
        }

        webView.loadUrl(UrlCleaner);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(webView.getWindowToken(), 0);
    }

    private void setColor(int color) {
        color = isPrivate ? ContextCompat.getColor(this, R.color.md_grey_900) : color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.darkColor(color));
            colorAnimation.setDuration(50);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor((int) animator.getAnimatedValue());
                    }
                }
            });
            colorAnimation.start();
        }

        int colorFrom = ContextCompat.getColor(this, !isPrivate ? R.color.pcPD : R.color.md_blue_600);
        Drawable backgroundFrom = toolbar.getBackground();
        if (backgroundFrom instanceof ColorDrawable)
            colorFrom = ((ColorDrawable) backgroundFrom).getColor();

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(50);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((int) animator.getAnimatedValue());
                swipeToRefresh.setProgressBackgroundColorSchemeColor((int) animator.getAnimatedValue());
                header = (RelativeLayout) findViewById(R.id.bookmarks_header);
                header.setBackgroundColor((int) animator.getAnimatedValue());
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setBackgroundColor((int) animator.getAnimatedValue());
                if (preferences.getBoolean("show_home", false)) {
                    toolbar.setLogo(R.drawable.home_white);
                }
                toolbar.setOverflowIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.overflow_white));

            }
        });
        colorAnimation.start();


        //noinspection deprecation
        bookmarksDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    private void setAutoCompleteSource() {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.omniBox);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history.toArray(new String[history.size()]));

        if (textView != null) {
            textView.setAdapter(adapter);

        }
    }

    private void addSearchInput(String input) {
        if (!history.contains(input)) {
            history.add(input);
            setAutoCompleteSource();
        }
    }

    private void savePrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(PREFS_SEARCH_HISTORY, history);
        editor.apply();


    }





    public class WebClient extends WebViewClient {
        private Map<String, Boolean> loadedUrls = new HashMap<>();

        @SuppressWarnings("deprecation")
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (preferences.getBoolean("no_ads", false)) {
                boolean ad;
                if (!loadedUrls.containsKey(url)) {
                    ad = AdBlock.isAd(url);
                    loadedUrls.put(url, ad);
                } else {
                    ad = loadedUrls.get(url);
                }
                return ad ? AdBlock.createEmptyResource() :
                        super.shouldInterceptRequest(view, url);


            }
            return super.shouldInterceptRequest(view, url);

        }


        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if(preferences.getBoolean("no_track", false)){
                    HashMap<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("DNT", "1");
                    view.loadUrl(url, extraHeaders);
                    return true;
                }
                if ((url.contains("market://")
                        || url.contains("mailto:")
                        || url.contains("play.google")
                        || url.contains("tel:"))) {
                    view.getContext().startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if ((url.contains("http://") || url.contains("https://"))) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    e.printStackTrace();
                }

                return true;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                return true;
            }
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                swipeToRefresh.setRefreshing(true);
                omniBox.setText(url);
                stopPage.setVisible(true);
                if (refreshPage.isVisible()) {
                    refreshPage.setVisible(false);
                }
                if ((url.contains("https://"))) {
                    secure.setVisibility(View.VISIBLE);
                } else {
                    secure.setVisibility(View.GONE);
                }

            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (Connectivity.isConnected(getApplicationContext()) && !refreshed) {
                webView.loadUrl(failingUrl);
                refreshed = true;
            } else {
                webView.loadUrl("file:///android_asset/error.html");
                omniBox.setText(R.string.app_name);
                setColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                Snackbar snackbar = Snackbar.make(webView, description, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.refresh, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (webView.canGoBack()) {
                            webView.stopLoading();
                            webView.goBack();
                        }
                    }
                });
                snackbar.show();

            }
        }


        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
            onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                swipeToRefresh.setRefreshing(false);
                listBookmarks = PreferencesUtility.getBookmarks();
                stopPage.setVisible(false);
                refreshPage.setVisible(true);
                omniBox.setText(webView.getUrl());
                omniBox.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                addressBarFrameLayout.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.url_bar_border));

                if (url.contains("file:///")) {
                    omniBox.setText(R.string.app_name);
                }


            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void scrollToTop() {
        if (scrollPosition > 10) {
            scrollToTop(webView);
        }
    }


    public static void scrollToTop(WebView webView) {
        ObjectAnimator anim = ObjectAnimator.ofInt(webView, "scrollY", webView.getScrollY(), 0);
        anim.setDuration(500);
        anim.start();
    }

    @Override
    public void loadBookmark(String title, String url) {
        loadPage(url);
    }

    public void loadPage(String htmlLink) {
        webView.loadUrl(htmlLink);

    }


}
