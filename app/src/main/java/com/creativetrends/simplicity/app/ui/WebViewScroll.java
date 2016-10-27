package com.creativetrends.simplicity.app.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.creativetrends.simplicity.app.R;
import com.creativetrends.simplicity.app.webview.SimplicityChromeClient;


@SuppressWarnings("deprecation")
public class WebViewScroll extends WebView {

    private final int[] scrollOffset = new int[2];
    private final int[] scrollConsumed = new int[2];
    private int nestedOffsetY;
    private NestedScrollingChildHelper childHelper;
    @SuppressWarnings("unused")
    private float startY, startX, endX;
    private OnScrollChangedCallback mOnScrollChangedCallback;


    private SimplicityChromeClient webChromeClient;

    private SharedPreferences prefs;

    public WebViewScroll(Context context) {
        super(context);
        init();
    }

    public WebViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebViewScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        childHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    public void setCanScrollVertically(boolean canScrollVertically) {
        boolean canScrollVertically1 = canScrollVertically;
    }

    public interface OnScrollChangedCallback {
        void onScroll(int i, int i2);
    }


    @SuppressWarnings("unused")
    public boolean isVideoFullscreen() {
        return webChromeClient != null && webChromeClient.isVideoFullscreen();
    }


    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client) {
        if (client instanceof SimplicityChromeClient)
            this.webChromeClient = (SimplicityChromeClient) client;
        super.setWebChromeClient(client);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        MotionEvent event = MotionEvent.obtain(ev);
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            nestedOffsetY = 0;
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        float eventY = event.getY();
        event.offsetLocation(0, nestedOffsetY);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                float deltaY = startY - eventY;

                if (prefs.getBoolean("scroll_toolbar", true)) {
                    if (dispatchNestedPreScroll(0, (int) deltaY, scrollConsumed, scrollOffset)) {
                        deltaY -= scrollConsumed[1];
                        startY = eventY - scrollOffset[1];
                        event.offsetLocation(0, -scrollOffset[1]);
                        nestedOffsetY += scrollOffset[1];
                    }

                    if (dispatchNestedScroll(0, scrollOffset[1], 0, (int) deltaY, scrollOffset)) {
                        event.offsetLocation(0, scrollOffset[1]);
                        nestedOffsetY += scrollOffset[1];
                        startY -= scrollOffset[1];
                    }
                }

                //if (prefs.getBoolean("gestures", true)) {
                    //if (Math.abs(deltaY) < Math.abs(startX - event.getX())) {
                        //float scrollX = startX - event.getX();
                        //if ((canGoForward() && scrollX > 0) || (canGoBack() && scrollX < 0))
                            //setX(-scrollX / 5);

                        //getRootView().findViewById(R.id.next).setPressed(false);
                        //getRootView().findViewById(R.id.previous).setPressed(false);
                    //}
                //}
                break;
            case MotionEvent.ACTION_DOWN:
                if (prefs.getBoolean("scroll_toolbar", true)) {
                    startY = eventY;
                    startX = event.getX();
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                } else {
                    startY = eventY;
                    startX = event.getX();
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endX = event.getX();
                animate().x(0).setDuration(0).start();

                //if (prefs.getBoolean("gestures", true)) {
                    //if (startX - endX > 600 && canGoForward()) goForward();
                    //else if (startX - endX < -600 && canGoBack()) goBack();
                //}

                stopNestedScroll();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        WebViewScroll webView = (WebViewScroll) getRootView().findViewById(R.id.mainWebView);
        @SuppressWarnings("unused")
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l, t);
        }
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }
}
