package com.creativetrends.simplicity.app.ui;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**Created by Creative Trends Apps on 1/1/2017.*/

public class CustomBottomNavigation extends BottomNavigationView {

    public CustomBottomNavigation(Context context) {
        super(context);
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        centerMenuIcon();
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        centerMenuIcon();
    }

    private void centerMenuIcon() {
        BottomNavigationMenuView menuView = getBottomMenuView();

        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);

                menuItemView.setChecked(false);
                menuItemView.setCheckable(false);


                AppCompatImageView icon = (AppCompatImageView) menuItemView.getChildAt(0);

                FrameLayout.LayoutParams params = (LayoutParams) icon.getLayoutParams();
                params.gravity = Gravity.CENTER;

                menuItemView.setShiftingMode(false);
            }
        }
    }

    private BottomNavigationMenuView getBottomMenuView() {

        Object menuView = null;
        try {

            Field field = BottomNavigationView.class.getDeclaredField("mMenuView");
            field.setAccessible(true);
            menuView = field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return (BottomNavigationMenuView) menuView;
    }


    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(true);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
            }
        } catch (NoSuchFieldException e) {
            Log.e("", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("", "Unable to change value of shift mode");
        }
    }

}