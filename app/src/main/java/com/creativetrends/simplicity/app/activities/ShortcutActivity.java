package com.creativetrends.simplicity.app.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.creativetrends.simplicity.app.R;

public class ShortcutActivity extends AppCompatDialogFragment {
    // The public interface is used to send information back to the activity that called ShortcutActivity.
    public interface CreateHomeScreenSchortcutListener {
        void onCreateHomeScreenShortcutCancel(DialogFragment dialog);

        void onCreateHomeScreenShortcutCreate(DialogFragment dialog);
    }

    private CreateHomeScreenSchortcutListener buttonListener;

    // Check to make sure that the activity that called ShortcutActivity implements both listeners.
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            buttonListener = (CreateHomeScreenSchortcutListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CreateHomeScreenShortcutListener.");
        }
    }

    // onCreateDialog requires @NonNull.
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a drawable version of the favorite icon.
        Drawable favoriteIconDrawable = new BitmapDrawable(getResources(), MainActivity.favoriteIcon);

        // Use AlertDialog.Builder to create the AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater customDialogInflater = getActivity().getLayoutInflater();
        alertDialogBuilder.setTitle(R.string.shortcut_name);
        alertDialogBuilder.setIcon(favoriteIconDrawable);
        alertDialogBuilder.setView(customDialogInflater.inflate(R.layout.activity_shortcut, null));
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonListener.onCreateHomeScreenShortcutCancel(ShortcutActivity.this);
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonListener.onCreateHomeScreenShortcutCreate(ShortcutActivity.this);
            }
        });

        // Assign the alertDialogBuilder to an AlertDialog.
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Show the keyboard when the dialog is displayed on the screen.
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // We need to show alertDialog before we can setOnKeyListener below.
        alertDialog.show();

        EditText shortcutNameEditText = (EditText) alertDialog.findViewById(R.id.shortcutNameEditText);

        // Allow the "enter" key on the keyboard to create the shortcut.
        assert shortcutNameEditText != null;
        shortcutNameEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button, select the PositiveButton "Create".
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Trigger the create listener.
                    buttonListener.onCreateHomeScreenShortcutCreate(ShortcutActivity.this);

                    // Manually dismiss alertDialog.
                    alertDialog.dismiss();

                    // Consume the event.
                    return true;
                } else {
                    // If any other key was pressed, do not consume the event.
                    return false;
                }
            }
        });

        // onCreateDialog requires the return of an AlertDialog.
        return alertDialog;
    }
}
