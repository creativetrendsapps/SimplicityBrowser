package com.creativetrends.app.simplicity.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.app.simplicity.utils.PrefManager;
import com.creativetrends.app.simplicity.utils.StaticUtils;
import com.creativetrends.simplicity.app.R;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    TextView[] dots;
    private int[] layouts;
    private PrefManager prefManager;
    private FloatingActionButton button;
    private static final int REQUEST_STORAGE = 1;
    int[] colorsActive;
    int[] colorsInactive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus();
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        button = findViewById(R.id.img_next);

        if(StaticUtils.isMarshmallow()) {
            layouts = new int[]{
                    R.layout.welcome_slide1,
                    R.layout.welcome_slide2,
                    R.layout.welcome_slide3,
                    R.layout.welcome_slide4,
                    R.layout.welcome_slide5,
            };
        }else{
            layouts = new int[]{
                    R.layout.welcome_slide1,
                    R.layout.welcome_slide2,
                    R.layout.welcome_slide3,
                    R.layout.welcome_slide4,
            };
        }


        addBottomDots(0);


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        button.setOnClickListener(v -> {

            int current = getItem(+1);
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                if(StaticUtils.isMarshmallow()){
                    requestStoragePermission();
                }else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        if(StaticUtils.isMarshmallow()) {
            colorsActive = getResources().getIntArray(R.array.array_dot_active_m);
            colorsInactive = getResources().getIntArray(R.array.array_dot_inactive_m);
        }else{
            colorsActive = getResources().getIntArray(R.array.array_dot_active);
            colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        }

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));
            } else {
                dots[i].setText(Html.fromHtml("&#8226;"));
            }
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                button.setImageDrawable(ContextCompat.getDrawable(WelcomeActivity.this, R.drawable.ic_check));
            } else {
                button.setImageDrawable(ContextCompat.getDrawable(WelcomeActivity.this, R.drawable.ic_arrow_forward));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus() {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        winParams.flags |= bits;
        win.setAttributes(winParams);
    }


    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        }
    }

    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   launchHomeScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
