package com.creativetrends.app.simplicity.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.creativetrends.simplicity.app.R;

import androidx.annotation.CheckResult;
import androidx.appcompat.widget.AppCompatTextView;

public class Cardbar {

    public static @CheckResult
        Toast snackBar(Context context, CharSequence message_to_show, boolean duration) {
        @SuppressLint("InflateParams")
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_snackbar, null);
        AppCompatTextView message = view.findViewById(R.id.message);
        message.setText(message_to_show);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        if (duration) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return toast;
    }

}