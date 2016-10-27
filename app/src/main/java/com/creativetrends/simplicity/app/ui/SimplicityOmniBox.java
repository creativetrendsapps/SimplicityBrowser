package com.creativetrends.simplicity.app.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**Created by Family on 9/24/2016.*/

public class SimplicityOmniBox extends AutoCompleteTextView {

    private int myThreshold;

    public SimplicityOmniBox(Context context) {
        super(context);
    }

    public SimplicityOmniBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SimplicityOmniBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }
        myThreshold = threshold;
    }

    public boolean enoughToFilter() {
        return true;
    }

    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {

        super.performFiltering("", 67);
        // TODO Auto-generated method stub
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

    }

    protected void performFiltering(CharSequence text, int keyCode) {
        // TODO Auto-generated method stub
        super.performFiltering(text, keyCode);
    }

    public int getThreshold() {
        return myThreshold;
    }
}