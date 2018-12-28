package com.creativetrends.app.simplicity.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.creativetrends.simplicity.app.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoMedium;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoRegular;


public class MaterialList extends ListPreference {

    private Context mContext;
    private AlertDialog mDialog;

    TextView titleView;
    TextView summaryView;

    ImageView imageView;
    View imageFrame;

    private int iconResId;
    private Drawable icon;

    public MaterialList(Context context) {
        super(context);
        mContext = context;
        init(context, null, 0, 0);
    }

    public MaterialList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs, 0, 0);
    }

    public MaterialList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("PrivateResource")
    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        if (mDialog != null) {
            ArrayList<HashMap<String, CharSequence>> listItems = new ArrayList<>();
            for (CharSequence entry : entries) {
                HashMap<String, CharSequence> map = new HashMap<>();
                map.put("item", entry);
                listItems.add(map);
            }
            mDialog.getListView().setAdapter(new SimpleAdapter(
                    mContext,
                    listItems,
                    R.layout.select_dialog_singlechoice_material,
                    new String[]{"item"},
                    new int[]{android.R.id.text1}
            ));
        }
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @Override
    protected void showDialog(Bundle state) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException("MaterialList requires an entries array and an entryValues array.");
        }

        int preselect = findIndexOfValue(getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(getDialogTitle())
                .setMessage(getDialogMessage())
                .setIcon(getDialogIcon())
                .setNegativeButton(getNegativeButtonText(), null)
                .setSingleChoiceItems(getEntries(), preselect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i >= 0 && getEntryValues() != null) {
                            String value = getEntryValues()[i].toString();
                            if (callChangeListener(value) && isPersistent()) {
                                setValue(value);
                            }
                        }
                        dialogInterface.dismiss();
                    }
                });

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        } else {
            builder.setMessage(getDialogMessage());
        }

        PreferenceManager pm = getPreferenceManager();
        try {
            Method method = pm.getClass().getDeclaredMethod(
                    "registerOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDialog = builder.create();
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.show();
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, new int[]{android.R.attr.icon}, defStyleAttr,
                        defStyleRes);
        iconResId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
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

        } catch (NullPointerException ignored) {
        } catch (Exception i) {
            i.printStackTrace();
        }
    }
}

