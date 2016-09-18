package com.creativetrends.simplicity.app.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;

import com.creativetrends.simplicity.app.R;

import static android.graphics.Color.parseColor;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;


final class ThemeUtils {


  static final int FALLBACK_COLOR = parseColor("#00bcd4");

  private ThemeUtils() {

  }

  static boolean isAtLeastL() {
    return SDK_INT >= LOLLIPOP;
  }

  @SuppressWarnings("ResourceType")
  @TargetApi(LOLLIPOP)
  static int resolveAccentColor(Context context) {
    Theme theme = context.getTheme();


    int attr = isAtLeastL() ? android.R.attr.colorAccent : R.attr.colorAccent;
    TypedArray typedArray = theme.obtainStyledAttributes(new int[] { attr, R.attr.mp_colorAccent });

    int accentColor = typedArray.getColor(0, FALLBACK_COLOR);
    accentColor = typedArray.getColor(1, accentColor);
    typedArray.recycle();

    return accentColor;
  }

}
