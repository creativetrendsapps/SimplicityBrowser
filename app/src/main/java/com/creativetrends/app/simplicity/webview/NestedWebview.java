package com.creativetrends.app.simplicity.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;

import com.anthonycr.progress.AnimatedProgressBar;
import com.creativetrends.app.simplicity.SimplicityApplication;
import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.app.simplicity.interfaces.ImageInterface;
import com.creativetrends.app.simplicity.ui.SimpleAutoComplete;
import com.creativetrends.app.simplicity.utils.ReaderHandler;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;

import java.util.Locale;
import java.util.Map;


/**
 * Created by Creative Trends Apps.
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class NestedWebview extends WebView implements NestedScrollingChild, NestedScrollingParent {
	private String TEXT = "1";
	private int searchEngine = 1;
	private AnimatedProgressBar P_BAR;
	private boolean ico ;
	private boolean FOCUS;
	AppCompatActivity WEB_ACTIVITY;
	SimpleAutoComplete text;
	private String PAGE_TITLE;
	public String found = "";
	private static final int API = Build.VERSION.SDK_INT;
	private static final int INVALID_POINTER = -1;
	private static final String TAG = "SimplicityView";

    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = INVALID_POINTER;
	private int mNestedYOffset;
	@SuppressWarnings("deprecation")
    private ScrollerCompat mScroller;
    private SharedPreferences prefs;

    private int mLastY;
    private int[] mScrollOffset = new int[2];
    private int[] mScrollConsumed = new int[2];
    private int mNestedOffsetY;
    private NestedScrollingChildHelper mChildHelper;


	private NestedWebview.OnScrollChangedCallback mOnScrollChangedCallback;
    public interface OnScrollChangedCallback {
        void onScroll(int i, int i2);
    }

    public NestedWebview(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public NestedWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        initScrollView();
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    private void initScrollView() {
        //noinspection deprecation
        mScroller = ScrollerCompat.create(getContext(), null);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        int mTouchSlop = configuration.getScaledTouchSlop();
        int mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        int mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final MotionEvent event = MotionEvent.obtain(ev);
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0;
        }

        final int eventY = (int) event.getY();
        event.offsetLocation(0, mNestedOffsetY);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - eventY;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    event.offsetLocation(0, -mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }

                mLastY = eventY - mScrollOffset[1];

                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    mLastY -= mScrollOffset[1];
                    event.offsetLocation(0, mScrollOffset[1]);
                    mNestedOffsetY += mScrollOffset[1];
                }
                break;

            case MotionEvent.ACTION_DOWN:
                mLastY = eventY;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                //if(PreferencesUtility.getBoolean("animate_tabs", false)) {
                //  if(MainActivity.viewPager != null && MainActivity.viewPager.getCurrentItem() != 3) {
                //MainActivity.viewPager.disableScroll(true);
                // }
                //}
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                break;

            default:
                // We don't care about_settings other touch events
        }

        // Execute event handler from parent class in all cases
        boolean eventHandled = super.onTouchEvent(event);

        // Recycle previously obtained event
        event.recycle();

        return eventHandled;
    }

    int getScrollRange() {
        return computeVerticalScrollRange();
    }

    private void endDrag() {
        boolean mIsBeingDragged = false;

        recycleVelocityTracker();
        stopNestedScroll();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        //noinspection deprecation
        final int pointerIndex = (ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK)
                >> MotionEventCompat.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            int mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void flingWithNestedDispatch(int velocityY) {
        final int scrollY = getScrollY();
        final boolean canFling = (scrollY > 0 || velocityY > 0)
                && (scrollY < getScrollRange() || velocityY < 0);
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);
            if (canFling) {
                fling(velocityY);
            }
        }
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int bottom = getChildAt(0).getHeight();

            mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0,
                    Math.max(0, bottom - height), 0, height / 2);

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_NONE;
    }

    public void disableGestures(boolean disable) {
        boolean areGesturesEnabled;
        if (disable) {
            areGesturesEnabled = false;
        } else {
            areGesturesEnabled = true;
        }
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        GestureDetector gestureDetector1 = gestureDetector;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l, t);
        }
    }


    public void setOnScrollChangedCallback(NestedWebview.OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }
	/*
	* Public constructors of BeHeView
	 */

    public NestedWebview(Context context) {
        super(context);
        mScrollOffset = new int[2];
        mScrollConsumed = new int[2];
        TEXT = "1";
        searchEngine = 1;
        found = "";
        Paint mPaint = new Paint();
    }

	public NestedWebview(Context c, Activity activity){
		super(c);
        mScrollOffset = new int[2];
        mScrollConsumed = new int[2];
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
		WEB_ACTIVITY = (AppCompatActivity) activity;
	}
	public NestedWebview(Context context, AppCompatActivity activity, AnimatedProgressBar pBar, final SimpleAutoComplete txt)  {
		super(activity);
        mScrollOffset = new int[2];
        mScrollConsumed = new int[2];
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
		P_BAR = pBar;
		WEB_ACTIVITY = activity;
		text = txt;
        setWebChromeClient(MainActivity.mWebChromeClient);
        setWebViewClient(new SimplicityWebViewClient(text, WEB_ACTIVITY,this));
        setDownloadListener(MainActivity.mDownloadlistener);
	    //setDownloadListener(new CiobanDownloadListener(WEB_ACTIVITY, this));
        setOnLongClickListener(((MainActivity) MainActivity.getMainActivity()).onLongClickListener);
        initializeSettings();
	}



	public void destroy(){
	  ViewGroup parent = (ViewGroup) this.getParent();
	  if( parent != null){
		  parent.removeView(this);
	  }
	  this.stopLoading();
	  this.onPause();
	  this.clearHistory();
	  this.clearCache(true);
	  this.setVisibility(View.GONE);
	  this.removeAllViews();
	  super.destroy();
	}
    public void loadHomepage() {
        if (UserPreferences.getBoolean("remember_page", false)) {
            loadUrl(UserPreferences.getString("last_page_reminder", ""));
        } else if (!UserPreferences.getBoolean("remember_page", false)) {
            loadUrl(UserPreferences.getString("homepage", ""));
        }
    }
	public void setDesktop(){
		getSettings().setDisplayZoomControls(true);
		getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
		setInitialScale(-10);
		getSettings().setBuiltInZoomControls(true);
		reload();
	}
	public void setMobile(){
		getSettings().setDisplayZoomControls(false);
		getSettings().setBuiltInZoomControls(false);
		if (API >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(WEB_ACTIVITY));
		} else {
			getSettings().setUserAgentString("Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev> ");
		}
		setInitialScale(0);
		reload();
	}

    public void setTitle(){
        if(getUrl() != null){
            text.setText(getUrl());
        }

    }

    public boolean isCurrentTab(){
		return FOCUS;
	}
    public void setIsCurrentTab(boolean focus){
		FOCUS = focus;
	}
    public void setSearchEngine(int engine){
		searchEngine = engine;
	}
    public Activity getActivity(){
		return WEB_ACTIVITY;
	}
    public void setNewParams(SimpleAutoComplete txt, AnimatedProgressBar pBar, AppCompatActivity activity){
		text = txt;
		P_BAR = pBar;
		WEB_ACTIVITY = activity;
        setWebChromeClient(MainActivity.mWebChromeClient);
		setWebViewClient(new SimplicityWebViewClient(text,WEB_ACTIVITY,this));
        setOnLongClickListener(((MainActivity) MainActivity.getMainActivity()).onLongClickListener);
        setDownloadListener(MainActivity.mDownloadlistener);
        initializeSettings();
	}
    public void setMAtch(String t){
		found = t;
	}
    public String hasAnyMatches(){
		return found;
	}

    @Override
    public void loadUrl(String url){
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders){
        super.loadUrl(url, additionalHttpHeaders);
    }

    public static void getUserAgent(Context context) {
        WebSettings.getDefaultUserAgent(context);
    }


    @SuppressLint({"SetJavaScriptEnabled"})
    public void initializeSettings(){
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        String lang = Locale.getDefault().getDisplayLanguage();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(NestedWebview.this, true);
        WebSettings mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        if(isTablet){
            mWebSettings.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Simplicity/57.0.3098.116");
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
        }else{
            mWebSettings.setUserAgentString(null);
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
        }
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        if (UserPreferences.getBoolean("enable_location", false)) {
            mWebSettings.setGeolocationEnabled(true);
        } else {
            mWebSettings.setGeolocationEnabled(false);
        }
        if(UserPreferences.getBoolean("lite_mode", false)){
            mWebSettings.setLoadsImagesAutomatically(false);
        }else{
            mWebSettings.setLoadsImagesAutomatically(true);
        }
        mWebSettings.setAllowFileAccessFromFileURLs(true);
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setTextZoom(Integer.parseInt(UserPreferences.getInstance(WEB_ACTIVITY).getFont()));
        addJavascriptInterface(new ReaderHandler(SimplicityApplication.getContextOfApplication(), MainActivity.getMainActivity()), "simplicity_reader");
        if(UserPreferences.getBoolean("facebook_photos", false)){
            addJavascriptInterface(new ImageInterface(WEB_ACTIVITY), "Photos");
        }

    }

}





