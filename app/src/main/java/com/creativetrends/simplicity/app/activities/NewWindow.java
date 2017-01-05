package com.creativetrends.simplicity.app.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akiniyalocts.minor.MinorLayout;
import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.behavior.BottomBehavior;
import com.creativetrends.simplicity.app.lock.SmartPassUnlock;
import com.creativetrends.simplicity.app.ui.WebViewScroll;
import com.creativetrends.simplicity.app.utils.AdBlock;
import com.creativetrends.simplicity.app.utils.AppTheme;
import com.creativetrends.simplicity.app.utils.Bookmark;
import com.creativetrends.simplicity.app.utils.Connectivity;
import com.creativetrends.simplicity.app.utils.Downloader;
import com.creativetrends.simplicity.app.utils.OnlineStatus;
import com.creativetrends.simplicity.app.utils.PreferencesUtility;
import com.creativetrends.simplicity.app.utils.SimplicityAdapter;
import com.creativetrends.simplicity.app.utils.StaticUtils;
import com.creativetrends.simplicity.app.webview.SimplicityChromeClient;
import com.google.firebase.crash.FirebaseCrash;

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


public class NewWindow extends AppCompatActivity implements ShortcutActivity.CreateHomeScreenSchortcutListener, SimplicityAdapter.onBookmarkSelected {
    AppBarLayout appbar;
    private boolean refreshed;
    public int scrollPosition = 0;
    public static final String PREFS_NAME = "PingBusPrefs";
    public static final String PREFS_SEARCH_HISTORY = "SearchHistory";
    SharedPreferences settings;
    public static final String PREFS_JELLY_BEAN_WARNING = "BetaNotification";
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
    private static final String TAG = NewWindow.class.getSimpleName();
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2562617;
    private static final int ID_CONTEXT_MENU_SHARE_IMAGE = 2562618;
    private static final int ID_CONTEXT_MENU_COPY_IMAGE = 2562619;
    private String urlToGrab;
    private static String appDirectoryName;
    public static CookieManager cookieManager;
    private SharedPreferences preferences;
    public static boolean javaScriptEnabled;
    public static boolean firstPartyCookiesEnabled;
    public static boolean thirdPartyCookiesEnabled;
    public static String defaultSearch;
    @SuppressLint("StaticFieldLeak")
    public static DrawerLayout bookmarksDrawer;
    public static String homepage;
    SwipeRefreshLayout swipeToRefresh;
    private Toolbar toolbar;
    private String UrlCleaner;
    private boolean isPrivate;
    private boolean computerMode;
    private static long back_pressed;
    @SuppressLint("StaticFieldLeak")
    RecyclerView recyclerBookmarks;
    private ArrayList<Bookmark> listBookmarks = new ArrayList<>();
    private SimplicityAdapter adapterBookmarks;
    private AutoCompleteTextView omniBox;
    ImageView home, refresh, stop, secure, add_button, downloads;
    private ProgressBar progressBar;
    DownloadManager mgr=null;
    long lastDownload=-1L;
    @SuppressLint("StaticFieldLeak")
    static CardView sim;
    private ScrollView menuScroll;
    private FrameLayout menuHolder;
    TextView new_window, pri_mode, books, find, share, homescreen, desktop, set, exit, brand;
    AppCompatCheckBox pri, desk;
    private static MinorLayout bottomtabs;



    @Override
    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        forClicks();
        isKeyBoardShowing();
        settings = getSharedPreferences(PREFS_NAME, 0);
        history = settings.getStringSet(PREFS_SEARCH_HISTORY, new HashSet<String>());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        appDirectoryName = getString(R.string.app_name).replace(" ", " ");
        toolbar = (Toolbar) findViewById(R.id.appBar);
        header = (RelativeLayout) findViewById(R.id.bookmarks_header);
        bookmarksDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        bookmarkFavs = (NavigationView) findViewById(R.id.simplicity_bookmarks);
        listBookmarks = PreferencesUtility.getBookmarks();
        recyclerBookmarks = (RecyclerView) findViewById(R.id.recycler_bookmarks);
        recyclerBookmarks.setLayoutManager(new LinearLayoutManager(this));
        adapterBookmarks = new SimplicityAdapter(this, listBookmarks, this);
        recyclerBookmarks.setAdapter(adapterBookmarks);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        home = (ImageView) findViewById(R.id.home_button);
        refresh = (ImageView) findViewById(R.id.refresh_page);
        stop = (ImageView) findViewById(R.id.stop_loading);
        secure = (ImageView) findViewById(R.id.favoriteIcon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        add_button = (ImageView) findViewById(R.id.add_bookmark);
        mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        sim = (CardView) findViewById(R.id.sim_menu);
        new_window = (TextView) findViewById(R.id.sim_new_window);
        pri_mode = (TextView) findViewById(R.id.sim_private_mode);
        books = (TextView) findViewById(R.id.sim_books);
        find = (TextView) findViewById(R.id.sim_find);
        share = (TextView) findViewById(R.id.sim_share);
        homescreen = (TextView) findViewById(R.id.sim_home_screen);
        desktop = (TextView) findViewById(R.id.sim_desktop);
        brand = (TextView) findViewById(R.id.textviewBrand);
        set = (TextView) findViewById(R.id.sim_settings);
        exit = (TextView) findViewById(R.id.sim_exit);
        downloads = (ImageView) findViewById(R.id.sim_downloads);
        pri = (AppCompatCheckBox) findViewById(R.id.sim_private_check);
        desk = (AppCompatCheckBox) findViewById(R.id.sim_desktop_check);
        bottomtabs = (MinorLayout) findViewById(R.id.bottom_tabs);
        setSupportActionBar(toolbar);
        set.setVisibility(View.GONE);
        exit.setText("Close Window");
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            desk.setVisibility(View.GONE);
            desktop.setVisibility(View.GONE);
        }
        //dialog();


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

        final SharedPreferences simplicity = getSharedPreferences(PREFS_JELLY_BEAN_WARNING, 0);
        boolean whats_new = simplicity.getBoolean("whats_new_239", false);
        try {
            if (!whats_new) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar snackbar = Snackbar.make(webView, getResources().getString(R.string.app_name) + " " + PreferencesUtility.getAppVersionName(getApplicationContext()), Snackbar.LENGTH_INDEFINITE);
                        //noinspection deprecation
                        snackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                SharedPreferences.Editor editor = simplicity.edit();
                                editor.putBoolean("whats_new_239", true);
                                editor.apply();
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                SharedPreferences.Editor editor = simplicity.edit();
                                editor.putBoolean("whats_new_239", true);
                                editor.apply();

                            }
                        });
                        snackbar.setAction("What's New", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                newDialog();
                            }
                        });
                        snackbar.show();
                    }
                }, 6000);
            }
        } catch (Exception ignored) {
            FirebaseCrash.report(new Exception(ignored));

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
            FirebaseCrash.report(new Exception(ignored));

        }

        omniBox = (AutoCompleteTextView) findViewById(R.id.omniBox);
        if (preferences.getBoolean("show_history", false)) {
            setAutoCompleteSource();
        } else {
            omniBox.setAdapter(new ArrayAdapter<>(this, R.layout.suggest_item, R.id.suggestion_text, getResources().getStringArray(R.array.query_suggestions)));
        }
        omniBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        loadUrlFromTextBox();
                        omniBox.setCursorVisible(false);
                        omniBox.clearFocus();
                        webView.isFocused();
                        if (preferences.getBoolean("show_history", false)) {
                            addSearchInput(omniBox.getText().toString());
                        }
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
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(webView, 0);
                omniBox.setCursorVisible(true);
                omniBox.selectAll();
                omniBox.hasFocus();
                omniBox.setHighlightColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600));

            }
        });


        omniBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                try {
                    loadUrlFromTextBox();
                    omniBox.clearFocus();
                    webView.isFocused();
                    savePrefs();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        });


        if (preferences.getBoolean("show_home", false)) {
            home.setVisibility(View.VISIBLE);
        } else {
            home.setVisibility(View.GONE);
        }

        if (preferences.getBoolean("show_home", false)) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.loadUrl(homepage);
                    webView.isFocused();
                }
            });
        }

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.stopLoading();
                webView.isFocused();
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
                            if (ActivityCompat.checkSelfPermission(NewWindow.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(NewWindow.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            } else {
                                try {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                    String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    lastDownload =dm.enqueue(request);
                                    Intent intent = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    }
                                    if (intent != null) {
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    }
                                    if (intent != null) {
                                        intent.setType("*/*");
                                    }


                                } catch (Exception exc) {
                                    FirebaseCrash.report(new Exception(exc));
                                    Toast.makeText(NewWindow.this, exc.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            try {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                lastDownload =dm.enqueue(request);
                                Intent intent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                }
                                if (intent != null) {
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                }
                                if (intent != null) {
                                    intent.setType("*/*");
                                }


                            } catch (Exception exc) {
                                FirebaseCrash.report(new Exception(exc));
                                Toast.makeText(NewWindow.this, exc.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                snackbar.show();
            }
        });

        SimplicityChromeClient webChromeClient = new SimplicityChromeClient(this) {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE)
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                progressBar.setProgress(progress);
                if (progress == 100)
                    progressBar.setVisibility(ProgressBar.GONE);
            }

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
                    FirebaseCrash.report(new Exception(e));
                    uploadMessage = null;
                    Toast.makeText(NewWindow.this.getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
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
                        omniBox.setText(getResources().getString(R.string.no_connection));
                    }
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    FirebaseCrash.report(new Exception(e));
                    e.printStackTrace();
                }

            }


            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                try {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        favoriteIcon = icon;
                        if (icon != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Palette.from(icon).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                                    if (vibrant != null) {
                                        setColor(palette.getVibrantColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600)));
                                    } else {
                                        setColor(palette.getMutedColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600)));
                                    }
                                    if (vibrant != null && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && preferences.getBoolean("nav_color", false)) {
                                        getWindow().setNavigationBarColor(palette.getVibrantColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600)));
                                    } else {
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && preferences.getBoolean("nav_color", false)) {
                                            setColor(palette.getMutedColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600)));
                                        }

                                    }

                                }
                            });
                        }


                    }else {
                        Log.i("MainActivity", "Not Lollipop+");

                    }
                }catch (NullPointerException ignored) {
                } catch (Exception e) {
                    FirebaseCrash.report(new Exception(e));
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
        defaultSearch = savedPreferences.getString("search_engine", "");
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (sim.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
    }


    @Override
    public void onCreateHomeScreenShortcutCancel(DialogFragment dialog) {

    }

    @Override
    public void onCreateHomeScreenShortcutCreate(DialogFragment dialog) {
        EditText shortcutName = (EditText) dialog.getDialog().findViewById(R.id.shortcutNameEditText);
        shortcutName.setText(webView.getTitle());
        Intent bookmarkShortcut = new Intent(getApplicationContext(), MainActivity.class);
        bookmarkShortcut.setAction(Intent.ACTION_VIEW);
        bookmarkShortcut.setData(Uri.parse(UrlCleaner));
        Intent shortcutMaker = new Intent();
        shortcutMaker.putExtra("duplicate", false);
        shortcutMaker.putExtra("android.intent.extra.shortcut.INTENT", bookmarkShortcut);
        shortcutMaker.putExtra("android.intent.extra.shortcut.NAME", shortcutName.getText().toString());
        shortcutMaker.putExtra("android.intent.extra.shortcut.ICON", webView.getFavicon());
        shortcutMaker.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(shortcutMaker);
    }




    @Override
    public void onBackPressed() {
        if (sim.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (bookmarksDrawer.isDrawerOpen(GravityCompat.END)) {
                    bookmarksDrawer.closeDrawer(GravityCompat.END);
                } else {
                    super.onBackPressed();
                }
            }

        }




    @Override
    protected void onPause() {
        preferences.edit().putString("needs_lock", "true").apply();
        savePrefs();
        super.onPause();
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
        }
        PreferencesUtility.saveBookmarks(adapterBookmarks.getListBookmarks());

        if (sim.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        registerForContextMenu(webView);
        webView.onResume();
        webView.requestFocus();
        listBookmarks = PreferencesUtility.getBookmarks();
        preferences.edit().putStringSet(PREFS_SEARCH_HISTORY, history).apply();
        preferences.getStringSet(PREFS_SEARCH_HISTORY, history);
        if (PreferencesUtility.getString("needs_lock", "").equals("true") && (preferences.getBoolean("smart_pass", false))) {
            Intent intent = new Intent(NewWindow.this, SmartPassUnlock.class);
            startActivity(intent);
        }
        if(isPrivate){
            showPrivateNotification();
        }
        if (sim.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPrivate();
        savePrefs();
        unregisterReceiver(onComplete);
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
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
                ClipboardManager clipboard = (ClipboardManager) NewWindow.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                    bottomtabs.setBackgroundColor((int) animator.getAnimatedValue());
                    bottomtabs.setVisibility(View.VISIBLE);
                    if (preferences.getBoolean("show_home", false)) {
                        toolbar.setLogo(null);
                    }
                    progressBar.setBackgroundColor((int) animator.getAnimatedValue());
                    brand.setBackgroundColor((int) animator.getAnimatedValue());
                    brand.setTextColor(ContextCompat.getColor(NewWindow.this, R.color.white));


                }
            });
            colorAnimation.start();
        }else{
            toolbar.setBackgroundColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600));
            swipeToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600));
            header = (RelativeLayout) findViewById(R.id.bookmarks_header);
            header.setBackgroundColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600));
            bottomtabs.setBackgroundColor(ContextCompat.getColor(NewWindow.this, R.color.md_blue_600));
            bottomtabs.setVisibility(View.VISIBLE);
            if (preferences.getBoolean("show_home", false)) {
                toolbar.setLogo(null);
            }
            progressBar.setBackgroundColor(ContextCompat.getColor(NewWindow.this, R.color.black_semi_transparent));
            brand.setBackgroundColor(ContextCompat.getColor(NewWindow.this, R.color.black_semi_transparent));
            brand.setTextColor(ContextCompat.getColor(NewWindow.this, R.color.black));
        }


        //noinspection deprecation
        bookmarksDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                setTranslucentStatus(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setTranslucentStatus(false);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    @SuppressLint("InflateParams")
    private void setAutoCompleteSource() {
        omniBox = (AutoCompleteTextView) findViewById(R.id.omniBox);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.history_list, R.id.suggestion_text, history.toArray(new String[history.size()]));
        omniBox.setAdapter(adapter);
    }

    private void addSearchInput(String input){
        if (!history.contains(input)){
            history.add(input);
            if (preferences.getBoolean("show_history", false)) {
                setAutoCompleteSource();
            }
        }
    }

    private void savePrefs(){
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
                if (preferences.getBoolean("no_track", false)) {
                    HashMap<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("DNT", "1");
                    view.loadUrl(url, extraHeaders);
                    return true;
                }
                if ((url.contains("market://")
                        || url.contains("mailto:")
                        || url.contains("play.google")
                        || url.contains("tel:"))) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if ((url.contains("http://") || url.contains("https://"))) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    FirebaseCrash.report(new Exception(e));
                    Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                    FirebaseCrash.log(e.getMessage());
                    e.printStackTrace();
                }

                return true;
            } catch (NullPointerException npe) {
                FirebaseCrash.report(new Exception(npe));
                npe.printStackTrace();
                return true;
            }
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                swipeToRefresh.setRefreshing(false);
                swipeToRefresh.setEnabled(false);
                omniBox.setText(url);


                stop.setVisibility(View.GONE);
                if ((url.contains("https://"))) {
                    secure.setVisibility(View.VISIBLE);
                } else {
                    secure.setVisibility(View.GONE);
                }


                ImageView simrefresh = (ImageView) findViewById(R.id.sim_refresh);
                ImageView simstop = (ImageView) findViewById(R.id.sim_stop);

                simstop.setVisibility(View.VISIBLE);
                if (simrefresh.getVisibility() == View.VISIBLE) {
                    simrefresh.setVisibility(View.GONE);
                }

            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                FirebaseCrash.report(new Exception(e));
                e.printStackTrace();
            }
        }


        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (Connectivity.isConnected(NewWindow.this) && !refreshed) {
                view.loadUrl(failingUrl);

                refreshed = true;
            }
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {

            onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                swipeToRefresh.setRefreshing(false);
                swipeToRefresh.setEnabled(true);
                omniBox.setText(webView.getUrl());
                listBookmarks = PreferencesUtility.getBookmarks();
                stop.setVisibility(View.GONE);
                toolbar.setTitle(null);
                addSearchInput(webView.getUrl());

                if (url.contains("file:///")) {
                    toolbar.setTitle(null);
                    toolbar.setSubtitle(null);
                }

                ImageView browserstop = (ImageView) findViewById(R.id.sim_stop);
                browserstop.setVisibility(View.GONE);
                ImageView browserrefresh = (ImageView) findViewById(R.id.sim_refresh);
                browserrefresh.setVisibility(View.VISIBLE);
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                FirebaseCrash.report(new Exception(e));
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


    private void newDialog() {
        try {
            AlertDialog.Builder whats_new = new AlertDialog.Builder(NewWindow.this);
            whats_new.setTitle("What's New");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new), Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new)));
            }
            whats_new.setPositiveButton("Great, Let's Go!", null);
            FirebaseCrash.log("User seen new dialog and clicked ok.");
            whats_new.show();
        } catch (Exception ignored) {
            FirebaseCrash.report(new Exception(ignored));

        }
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void showPrivateNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this, R.color.md_grey_900))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Running in private mode.")
                .setTicker(null)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setOngoing(true);
        mBuilder.setSmallIcon(R.drawable.ic_visibility_off_black_24dp);
        Notification note = mBuilder.build();
        mNotificationManager.notify(1, note);


    }

    public static void clearPrivate() {
        NotificationManager notificationManager = (NotificationManager)
                SimplicityApplication.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public static void isKeyBoardShowing() {
        try{
            webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    webView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = webView.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        try {
                            bottomtabs.setVisibility(View.GONE);
                            if (sim.getVisibility() == View.VISIBLE) {
                                sim.setVisibility(View.GONE);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }
            });
        } catch (Exception ignored) {

        }
    }
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Snackbar snackbar = Snackbar.make(webView, "Download finished", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("view", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                }
            });
            snackbar.show();
        }
    };


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.menu_holder:
                    hideMenu();
                    return;

                case R.id.sim_go_forward:
                    hideMenu();
                    AlertDialog.Builder info = new AlertDialog.Builder(NewWindow.this);
                    info.setTitle(webView.getUrl());
                    if (webView.getUrl().contains("https://")) {
                        info.setMessage(getResources().getString(R.string.private_info));
                    } else {
                        info.setMessage(getResources().getString(R.string.none_private_info));
                    }
                    info.setPositiveButton("OKAY", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    info.setNeutralButton(null, null);
                    info.show();
                    return;

                case R.id.sim_bookmark:
                    hideMenu();
                    Bookmark bookmark = new Bookmark();
                    bookmark.setTitle(webView.getTitle());
                    bookmark.setUrl(webView.getUrl());
                    bookmark.setImage(webView.getFavicon());
                    adapterBookmarks.addItem(bookmark);
                    Snackbar snackbar = Snackbar.make(webView, "Added: " + webView.getTitle().replace("", "") + " to bookmarks.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("view", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookmarksDrawer.openDrawer(GravityCompat.END);
                        }
                    });
                    snackbar.show();
                    return;

                case R.id.sim_refresh:
                    hideMenu();
                    webView.reload();
                    return;

                case R.id.sim_stop:
                    hideMenu();
                    webView.stopLoading();
                    return;

                case R.id.sim_new_window:
                    hideMenu();
                    if (preferences.getBoolean("merge_windows", false)) {
                        Intent intent = new Intent(NewWindow.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(NewWindow.this, MainActivity.class);
                        startActivity(intent);
                    }
                    return;

                case R.id.sim_private_mode:
                    hideMenu();
                    isPrivate = !isPrivate;
                    pri.setChecked(isPrivate);
                    if (!isPrivate) {
                        clearPrivate();
                        CookieManager.getInstance().setAcceptCookie(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setSavePassword(true);
                        webView.getSettings().setSaveFormData(true);
                        webView.getSettings().setDatabaseEnabled(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.reload();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().setAcceptCookie(true);
                        } else {
                            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
                        }
                        CookieSyncManager.createInstance(NewWindow.this);
                        CookieSyncManager.getInstance().startSync();
                    } else {
                        showPrivateNotification();
                        webView.isPrivateBrowsingEnabled();
                        webView.getSettings().setSavePassword(false);
                        webView.getSettings().setSaveFormData(false);
                        webView.getSettings().setDatabaseEnabled(false);
                        webView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(NewWindow.this, R.color.md_grey_900));
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && preferences.getBoolean("nav_color", false)) {
                            getWindow().setNavigationBarColor(ContextCompat.getColor(NewWindow.this, R.color.md_grey_900));
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().setAcceptCookie(false);
                        } else {
                            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
                        }
                        CookieSyncManager.createInstance(NewWindow.this);
                        CookieSyncManager.getInstance().startSync();
                    }
                    hideMenu();
                    return;

                case R.id.sim_books:
                    hideMenu();
                    bookmarksDrawer.openDrawer(GravityCompat.END);
                    return;

                case R.id.sim_find:
                    hideMenu();
                    webView.showFindDialog(null, true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    return;

                case R.id.sim_share:
                    hideMenu();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Share current page");
                    i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                    startActivity(Intent.createChooser(i, "Share with"));
                    return;

                case R.id.sim_home_screen:
                    hideMenu();
                    AppCompatDialogFragment shortcutDialog = new ShortcutActivity();
                    shortcutDialog.show(getSupportFragmentManager(), "createShortcut");
                    return;

                case R.id.sim_desktop:
                    hideMenu();
                    if (desk.isChecked()) {
                        desk.setChecked(false);
                    } else {
                        desk.setChecked(true);
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
                    return;

                case R.id.sim_settings:
                    hideMenu();
                    Intent settingsIntent = new Intent(NewWindow.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    webView.onPause();
                    return;

                case R.id.sim_exit:
                    hideMenu();
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                    return;

                case R.id.sim_back:
                    appbar.setExpanded(true, true);
                    if (bookmarksDrawer.isDrawerOpen(GravityCompat.END)) {
                        bookmarksDrawer.closeDrawer(GravityCompat.END);
                    } else {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                    }
                    return;
                case R.id.sim_forward:
                    appbar.setExpanded(true, true);
                    if (webView.canGoForward()) {
                        webView.goForward();
                    }
                    return;

                case R.id.sim_newwindow:
                    if (preferences.getBoolean("merge_windows", false)) {
                        Intent intent = new Intent(NewWindow.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(NewWindow.this, MainActivity.class);
                        startActivity(intent);
                    }
                    return;

                case R.id.sim_jump:
                    appbar.setExpanded(true, true);
                    scrollToTop();
                    return;

                case R.id.refresh_page:
                    showMenu();
                    return;

                case R.id.sim_downloads:
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    return;

                case R.id.sim_desktop_check:
                    hideMenu();
                    if (desk.isChecked()) {
                        desk.setChecked(true);

                    } else {
                        desk.setChecked(false);
                    }
                    if (computerMode) {
                        webView.getSettings().setUserAgentString("");
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.reload();
                        computerMode = false;
                    } else {
                        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.41 Safari/537.36");
                        webView.getSettings().setLoadWithOverviewMode(false);
                        webView.getSettings().setUseWideViewPort(false);
                        webView.reload();
                        computerMode = true;
                    }
                    return;

                case R.id.sim_private_check:
                    hideMenu();
                    isPrivate = !isPrivate;
                    pri.setChecked(isPrivate);
                    if (!isPrivate) {
                        clearPrivate();
                        CookieManager.getInstance().setAcceptCookie(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setSavePassword(true);
                        webView.getSettings().setSaveFormData(true);
                        webView.getSettings().setDatabaseEnabled(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.reload();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().setAcceptCookie(true);
                        } else {
                            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
                        }
                        CookieSyncManager.createInstance(NewWindow.this);
                        CookieSyncManager.getInstance().startSync();
                    } else {
                        showPrivateNotification();
                        webView.isPrivateBrowsingEnabled();
                        webView.getSettings().setSavePassword(false);
                        webView.getSettings().setSaveFormData(false);
                        webView.getSettings().setDatabaseEnabled(false);
                        webView.getSettings().setDomStorageEnabled(false);
                        setColor(ContextCompat.getColor(NewWindow.this, R.color.md_grey_900));
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && preferences.getBoolean("nav_color", false)) {
                            getWindow().setNavigationBarColor(ContextCompat.getColor(NewWindow.this, R.color.md_grey_900));
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().setAcceptCookie(false);
                        } else {
                            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
                        }
                        CookieSyncManager.createInstance(NewWindow.this);
                        CookieSyncManager.getInstance().startSync();
                    }
                    hideMenu();

                    return;

                default:
                    hideMenu();

            }

        }

    };

    private void forClicks() {
        sim = (CardView) findViewById(R.id.sim_menu);
        menuScroll = (ScrollView) findViewById(R.id.scroller);
        menuHolder = (FrameLayout) findViewById(R.id.menu_holder);
        menuHolder.setOnClickListener(onClickListener);
        menuHolder.setClickable(false);
        menuHolder.setFocusable(false);
        findViewById(R.id.sim_go_forward).setOnClickListener(onClickListener);
        findViewById(R.id.sim_bookmark).setOnClickListener(onClickListener);
        findViewById(R.id.sim_refresh).setOnClickListener(onClickListener);
        findViewById(R.id.sim_stop).setOnClickListener(onClickListener);
        findViewById(R.id.sim_new_window).setOnClickListener(onClickListener);
        findViewById(R.id.sim_private_mode).setOnClickListener(onClickListener);
        findViewById(R.id.sim_books).setOnClickListener(onClickListener);
        findViewById(R.id.sim_find).setOnClickListener(onClickListener);
        findViewById(R.id.sim_share).setOnClickListener(onClickListener);
        findViewById(R.id.sim_home_screen).setOnClickListener(onClickListener);
        findViewById(R.id.sim_desktop).setOnClickListener(onClickListener);
        findViewById(R.id.sim_settings).setOnClickListener(onClickListener);
        findViewById(R.id.sim_exit).setOnClickListener(onClickListener);
        findViewById(R.id.sim_back).setOnClickListener(onClickListener);
        findViewById(R.id.sim_forward).setOnClickListener(onClickListener);
        findViewById(R.id.sim_newwindow).setOnClickListener(onClickListener);
        findViewById(R.id.sim_jump).setOnClickListener(onClickListener);
        findViewById(R.id.refresh_page).setOnClickListener(onClickListener);
        findViewById(R.id.sim_downloads).setOnClickListener(onClickListener);
        findViewById(R.id.sim_desktop_check).setOnClickListener(onClickListener);
        findViewById(R.id.sim_private_check).setOnClickListener(onClickListener);


    }


    private void showMenu() {
        menuScroll.setScrollY(0);
        Animation grow = AnimationUtils.loadAnimation(this, R.anim.translate_from_top);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        grow.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                sim.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        in.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sim.startAnimation(grow);
        new_window.startAnimation(grow);
        pri_mode.startAnimation(grow);
        pri.startAnimation(grow);
        books.startAnimation(grow);
        find.startAnimation(grow);
        share.startAnimation(grow);
        homescreen.startAnimation(grow);
        desktop.startAnimation(grow);
        desk.startAnimation(grow);
        set.startAnimation(grow);
        exit.startAnimation(grow);
        menuHolder.setClickable(true);
        menuHolder.setFocusable(true);
        sim.setSoundEffectsEnabled(false);
        menuHolder.setSoundEffectsEnabled(false);
    }


    private void hideMenu() {
        Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                sim.setVisibility(View.GONE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sim.startAnimation(fade);
        menuHolder.setClickable(false);
        menuHolder.setFocusable(false);
        menuHolder.setSoundEffectsEnabled(false);
    }




}