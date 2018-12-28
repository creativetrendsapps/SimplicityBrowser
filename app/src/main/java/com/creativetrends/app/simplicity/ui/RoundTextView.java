package com.creativetrends.app.simplicity.ui;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 5/18/2018.
 */
public class RoundTextView extends AppCompatTextView {

    public RoundTextView(Context context) {
        super(context);
    }

    public RoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int r = Math.max(w,h);

        setMeasuredDimension(r, r);

    }
}