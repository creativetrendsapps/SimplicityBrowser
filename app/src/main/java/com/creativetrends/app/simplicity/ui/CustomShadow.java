package com.creativetrends.app.simplicity.ui;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by Creative Trends Apps (Jorell Rutledge) 3/26/2018.
 */

public class CustomShadow extends ViewOutlineProvider {

    private int roundCorner;

    public CustomShadow(int round) {
        roundCorner = round;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), roundCorner);
    }
}