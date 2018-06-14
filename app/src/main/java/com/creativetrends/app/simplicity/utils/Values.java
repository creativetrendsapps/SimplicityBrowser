package com.creativetrends.app.simplicity.utils;

import android.content.Context;
import android.view.ViewConfiguration;

import java.util.HashMap;

/**
 * Created by Creative Trends Apps.
 */

class Values {
    private Context context;

    Values(Context callingContext) {
        context = callingContext;
    }


    HashMap<String, Integer> getValuesForSwipe() {
        HashMap<String, Integer> swipeValues = new HashMap<>();
        ViewConfiguration vc = ViewConfiguration.get(context);
        swipeValues.put("swipeMinDistance", vc.getScaledPagingTouchSlop());
        swipeValues.put("swipeThresholdVelocity", vc.getScaledMinimumFlingVelocity());
        swipeValues.put("swipeMaxOffPath", vc.getScaledMinimumFlingVelocity());
        return swipeValues;
    }
}