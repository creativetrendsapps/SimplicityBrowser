package com.creativetrends.simplicity.app.ui;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/** Created by Creative Trends Apps on 10/24/2016. */

public class BottomNavigationViewHelper {
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(true);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                AppCompatImageView icon = (AppCompatImageView) item.getChildAt(0);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) icon.getLayoutParams();
                params.gravity = Gravity.CENTER;
                item.setShiftingMode(false);
                //item.setChecked(item.getItemData().isChecked());
                item.setCheckable(false);
                item.setChecked(false);
                item.setTitle(null);
            }
        } catch (NoSuchFieldException e) {
            Log.e("", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("", "Unable to change value of shift mode");
        }
    }
}