package com.akiniyalocts.minor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by anthonykiniyalocts on 5/13/16.
 */
public class MinorView extends FrameLayout{
    public MinorView(Context context) {
        super(context);
    }

    public MinorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MinorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MinorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        init(context, attrs);
    }


    private int selectedTitleColor = -1;

    private int titleColor = -1;

    private TextView titleTextView;

    private FrameLayout notificationView;

    private FrameLayout tabSelected;

    private TextView notificationTextView;

    private LayoutParams params;

    int regInDP = 8;

    int rightInDP = 16;

    int marginInDp = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, regInDP, getResources()
                    .getDisplayMetrics());

    int rihgtMInDp = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, rightInDP, getResources()
                    .getDisplayMetrics());

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MinorView,
                0, 0
        );


        // get attributes
        int iconViewRes = a.getResourceId(R.styleable.MinorView_minor_icon_view, -1);

        String title = a.getString(R.styleable.MinorView_minor_title);

        titleColor = a.getColor(R.styleable.MinorView_minor_title_text_color, ContextCompat.getColor(context,android.R.color.primary_text_light));

        selectedTitleColor = a.getColor(R.styleable.MinorView_minor_title_selected_color, -1);
        // Build layout
        LinearLayout minorLayout = new LinearLayout(getContext());

        minorLayout.setOrientation(LinearLayout.VERTICAL);


        minorLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        minorLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        minorLayout.setPadding(5, 5, 2, 5);


        this.setClickable(true);


        // Add icon and title
        if(iconViewRes != -1){
            View iconView = inflate(context, iconViewRes, null);
            minorLayout.addView(iconView, getLayoutParamsForIconView());
        }
        else {
            throw new IllegalArgumentException("You must specify a view for MinorView to inflate as an icon. Use app:minor_icon_view=@layout/your_view");
        }

        if(title != null){
            titleTextView = new TextView(context);
            titleTextView.setLayoutParams(getLayoutParamsForIconView());
            titleTextView.setText(title);
            titleTextView.setTextSize(10);
            titleTextView.setEllipsize(TextUtils.TruncateAt.END);
            titleTextView.setSingleLine(true);
            titleTextView.setTextColor(titleColor);
            titleTextView.setClickable(false);
            titleTextView.setFocusable(false);
            titleTextView.setPadding(0, -8, 0, 0);


            if(a.getBoolean(R.styleable.MinorView_minor_selected, false)){
                if(selectedTitleColor != -1){
                    titleTextView.setTextColor(selectedTitleColor);
                }
            }

            titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            minorLayout.addView(titleTextView);
        }



        minorLayout.requestLayout();

        this.addView(minorLayout);

        initNotificationView();
        initTabView();

        a.recycle();

    }

    private LayoutParams getLayoutParamsForIconView(){
        if(params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        return params;
    }

    public void selected(){
        if(tabSelected != null){
            tabSelected.setVisibility(VISIBLE);
        }

        }



    private void initTabView(){
        if(tabSelected == null) {
            tabSelected = (FrameLayout) inflate(getContext(), R.layout.tab_select, null);

        }

        LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        params.setMargins(0,8,0,0);
        tabSelected.setPadding(0, 5, 0, 0);

        tabSelected.setLayoutParams(params);


        this.addView(tabSelected);
            tabSelected.setVisibility(INVISIBLE);
            invalidate();

    }


    private void initNotificationView(){
        if(notificationView == null) {
            notificationView = (FrameLayout) inflate(getContext(), R.layout.minor_notification, null);

            if (notificationTextView == null) {
                notificationTextView = (TextView) notificationView.findViewById(R.id.minor_notification_text);
            }

        }

        LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.END);

        params.setMargins(marginInDp, marginInDp, rihgtMInDp, marginInDp);
        notificationView.setPadding(5, 5, 5, 5);
        notificationView.setLayoutParams(params);


        this.addView(notificationView);

        notificationView.setVisibility(INVISIBLE);

        invalidate();
    }

    public void addNotification(final int notificationCount){

        if(notificationView != null){
            notificationView.setVisibility(VISIBLE);
        }

        if(notificationCount <= 99) {
            if(notificationTextView != null) {
                notificationTextView.setText(String.valueOf(notificationCount));
            }
        }
        else {
            if(notificationTextView != null) {
                notificationTextView.setText("*");
            }
        }

    }


    public void removeNotification(final int notificationCount){

        if(notificationView != null){
            notificationView.setVisibility(INVISIBLE);
        }

        if(notificationCount <= 0) {
            if (notificationTextView != null) {
                notificationTextView.setText(String.valueOf(notificationCount));
            }
        }
    }

    public void unselected(){
        if(tabSelected != null){
            tabSelected.setVisibility(GONE);
        }

    }





}
