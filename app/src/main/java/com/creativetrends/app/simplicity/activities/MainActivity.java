package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.adapters.BookmarkItems;
import com.creativetrends.app.simplicity.adapters.HistoryItems;
import com.creativetrends.app.simplicity.suggestions.SuggestionsAdapter;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.ui.SimplicitySslCertificate;
import com.creativetrends.app.simplicity.utils.AppUpdater;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.FileSize;
import com.creativetrends.app.simplicity.utils.SimplicityDownloader;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.app.simplicity.utils.TabManager;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.app.simplicity.webview.CSSInjection;
import com.creativetrends.app.simplicity.webview.NestedWebview;
import com.creativetrends.simplicity.app.R;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
import static android.view.View.GONE;

@SuppressWarnings (value="StaticFieldLeak")
public class MainActivity extends BaseActivity implements  SwipeRefreshLayout.OnRefreshListener {
    public static Activity mainActivity;
    public static SharedPreferences mPreferences;
    public NestedWebview mWebView;
    //WebSettings mWebSettings;
    boolean isDesktop;
    public static boolean isIncognito;
    public SimpleAutoComplete mSearchView;
    public static Toolbar mToolbar;
    String UrlCleaner, defaultSearch, defaultProvider;
    EditText shortcutNameEditText;
    public static ImageView mHomebutton, mJump, mSecure, mAddress, mTabs, mOverflow, vSearch, bookmarkicon, mForward, mRefresh, mStop, mClose;

    public static TextView mBadgeText;
    public static Bitmap favoriteIcon;
    AppBarLayout mAppbar;
    //BottomNavigationViewEx mTabs;
    HashMap<String, String> extraHeaders = new HashMap<>();
    public static CardView mCardView;
    public static LinearLayout bCardView, top, items;
    FrameLayout mHolder, customViewContainer;
    AppCompatCheckBox pri, desk;
    public static String homepage;
    public static boolean adBlockerEnabled;
    CoordinatorLayout background_color;
    private static final int STORAGE_PERMISSION_CODE = 2284, REQUEST_STORAGE = 1;
    private String urlToGrab;
    public AnimatedProgressBar mProgress;
    private long back_pressed;
    public static String webViewTitle;
    public static SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout swipe, desk_rel;
    RelativeLayout root;
    public static RelativeLayout mStop_rel, mRefresh_rel, update_root;
    public static Set<String> adServersSet;

    // fullscreen videos
    public static MyWebChromeClient mWebChromeClient;
    // variables for camera and choosing files methods
    private static final int FILECHOOSER_RESULTCODE = 1;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    ArrayList<BookmarkItems> listBookmarks = new ArrayList<>();
    BookmarkItems bookmark;
    boolean TopTabs;
    public static boolean isLoading;
    //public static final String EXTRA_URL = "extra_url";
    //public static final String ACTION_URL_RESOLVED = "com.creativetrends.simplicity.app.URL_RESOLVED";
    public static SslCertificate sslCertificate;
    public static int scrollPosition = 0;
    String filename1;
    public static String file_size_main;
    //drawer
    DrawerLayout mDrawer;
    //navigation
    public static NavigationView mNavigation;
    Uri data;
    ViewGroup.MarginLayoutParams layoutParams;
    public static final String PREFS_NAME = "PingBusPrefs";
    public static final String PREFS_SEARCH_HISTORY = "SearchHistory";
    SharedPreferences settings;
    private Set<String> history;
    public static DownloadListener mDownloadlistener;
    //private static List<NestedWebview> mViewsList = new ArrayList<>();
    LinearLayout mNewTab, mCloseTab;
    private static ForegroundColorSpan initialGrayColorSpan;
    private static ForegroundColorSpan finalGrayColorSpan;
    Executor newExecutor = Executors.newSingleThreadExecutor();
    public static int current_color;
    boolean isTablet;
    public static String currentVersion, latestVersion;


    AppCompatCheckBox checker;

    private boolean ifLaunched = true;

    RelativeLayout search_bar;
    //TextView sAddress, sTitle;
    //RelativeLayout sug;

    /*private final BroadcastReceiver mUrlResolvedReceiver = new BroadcastReceiver() {
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
    };*/


    public final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE || mWebView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_TYPE) {
                urlToGrab = mWebView.getHitTestResult().getExtra();
                new FileSize(urlToGrab).execute();
                BottomSheetMenuDialog dialog = new BottomSheetBuilder(MainActivity.this, null)
                        .setMode(BottomSheetBuilder.MODE_LIST)
                        .setMenu(R.menu.menu_image)
                        .addTitleItem(R.string.image_actions)
                        .setItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.image_save:
                                    filename1 = URLUtil.guessFileName(urlToGrab, null, getMimeType(urlToGrab));
                                    if (UserPreferences.getBoolean("rename", false)) {
                                        new FileSize(urlToGrab).execute();
                                        new Handler().postDelayed(() -> createDownload(), 1800);
                                    } else {
                                        requestStoragePermission();
                                    }
                                    break;

                                case R.id.image_open:
                                    try {
                                        NestedWebview beHeView = new NestedWebview(getApplicationContext(), MainActivity.this, mProgress, mSearchView);
                                        beHeView.loadUrl(mWebView.getHitTestResult().getExtra());
                                        TabManager.addTab(beHeView);
                                        TabManager.setCurrentTab(beHeView);
                                        TabManager.updateTabView();
                                        refreshTab();
                                        if (TabManager.getList() != null) {
                                            mBadgeText.setText(String.valueOf(TabManager.getList().size()));
                                        }
                                    } catch (Exception i) {
                                        i.printStackTrace();
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
                        .setItemClickListener(item -> {

                            switch (item.getItemId()) {

                                case R.id.link_open:
                                    try {
                                        NestedWebview beHeView = new NestedWebview(getApplicationContext(), MainActivity.this, mProgress, mSearchView);
                                        beHeView.loadUrl(mWebView.getHitTestResult().getExtra());
                                        TabManager.addTab(beHeView);
                                        TabManager.setCurrentTab(beHeView);
                                        TabManager.updateTabView();
                                        refreshTab();
                                        if (TabManager.getList() != null) {
                                            mBadgeText.setText(String.valueOf(TabManager.getList().size()));
                                        }
                                    } catch (Exception i) {
                                        i.printStackTrace();
                                    }
                                    break;

                                case R.id.link_copy_text:
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
            } else if (mWebView.getHitTestResult().getType() == WebView.HitTestResult.PHONE_TYPE) {
                Intent intent14 = new Intent(Intent.ACTION_DIAL);
                intent14.setData(Uri.parse(mWebView.getHitTestResult().getExtra()));
                startActivity(intent14);
                return true;
            } else {
                Log.d("MainActivity", "Show webview context");
                return false;
            }
        }
    };


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.sim_new_update_rel:
                    String update = "https://play.google.com/store/apps/details?id=com.creativetrends.simplicity.app";
                    try {
                        if (!URLUtil.isValidUrl(update)) {
                            Toast.makeText(mainActivity, "Couldn't get link", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(update));
                            mainActivity.startActivity(intent);
                        }
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                        Toast.makeText(mainActivity, i.toString(), Toast.LENGTH_SHORT).show();
                    }
                    return;

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
                        String setLetter = getWebTitle.substring(0, 1);
                        listBookmarks = UserPreferences.getBookmarks();
                        bookmark = new BookmarkItems();
                        bookmark.setTitle(mWebView.getTitle());
                        bookmark.setUrl(mWebView.getUrl());
                        bookmark.setLetter(setLetter);
                        bookmark.setImage(Palette.from(favoriteIcon).generate().getVibrantColor(Palette.from(favoriteIcon).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.md_blue_600))));
                        listBookmarks.add(bookmark);
                        UserPreferences.saveBookmarks(listBookmarks);
                        Cardbar.snackBar(getApplicationContext(), mWebView.getTitle() + " " + getResources().getString(R.string.added_to_bookmarks), true).show();
                        if (currentUser != null) {
                            uploadToFireBase();
                        }

                    }
                    return;


                case R.id.sim_download:
                    try {
                        hideMenu();
                        Intent downloadIntent = new Intent(MainActivity.this, FilePickerActivity.class);
                        startActivity(downloadIntent);
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                    } catch (NullPointerException ignored) {

                    }
                    return;


                case R.id.sim_downloads:
                    try {
                        hideMenu();
                        StaticUtils.openDownloads(MainActivity.this);
                    } catch (ActivityNotFoundException i) {
                        i.printStackTrace();
                    } catch (NullPointerException ignored) {

                    }
                    return;
                case R.id.home_rel:
                case R.id.sim_refresh:
                    mWebView.reload();
                    if (UserPreferences.getBoolean("lite_mode", false)) {
                        mWebView.getSettings().setLoadsImagesAutomatically(false);
                    } else {
                        mWebView.getSettings().setLoadsImagesAutomatically(true);
                    }
                    return;

                case R.id.home2_rel:
                case R.id.sim_refresh2:
                    if (isLoading) {
                        mWebView.stopLoading();
                        isLoading = false;
                    }
                    return;
                case R.id.sim_stop:
                    hideMenu();
                    try {
                        viewSslCertificate(mWebView);
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                    return;

                case R.id.sim_new_window:
                    hideMenu();
                    try {
                        if (mWebView != null && !mWebView.hasFocus()) {
                            mWebView.requestFocus();
                        }
                        NestedWebview behe = new NestedWebview(getApplicationContext(), MainActivity.this, mProgress, mSearchView);
                        behe.loadHomepage();
                        TabManager.addTab(behe);
                        TabManager.setCurrentTab(behe);
                        TabManager.updateTabView();
                        refreshTab();
                        if (TabManager.getList() != null) {
                            if (TabManager.getList().size() >= 20) {
                                mBadgeText.setText(":O");
                            } else {
                                mBadgeText.setText(String.valueOf(TabManager.getList().size()));
                            }
                        }
                    } catch (Exception z) {
                        z.printStackTrace();
                    }
                    return;

                case R.id.sim_private_mode:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    Intent intent = new Intent(MainActivity.this, PrivateActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                    intent.setData(Uri.parse("about:blank"));
                    startActivity(intent);
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
                    } catch (NullPointerException ignored) {
                    } catch (Exception p) {
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

                case R.id.sim_private_check:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    Intent pri_intent = new Intent(MainActivity.this, PrivateActivity.class);
                    startActivity(pri_intent);
                    return;

                case R.id.voice_button:
                    promptSpeechInput();
                    return;

                case R.id.sim_close:
                    hideMenu();
                    System.exit(0);
                    if (mPreferences.getBoolean("clear_data", false) && getIntent().getBooleanExtra("isNewTab", false)) {
                        StaticUtils.deleteCache(SimplicityApplication.getContextOfApplication());
                        ArrayList<HistoryItems> listHistory = UserPreferences.getHistory();
                        listHistory.clear();
                        UserPreferences.saveHistory(listHistory);
                        mPreferences.edit().putString("last_page_reminder", homepage).apply();
                    }
                    return;


                case R.id.sim_home_tabs:
                    if (mPreferences.getBoolean("no_track", false)) {
                        mAppbar.setExpanded(true, true);
                        mWebView.loadUrl(homepage, extraHeaders);
                    } else {
                        mAppbar.setExpanded(true, true);
                        mWebView.loadUrl(homepage);
                    }
                    break;

                case R.id.jump_rel:
                case R.id.sim_jump_tabs:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    if (scrollPosition >= 10) {
                        scrollToTop(mWebView);
                        mAppbar.setExpanded(true, true);
                    }
                    break;

                case R.id.search_rel:
                case R.id.sim_address:
                    mSearchView.requestFocus();
                    mSearchView.selectAll();
                    try {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    } catch (Exception in) {
                        in.printStackTrace();
                    }
                    break;


                case R.id.tabs_rel:
                case R.id.sim_tabs_tabs:
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                    /*if (mPreferences.getBoolean("merge_windows", false)) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_DOCUMENT | FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.setData(Uri.parse(homepage));
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("isNewTab" , true);
                        startActivity(intent);
                    }*/
                    mDrawer.openDrawer(GravityCompat.START, true);
                    break;


                case R.id.overflow_rel:
                case R.id.sim_over_button:
                    showMenu();
                    break;

                default:

            }

        }

    };


    @SuppressLint({"SetJavaScriptEnabled", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        TopTabs = UserPreferences.getInstance(this).getTabs().equals("top");
        if (UserPreferences.getBoolean("dark_mode", false)) {
            setTheme(R.style.MainThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ! UserPreferences.getBoolean("dark_mode", false)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }else{
            getMainActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getMainActivity(), R.color.black));

        }

        if (UserPreferences.getBoolean("dark_mode", false)) {
            setColor(ContextCompat.getColor(this, R.color.md_grey_900));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            isIncognito = true;
            View div = findViewById(R.id.light_divide);
            div.setVisibility(GONE);
        }
    }


    public void initialize() {
        currentVersion = StaticUtils.getAppVersionName(this);
        new AppUpdater(this).execute();
        settings = getSharedPreferences(PREFS_NAME, 0);
        history = settings.getStringSet(PREFS_SEARCH_HISTORY, new HashSet<>());
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
        mClose = findViewById(R.id.search_clear);
        mForward = findViewById(R.id.sim_go_forward);
        top = findViewById(R.id.root_overflow);
        items = findViewById(R.id.sub_overflow);
        mAppbar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        bCardView = findViewById(R.id.root_bottom_tabs);
        mAddress = findViewById(R.id.sim_address);
        mTabs = findViewById(R.id.sim_tabs_tabs);
        mBadgeText = findViewById(R.id.tabs_badge);
        mNewTab = findViewById(R.id.new_click);
        mCloseTab = findViewById(R.id.close_click);
        update_root = findViewById(R.id.sim_new_update_rel);
        search_bar = findViewById(R.id.search_bar);
        //sAddress = findViewById(R.id.suggest_url);
        //sTitle = findViewById(R.id.suggest_title);
        desk_rel = findViewById(R.id.desk_rel);
        //sug = findViewById(R.id.simple_suggestion_background);
        if (isTablet) {
            desk_rel.setVisibility(GONE);
        }
        //relative layouts start

        //relative layouts end
        mDrawer = findViewById(R.id.drawer);
        mDrawer.setScrimColor(ContextCompat.getColor(this, R.color.transparent));
        mNavigation = findViewById(R.id.nav_tabs);
        mNavigation.setItemIconTintList(null);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        TabManager.setNavigationView(mNavigation);
        mNewTab.setOnClickListener(v -> {
            try {
                NestedWebview behe = new NestedWebview(getApplicationContext(), MainActivity.this, mProgress, mSearchView);
                behe.loadHomepage();
                TabManager.addTab(behe);
                TabManager.setCurrentTab(behe);
                TabManager.updateTabView();
                refreshTab();
                if (TabManager.getList() != null) {
                    if (TabManager.getList().size() >= 20) {
                        mBadgeText.setText(":O");
                    } else {
                        mBadgeText.setText(String.valueOf(TabManager.getList().size()));
                    }
                }
                mDrawer.closeDrawer(GravityCompat.START, false);
            } catch (Exception n) {
                n.printStackTrace();
            }
        });
        mCloseTab.setOnClickListener(v -> {
            try {
                int size = 0;
                if (TabManager.getList() != null) {
                    size = TabManager.getList().size();
                }
                if (size > 1) {
                    NestedWebview tab = TabManager.getCurrentTab();
                    NestedWebview main = TabManager.getList().get(0);
                    TabManager.setCurrentTab(main);
                    TabManager.removeTab(tab);
                    TabManager.updateTabView();
                    refreshTab();
                    if (TabManager.getList().size() >= 20) {
                        mBadgeText.setText(":O");
                    } else {
                        mBadgeText.setText(String.valueOf(TabManager.getList().size()));
                    }
                    mDrawer.closeDrawer(GravityCompat.START, false);
                } else if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            } catch (Exception d) {
                d.printStackTrace();
            }
        });

        mClose.setOnClickListener(v -> mSearchView.setText(""));


        mBadgeText.setText(R.string.one);
        swipe = new RelativeLayout(this);
        root = findViewById(R.id.root);
        if (getSupportActionBar() != null) {
            setSupportActionBar(mToolbar);
        }
        mSearchView = findViewById(R.id.search_box);
        initialGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_500));
        finalGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_500));
        TabManager.setCookie(true);
        initializeBeHeView();
        mWebView.setOnScrollChangedCallback((l, t) -> {
            scrollPosition = t;
            if (scrollPosition >= 10) {
                layoutParams = (ViewGroup.MarginLayoutParams) swipeRefreshLayout.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                swipeRefreshLayout.requestLayout();
            } else {
                layoutParams = (ViewGroup.MarginLayoutParams) swipeRefreshLayout.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.photo_snack));
                swipeRefreshLayout.requestLayout();
            }
        });
        data = getIntent().getData();
        forClicks();
        extraHeaders.put("DNT", "1");
        homepage = mPreferences.getString("homepage", "");
        defaultSearch = mPreferences.getString("search_engine", "");
        webViewTitle = getString(R.string.app_name);

        final Intent intent = getIntent();
        String intentUrl = getIntent().getStringExtra("start_widget");
        if (intent.getData() != null) {
            final Uri intentUriData = intent.getData();
            UrlCleaner = intentUriData.toString();
            getIntent().removeExtra(intentUriData.toString());
        } else {
            try {
                if (intentUrl != null) {
                    if (intentUrl.contains("widget")) {
                        mSearchView.requestFocus();
                        mSearchView.selectAll();
                        mSearchView.getText().clear();
                        try {
                            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        } catch (Exception in) {
                            in.printStackTrace();
                        }
                    }
                }
                getIntent().removeExtra(intentUrl);
            } catch (Exception i) {
                i.printStackTrace();

            }
        }


        if (UrlCleaner == null && mPreferences.getBoolean("remember_page", false)) {
            UrlCleaner = mPreferences.getString("last_page_reminder", "");
        } else if (UrlCleaner == null && !mPreferences.getBoolean("remember_page", false)) {
            UrlCleaner = homepage;
        }
        if (mPreferences.getBoolean("no_track", false)) {
            mWebView.loadUrl(UrlCleaner, extraHeaders);
        } else {
            mWebView.loadUrl(UrlCleaner);
        }

        adServersSet = new HashSet<>();

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

        mWebChromeClient = new MyWebChromeClient(this);
        mDownloadlistener = (url, userAgent, contentDisposition, mimetype, contentLength) -> {
            filename1 = URLUtil.guessFileName(url, contentDisposition, getMimeType(url));
            urlToGrab = url;
            if (StaticUtils.isMarshmallow()) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                } else {
                    try {
                        new FileSize(url).execute();
                        if (UserPreferences.getBoolean("rename", false)) {
                            new Handler().postDelayed(this::createDownload, 1800);
                        } else {
                            Cardbar.snackBar(getApplicationContext(), getString(R.string.downloading), true).show();
                            new SimplicityDownloader(this).execute(Uri.parse(url).toString());
                        }
                    } catch (Exception exc) {
                        Cardbar.snackBar(getApplicationContext(), exc.toString(), true).show();


                    }
                }
            }
        };


        if (mPreferences.getBoolean("from_history", false)) {
            setAutoCompleteSource();
        } else {
            mSearchView.setAdapter(new SuggestionsAdapter(this));
        }
        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (mPreferences.getBoolean("from_history", false)) {
                addSearchInput(mSearchView.getText().toString());
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                StaticUtils.hideKeyboard(mSearchView);
                if (mSearchView.getText().toString().contains("simplicity://flags")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent Intent = new Intent(MainActivity.this, ExperimentalActivity.class);
                    startActivity(Intent);
                } else if (mSearchView.getText().toString().contains("simplicity://history")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(history);

                } else if (mSearchView.getText().toString().contains("simplicity://bookmarks")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent settingsIntent = new Intent(MainActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                } else {
                    loadUrlFromTextBox();
                }
                mSearchView.clearFocus();
                return true;
            }
            return false;
        });
        mSearchView.setOnKeyListener((v, keyCode, event) -> {
            if (mPreferences.getBoolean("from_history", false)) {
                addSearchInput(mSearchView.getText().toString());
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                StaticUtils.hideKeyboard(mSearchView);
                if (mSearchView.getText().toString().contains("simplicity://flags")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent Intent = new Intent(MainActivity.this, ExperimentalActivity.class);
                    startActivity(Intent);
                } else if (mSearchView.getText().toString().contains("simplicity://history")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent history = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(history);

                } else if (mSearchView.getText().toString().contains("simplicity://bookmarks")) {
                    mSearchView.setText(mWebView.getUrl());
                    Intent settingsIntent = new Intent(MainActivity.this, BookmarksActivity.class);
                    startActivity(settingsIntent);
                } else {
                    loadUrlFromTextBox();
                }
                mSearchView.clearFocus();
                return true;
            } else {
                return false;
            }
        });

        mSearchView.setOnClickListener(v -> {
            mSearchView.getText().removeSpan(initialGrayColorSpan);
            mSearchView.getText().removeSpan(finalGrayColorSpan);
            mSearchView.setSelectAllOnFocus(true);
            if (mPreferences.getBoolean("from_history", false)) {
                addSearchInput(mSearchView.getText().toString());
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(mWebView, 0);
            }

        });

        mSearchView.setOnItemClickListener((parent, view, position, rowId) -> {
            if (mPreferences.getBoolean("from_history", false)) {
                addSearchInput(mSearchView.getText().toString());
            }
            StaticUtils.hideKeyboard(mSearchView);
            mSearchView.clearFocus();
            loadUrlFromTextBox();
        });



        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            bCardView.setTranslationY(Math.round(-verticalOffset));
            View view = findViewById(R.id.fake_shadow);
            view.setTranslationY(Math.round(-verticalOffset));
        });


        mNavigation.setNavigationItemSelectedListener(item -> {
            List<MenuItem> items = new ArrayList<>();
            Menu menu = mNavigation.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                items.add(menu.getItem(i));
            }
            for (MenuItem itm : items) {
                itm.setChecked(false);
            }
            item.setChecked(true);
            NestedWebview view = TabManager.getTabAtPosition(item);
            TabManager.setCurrentTab(view);
            refreshTab();
            mDrawer.closeDrawer(GravityCompat.START);
            if (UserPreferences.getBoolean("dark_mode", false)) {
                if (mWebView.getFavicon() != null) {
                    setColor(Palette.from(mWebView.getFavicon()).generate().getVibrantColor(Palette.from(mWebView.getFavicon()).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav))));
                } else {
                    setColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav));
                }
            }
            return false;
        });

        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                try {
                    Window window = getWindow();
                    window.setStatusBarColor(StaticUtils.adjustAlpha(current_color, 0.4f));
                    mDrawer.setStatusBarBackgroundColor(current_color);
                    if (mCardView.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                try {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(current_color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }

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
    public void onRestart() {
        super.onRestart();
        applyAppSettings();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            downloadFromFirebase();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ifLaunched) {
            handleIntents(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TabManager.resetAll(this, mProgress, mSearchView);
        if (getIntent() == null && mWebView.getUrl() == null) {
            mWebView.loadHomepage();
        }
        TabManager.resume();
        TabManager.updateTabView();
        applyHomeButton();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mDrawer.invalidate();
        TabManager.stopPlayback();
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        shouldSync = false;
    }


    @Override
    public void onBackPressed() {
        if (mCardView.getVisibility() == View.VISIBLE) {
            hideMenu();
        }else if (TabManager.getCurrentTab() == null || !TabManager.getCurrentTab().canGoBack()) {
            finishActivity();
        } else {
            goBack();
        }
    }


    private void handleIntents(Intent intent) {
        setIntent(intent);
        String webViewUrl = getIntent().getDataString();
            try {
                String intentUrl = getIntent().getStringExtra("start_widget");
                if (intentUrl != null) {
                    if (intentUrl.contains("widget")) {
                        mSearchView.requestFocus();
                        mSearchView.selectAll();
                        mSearchView.getText().clear();
                        try {
                            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        } catch (Exception in) {
                            in.printStackTrace();
                        }
                    }
                }
                intent.removeExtra(intentUrl);
            } catch (Exception i) {
                i.printStackTrace();

            }


            if (webViewUrl != null && URLUtil.isValidUrl(getIntent().getDataString())) {
                try {
                    int size = 0;
                    if (TabManager.getList() != null) {
                        size = TabManager.getList().size();
                    }
                    if (size < 1) {
                        mWebView.loadUrl(webViewUrl);
                    }else{
                        NestedWebview behe = new NestedWebview(getApplicationContext(), (MainActivity) MainActivity.getMainActivity(), ((MainActivity) MainActivity.getMainActivity()).mProgress, ((MainActivity) MainActivity.getMainActivity()).mSearchView);
                        behe.loadUrl(webViewUrl);
                        TabManager.addTab(behe);
                        TabManager.setCurrentTab(behe);
                        TabManager.updateTabView();
                        ((MainActivity) MainActivity.getMainActivity()).refreshTab();
                        try {
                            mBadgeText.setText(String.valueOf(size + 1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    intent.removeExtra(webViewUrl);
                } catch (Exception i) {
                    i.printStackTrace();
                }
            }

    }

    private void goBack() {
        TabManager.getCurrentTab().goBack();
    }

    private void finishActivity() {
        int size = 0;
        if (TabManager.getList() != null) {
            size = TabManager.getList().size();
        }
        if (size > 1) {
            NestedWebview tab = TabManager.getCurrentTab();
            NestedWebview main = TabManager.getList().get(0);
            TabManager.setCurrentTab(main);
            TabManager.removeTab(tab);
            TabManager.updateTabView();
            refreshTab();
            if(TabManager.getList().size() >= 20) {
                mBadgeText.setText(":O");
            }else{
                mBadgeText.setText(String.valueOf(TabManager.getList().size()));
            }
        }else if (mPreferences.getBoolean("confirm_close", false)) {
            if (back_pressed + 2000 > System.currentTimeMillis())
                System.exit(0);
            else
                Cardbar.snackBar(getApplicationContext(), getString(R.string.simplicity_close), true).show();


            back_pressed = System.currentTimeMillis();
        } else {
            System.exit(0);
            if(mPreferences.getBoolean("clear_data", false)){
                StaticUtils.deleteCache(SimplicityApplication.getContextOfApplication());
                ArrayList<HistoryItems> listBookmarks = UserPreferences.getHistory();
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
                ArrayList<HistoryItems> listBookmarks = UserPreferences.getHistory();
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
        ifLaunched = false;

    }

    public void loadUrlFromTextBox() {
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





     public void setColor(int color) {
        if(!UserPreferences.getBoolean("dark_mode", false)) {
            color = isIncognito ? ContextCompat.getColor(getMainActivity(), R.color.md_grey_900) : color;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getMainActivity().getWindow().getStatusBarColor(), StaticUtils.colorNav(color));
                colorAnimation.setDuration(100);
                colorAnimation.addUpdateListener(animator -> getMainActivity().getWindow().setStatusBarColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
            }
            int colorFrom = ContextCompat.getColor(getMainActivity(), !isIncognito ? R.color.md_grey_900 : R.color.no_fav);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable backgroundFrom = mToolbar.getBackground();
                if (backgroundFrom instanceof ColorDrawable)
                    colorFrom = ((ColorDrawable) backgroundFrom).getColor();
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
                colorAnimation.setDuration(100);
                colorAnimation.addUpdateListener(animator -> {
                    current_color = StaticUtils.colorNav((int) animator.getAnimatedValue());
                    mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                    mProgress.setBackgroundColor((int) animator.getAnimatedValue());
                    swipeRefreshLayout.setColorSchemeColors((int) animator.getAnimatedValue());
                    View header = mNavigation.getHeaderView(0);
                    LinearLayout back = header.findViewById(R.id.tab_header);
                    TextView tab_label = header.findViewById(R.id.tabs_header);
                    back.setBackgroundColor((int) animator.getAnimatedValue());
                    if (StaticUtils.isColorDark(StaticUtils.lightColor((int) animator.getAnimatedValue()))) {
                        mSearchView.setTextColor(ContextCompat.getColor(getMainActivity(), R.color.white));
                        initialGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getMainActivity(), R.color.gray_500_light));
                        finalGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getMainActivity(), R.color.gray_500_light));
                        mSecure.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.white));
                        vSearch.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.white));
                        mClose.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.white));
                        mHomebutton.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.white));
                        tab_label.setTextColor(ContextCompat.getColor(getMainActivity(), R.color.white));
                        StaticUtils.clearLightStatusBar(getMainActivity());
                    } else {
                        StaticUtils.setLightStatusBar(getMainActivity());
                        mSearchView.setTextColor(ContextCompat.getColor(getMainActivity(), R.color.black));
                        tab_label.setTextColor(Color.parseColor("#222222"));
                        initialGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getMainActivity(), R.color.gray_500));
                        finalGrayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getMainActivity(), R.color.gray_500));
                        mSecure.setColorFilter(null);
                        vSearch.setColorFilter(null);
                        mClose.setColorFilter(null);
                        mHomebutton.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.black));

                    }
                });
                colorAnimation.start();
            }
        }else{
            color = isIncognito ? ContextCompat.getColor(this, R.color.md_grey_900) : color;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.colorNav(color));
                colorAnimation.setDuration(100);
                colorAnimation.addUpdateListener(animator -> getWindow().setStatusBarColor((int) animator.getAnimatedValue()));
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
                    current_color = StaticUtils.colorNav((int) animator.getAnimatedValue());
                    mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                    mProgress.setBackgroundColor((int) animator.getAnimatedValue());
                    bCardView.setBackgroundColor((int) animator.getAnimatedValue());
                    mSearchView.setTextColor(ContextCompat.getColor(this, R.color.white));
                    search_bar.setBackground(ContextCompat.getDrawable(this, R.drawable.search_bar_dark));
                    mSecure.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    vSearch.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mHomebutton.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mJump.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mOverflow.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mAddress.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mTabs.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mAddress.setBackground(ContextCompat.getDrawable(this, R.drawable.search_bar_dark));
                    mRefresh.setColorFilter(ContextCompat.getColor(this, R.color.white));
                    mStop.setColorFilter(ContextCompat.getColor(getMainActivity(), R.color.white));
                });
                colorAnimation.start();
            }
        }

    }

    protected void getBookmarkIcon() {
        try {
            if (bookmarkicon != null && UserPreferences.isStarred(mWebView.getUrl())) {
                bookmarkicon.setColorFilter(ContextCompat.getColor(this, R.color.md_blue_600), PorterDuff.Mode.SRC_IN);
            } else if (bookmarkicon != null) {
                if(UserPreferences.getBoolean("dark_mode", false)) {
                    bookmarkicon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
                }else{
                    bookmarkicon.setColorFilter(ContextCompat.getColor(this, R.color.dark), PorterDuff.Mode.SRC_IN);
                }
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

                    bCardView.setVisibility(GONE);
                    View view = findViewById(R.id.fake_shadow);
                    view.setVisibility(GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if (getIntent() != null) {
            handleIntents(getIntent());
        }
        return super.onCreateOptionsMenu(menu);
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
        findViewById(R.id.sim_new_update_rel).setOnClickListener(onClickListener);

        //relative layouts
        findViewById(R.id.home_rel).setOnClickListener(onClickListener);
        findViewById(R.id.home2_rel).setOnClickListener(onClickListener);
        findViewById(R.id.jump_rel).setOnClickListener(onClickListener);
        findViewById(R.id.search_rel).setOnClickListener(onClickListener);
        findViewById(R.id.tabs_rel).setOnClickListener(onClickListener);
        findViewById(R.id.overflow_rel).setOnClickListener(onClickListener);
        findViewById(R.id.home_rel).setOnClickListener(onClickListener);
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
        mCardView.setVisibility(GONE);
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
        defaultSearch = mPreferences.getString("search_engine", "");
        defaultProvider = mPreferences.getString("search_suggestions", "");
        homepage = mPreferences.getString("homepage", "");
        adBlockerEnabled = mPreferences.getBoolean("no_ads", true);
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(GONE);
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
        try{

        }catch (Exception i){
            i.printStackTrace();

        }
    }

    private void applyHomeButton() {

        defaultSearch = mPreferences.getString("search_engine", "");
        defaultProvider = mPreferences.getString("search_suggestions", "");
        if(mPreferences.getBoolean("v_search", false)){
            findViewById(R.id.voice_button).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.voice_button).setVisibility(GONE);
        }
        adBlockerEnabled = mPreferences.getBoolean("no_ads", true);


    }




    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else if (urlToGrab != null) {
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
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.md_blue_600));
    }

    private AlertDialog createExitDialog() {
        return new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    if (!shortcutNameEditText.getText().toString().isEmpty()) {
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.setAction(Intent.ACTION_MAIN);
                        intent1.setData(Uri.parse(mSearchView.getText().toString()));
                        ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, shortcutNameEditText.getText().toString())
                                .setShortLabel(shortcutNameEditText.getText().toString())
                                .setIcon(IconCompat.createWithBitmap(StaticUtils.createScaledBitmap(StaticUtils.getCroppedBitmap(favoriteIcon), 300, 300)))
                                .setIntent(intent1)
                                .build();
                        ShortcutManagerCompat.requestPinShortcut(MainActivity.this, pinShortcutInfo, null);
                    } else {
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.setAction(Intent.ACTION_MAIN);
                        intent1.setData(Uri.parse(mSearchView.getText().toString()));
                        ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, shortcutNameEditText.getHint().toString())
                                .setShortLabel(shortcutNameEditText.getHint().toString())
                                .setIcon(IconCompat.createWithBitmap(StaticUtils.createScaledBitmap(StaticUtils.getCroppedBitmap(favoriteIcon), 300, 300)))
                                .setIntent(intent1)
                                .build();
                        ShortcutManagerCompat.requestPinShortcut(MainActivity.this, pinShortcutInfo, null);
                    }
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
    public void createDownload() {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.layout_download, null);
        shortcutNameEditText = alertLayout.findViewById(R.id.file_name_edit_text);
        EditText path = alertLayout.findViewById(R.id.file_path_edit_text);
        TextView download_size = alertLayout.findViewById(R.id.down_text);
        TextView download_warn = alertLayout.findViewById(R.id.down_text_warn);
        checker = alertLayout.findViewById(R.id.check_off);
        AlertDialog alertDialog = createDialog();
        alertDialog.setView(alertLayout);
        alertDialog.setCancelable(false);
        alertDialog.show();
        if (UserPreferences.isDangerousFileExtension(filename1)) {
            download_warn.setVisibility(View.VISIBLE);
            download_warn.setText(String.format(getString(R.string.download_warning), filename1));
        }
        shortcutNameEditText.setHint(filename1);
        path.setText(Environment.getExternalStorageDirectory().getPath() + File.separator +Environment.DIRECTORY_DOWNLOADS + File.separator + getString(R.string.app_name) + File.separator + "Simplicity Downloads");
        download_size.setText(getResources().getString(R.string.download_file) + " " +file_size_main);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.md_blue_600));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);
    }

    private AlertDialog createDialog() {
        return new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton(getString(R.string.download), (dialog, which) -> {
                    if (!shortcutNameEditText.getText().toString().isEmpty()) {
                        UserPreferences.putString("file_name_new", shortcutNameEditText.toString());
                    } else {
                        UserPreferences.putString("file_name_new", "");
                    }
                    requestStoragePermission();
                    if(checker.isChecked()){
                        UserPreferences.putBoolean("rename", true);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .setCancelable(true)
                .create();
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


    @Override
    public void onRefresh() {
        if(mWebView != null){
            mWebView.reload();
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 200);
        }
    }


    public class MyWebChromeClient extends WebChromeClient implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        private Activity activity;

        private boolean isVideoFullscreen;
        private FrameLayout videoViewContainer;
        private CustomViewCallback videoViewCallback;

        private AlertDialog customViewDialog;

        MyWebChromeClient(Activity activity) {
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
        /*@Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onCloseWindow(WebView window) {

            super.onCloseWindow(window);
        }*/
        public boolean onBackPressed() {
            if (isVideoFullscreen) {
                onHideCustomView();
                return true;
            } else {
                return false;
            }
        }


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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                mProgress.setVisibility(GONE);
            }
            if (UserPreferences.getBoolean("dark_mode_web", false) && view != null) {
                CSSInjection.injectDarkMode(SimplicityApplication.getContextOfApplication(), view);
                view.setBackgroundColor(Color.parseColor("#202020"));
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
                setColor(Palette.from(icon).generate().getVibrantColor(Palette.from(icon).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav))));
            }else{
                setColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav));

            }
            try {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
                int color = StaticUtils.fetchColorPrimary(MainActivity.this);
                ActivityManager.TaskDescription description;
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    //noinspection deprecation
                    description = new ActivityManager.TaskDescription("Simplicity - " + mWebView.getTitle(), bm, color);
                } else {
                    description = new ActivityManager.TaskDescription("Simplicity - " + mWebView.getTitle(), bm, color);
                }
                setTaskDescription(description);

            }catch (Exception i){
                i.printStackTrace();
            }
            if (mWebView.isCurrentTab()) {
                TabManager.updateTabView();
            }
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            try {
                webViewTitle = title;
                mSearchView.setText(mWebView.getUrl());
                mPreferences.edit().putString("last_page_reminder", mWebView.getUrl()).apply();
                TabManager.updateTabView();
                DateFormat df = new SimpleDateFormat("E - MMM d, yyyy h:mm a", Locale.getDefault());
                String date = df.format(Calendar.getInstance().getTime());
                ArrayList<HistoryItems> listBookmarks = UserPreferences.getHistory();
                HistoryItems bookmark = new HistoryItems();
                bookmark.setTitle(mWebView.getTitle());
                bookmark.setUrl(mWebView.getUrl());
                bookmark.setDate(date);
                listBookmarks.add(bookmark);
                UserPreferences.saveHistory(listBookmarks);
                addSearchInput(mWebView.getUrl());
                savePrefs();
                if(currentUser != null) {
                    uploadToFireBase();
                }

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


    }


    public void startReaderMode(){
        mWebView.loadUrl("javascript:window.simplicity_reader.processReaderMode(document.getElementsByTagName('body')[0].innerText,document.title);");

    }

    /*private class ReaderHandler  {
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
    }*/


    private String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return null;
    }


    public void onAmpPage(boolean isAmped) {
        scrollPosition = mWebView.getScrollY();
        if (isAmped) {
            swipeRefreshLayout.setEnabled(false);
        }else {
            swipeRefreshLayout.setEnabled(false);
        }
    }





    private void setAutoCompleteSource() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.layout_suggest_items, R.id.suggestion_text, history.toArray(new String[0]));
        mSearchView.setAdapter(adapter);
    }

    private void addSearchInput(String input){
        for(String value: history) {
            if (!history.contains(value)) {
                history.add(input);
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

    public void initializeBeHeView() {
        ViewGroup parent;
        List<NestedWebview> list = TabManager.getList();
        if (list == null || !list.isEmpty()) {
            mWebView = TabManager.getCurrentTab();
            parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        } else {
            mWebView = new NestedWebview(getApplicationContext(), this, mProgress,  mSearchView);
            TabManager.addTab(mWebView);
            TabManager.setCurrentTab(mWebView);
        }
        parent = (ViewGroup) mWebView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mWebView.setIsCurrentTab(true);
        TabManager.setCurrentTab(mWebView);
        mWebView = TabManager.getCurrentTab();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        swipe.addView(mWebView);
        swipe.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        root.addView(swipe);
    }


    public void refreshTab() {
        mWebView = TabManager.getCurrentTab();
        mWebView.setLayoutParams(new SwipeRefreshLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        swipe = new RelativeLayout(this);
        ViewGroup group = (ViewGroup) mWebView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        swipe.addView(mWebView);
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root.getChildAt(i) instanceof GridView) {
            } else {
                View view = root.getChildAt(i);
                root.removeView(view);
            }
        }
        root.addView(swipe);
        if (mWebView.getUrl() == null) {
            mSearchView.setText(homepage);
        } else {
            mSearchView.setText(mWebView.getUrl());
        }
        if (mWebView.getFavicon() != null && StaticUtils.isLollipop()) {
            setColor(Palette.from(mWebView.getFavicon()).generate().getVibrantColor(Palette.from(mWebView.getFavicon()).generate().getMutedColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav))));
        }else{
            setColor(ContextCompat.getColor(MainActivity.this, R.color.no_fav));

        }

    }

    public static Activity getMainActivity() {
        return mainActivity;
    }


    public  void colorLightWhite() {
        // Get the URL string.
        String urlString = mSearchView.getText().toString();

        // Highlight the URL according to the protocol.
        if (urlString.startsWith("file://")) {  // This is a file URL.
            // De-emphasize only the protocol.
            mSearchView.getText().setSpan(initialGrayColorSpan, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (urlString.startsWith("content://")) {
            // De-emphasize only the protocol.
            mSearchView.getText().setSpan(initialGrayColorSpan, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {  // This is a web URL.
            // Get the index of the `/` immediately after the domain name.
            int endOfDomainName = urlString.indexOf("/", (urlString.indexOf("//") + 2));

            // Create a base URL string.
            String baseUrl;

            // Get the base URL.
            if (endOfDomainName > 0) {  // There is at least one character after the base URL.
                // Get the base URL.
                baseUrl = urlString.substring(0, endOfDomainName);
            } else {  // There are no characters after the base URL.
                // Set the base URL to be the entire URL string.
                baseUrl = urlString;
            }

            // Get the index of the last `.` in the domain.
            int lastDotIndex = baseUrl.lastIndexOf(".");

            // Get the index of the penultimate `.` in the domain.
            int penultimateDotIndex = baseUrl.lastIndexOf(".", lastDotIndex - 1);

            // Markup the beginning of the URL.
            if (urlString.startsWith("http://")) {  // Highlight the protocol of connections that are not encrypted.
                mSearchView.getText().setSpan(initialGrayColorSpan, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                    mSearchView.getText().setSpan(initialGrayColorSpan, 7, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            } else if (urlString.startsWith("https://")) {  // De-emphasize the protocol of connections that are encrypted.
                if (penultimateDotIndex > 0) {  // There is more than one subdomain in the domain name.
                    // De-emphasize the protocol and the additional subdomains.
                    mSearchView.getText().setSpan(initialGrayColorSpan, 0, penultimateDotIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else {  // There is only one subdomain in the domain name.
                    // De-emphasize only the protocol.
                    mSearchView.getText().setSpan(initialGrayColorSpan, 0, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }

            // De-emphasize the text after the domain name.
            if (endOfDomainName > 0) {
                mSearchView.getText().setSpan(finalGrayColorSpan, endOfDomainName, urlString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }


    private void uploadToFireBase(){
        try{
            if(currentUser != null) {
                final File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String pathToMyAttachedFile = File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator + mAuth.getCurrentUser().getUid() + ".sbh";
                File file = new File(root, pathToMyAttachedFile);
                final Uri uri = Uri.fromFile(file);
                StorageReference proimage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid() + "/simplicity_backup/" + mAuth.getCurrentUser().getUid() + ".sbh");
                if (uri != null) {
                    proimage.putFile(uri).continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return proimage.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ExportUtils.writeToFile(file, this);
                            Log.d("Backed up", "sent history to Firebase");
                        } else {
                            Log.d("Failed backed up", Objects.requireNonNull(task.getException()).toString());
                        }
                    });


                }
            }

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    private void downloadFromFirebase(){
        try{
        StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() +"/simplicity_backup/"+ currentUser.getUid() + ".sbh");
        File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator, currentUser.getUid() + ".sbh");

        proimage.getFile(bh).addOnSuccessListener(taskSnapshot -> {
            Log.d("Downloaded", "got backup from Firebase");
            ExportUtils.readFromFile(bh, this);
        }).addOnFailureListener(exception ->
                Log.d("Failed from Firebase", Objects.requireNonNull(exception).toString()));

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    protected void addWidget(){
    Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
    startActivity(intent);
    }





}
