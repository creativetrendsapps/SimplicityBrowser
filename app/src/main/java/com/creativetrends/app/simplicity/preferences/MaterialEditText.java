package com.creativetrends.app.simplicity.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativetrends.simplicity.app.R;

import java.lang.reflect.Method;

import static android.graphics.Color.parseColor;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoMedium;
import static com.creativetrends.app.simplicity.preferences.Typefaces.getRobotoRegular;


@SuppressLint("NewApi")
public class MaterialEditText extends EditTextPreference {
    private TextView titleView;
    TextView summaryView;

    ImageView imageView;
    View imageFrame;


    private int iconResId;
    private Drawable icon;
    private int mColor = 0;
    static final int FALLBACK_COLOR = parseColor("#00bcd4");
    private AlertDialog mDialog;
    private EditText mEditText;
    private final AlertDialog.OnClickListener callback = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String value = mEditText.getText().toString();
            if (callChangeListener(value) && isPersistent()) {
                setText(value);
            }
        }
    };

    public MaterialEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
        int fallback = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fallback = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent}).getColor(0, FALLBACK_COLOR);
        }
        mColor = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorAccent}).getColor(0, fallback);

        mEditText = new AppCompatEditText(context, attrs);
        mEditText.setEnabled(true);
    }

    public MaterialEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, null, 0, 0);
    }

    private static ColorStateList createEditTextColorStateList(Context context, int color) {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        states[0] = new int[]{-android.R.attr.state_enabled};
        colors[0] = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorControlNormal}).getColor(0, 0);
        states[1] = new int[]{-android.R.attr.state_pressed, -android.R.attr.state_focused};
        colors[1] = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorControlNormal}).getColor(0, 0);
        states[2] = new int[]{};
        colors[2] = color;
        return new ColorStateList(states, colors);
    }

    @Override
    protected void onAddEditTextToDialogView(@NonNull View view, @NonNull EditText editText) {
        if (editText.getParent() != null) {
            ((ViewGroup) mEditText.getParent()).removeView(editText);
        }
        ((ViewGroup) view).addView(
                editText,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onBindDialogView(@NonNull View view) {
        mEditText.setText("");
        if (getText() != null) {
            mEditText.setText(getText());
        }
        ViewParent oldParent = mEditText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(mEditText);
            }
            onAddEditTextToDialogView(view, mEditText);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public Dialog getDialog() {
        return mDialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void showDialog(Bundle state) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext())
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .setPositiveButton(getPositiveButtonText(), callback)
                .setNegativeButton(getNegativeButtonText(), null)
                .setOnDismissListener(this);

        @SuppressLint("InflateParams") View layout = LayoutInflater.from(getContext()).inflate(R.layout.pref_layout_edittext, null);
        onBindDialogView(layout);

        ColorStateList editTextColorStateList = createEditTextColorStateList(mEditText.getContext(), mColor);
        if (mEditText instanceof AppCompatEditText) {
            ((AppCompatEditText) mEditText).setSupportBackgroundTintList(editTextColorStateList);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditText.setBackgroundTintList(editTextColorStateList);
        }

        TextView message = layout.findViewById(android.R.id.message);
        if (getDialogMessage() != null && getDialogMessage().toString().length() > 0) {
            message.setVisibility(View.VISIBLE);
            message.setText(getDialogMessage());
        } else {
            message.setVisibility(View.GONE);
        }
        mBuilder.setView(layout);

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

        mDialog = mBuilder.create();
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        requestInputMethod(mDialog);

        mDialog = mBuilder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.show();
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
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

            if (icon == null && iconResId > 0) {
                icon = getContext().getResources().getDrawable(iconResId);
            }
            imageView = view.findViewById(android.R.id.icon);
            imageView.setImageDrawable(icon);
            imageView.setVisibility(icon != null ? VISIBLE : GONE);

            imageFrame = view.findViewById(android.R.id.icon_frame);
            imageFrame.setVisibility(icon != null ? VISIBLE : GONE);

        } catch (NullPointerException ignored) {
        } catch (Exception i) {
            i.printStackTrace();
        }
    }
}

