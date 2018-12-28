package com.creativetrends.app.simplicity.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 4/2/2018.
 */
public class SimpleAutoComplete extends AppCompatAutoCompleteTextView {
    private View.OnFocusChangeListener mFocusChangeListener;
    private int mPositionX;

    public SimpleAutoComplete(Context context) {
        super(context);
    }

    public SimpleAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Override View's focus change listener handling so that we're able to
    // call it before the actual focus change handling, in particular before
    // the IME is fired up.
    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mFocusChangeListener = l;
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return mFocusChangeListener;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (mFocusChangeListener != null) {
            mFocusChangeListener.onFocusChange(this, gainFocus);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    private static LinearGradient getGradient(float widthEnd, float fadeStart,
                                              float stopStart, float stopEnd, int color) {
        return new LinearGradient(0, 0, widthEnd, 0,
                new int[] { color, Color.TRANSPARENT, color, color, Color.TRANSPARENT },
                new float[] { 0, fadeStart, stopStart, stopEnd, 1f }, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        mPositionX = x;
        requestLayout();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float lineWidth = getLayout().getLineWidth(0);
        float width = getMeasuredWidth();

        if (getText() == null || getText().length() == 0 || lineWidth <= width) {
            getPaint().setShader(null);
            super.onDraw(canvas);
            return;
        }

        int textColor = getCurrentTextColor();
        float widthEnd = width + mPositionX;
        float percent = (int) (width * 0.2);

        float fadeStart = mPositionX / widthEnd;

        float stopStart = mPositionX > 0 ? ((mPositionX + percent) / widthEnd) : 0;
        float stopEnd = (widthEnd - (lineWidth > widthEnd ? percent : 0)) / widthEnd;
        getPaint().setShader(getGradient(widthEnd, fadeStart, stopStart, stopEnd, textColor));
        super.onDraw(canvas);
    }
}