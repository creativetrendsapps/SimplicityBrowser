package com.creativetrends.simplicity.app.preferences;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

import com.creativetrends.simplicity.app.R;


public class SwitchPreference extends CheckBoxPreference {

    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.pref_widget_switch);
    }

}