package com.creativetrends.app.simplicity.preferences;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.creativetrends.simplicity.app.R;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoMedium;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoRegular;


public class SwitchPreference extends CheckBoxPreference {
    TextView titleView;
    TextView summaryView;
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
        setWidgetLayoutResource(R.layout.simple_switchpreference);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onBindView(View view) {
        super.onBindView(view);
        try {

            CharSequence title = getTitle();
            titleView = view.findViewById(android.R.id.title);
            titleView.setText(title);
            titleView.setVisibility(!isEmpty(title) ? VISIBLE : GONE);
            titleView.setTypeface(getRobotoMedium(getContext()));

            CharSequence summary = getSummary();
            summaryView = view.findViewById(android.R.id.summary);
            summaryView.setText(summary);
            summaryView.setVisibility(!isEmpty(summary) ? VISIBLE : GONE);
            summaryView.setTypeface(getRobotoRegular(getContext()));

        }catch  (NullPointerException ignored){
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

}