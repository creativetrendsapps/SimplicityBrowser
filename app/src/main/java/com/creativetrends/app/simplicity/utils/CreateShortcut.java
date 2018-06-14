package com.creativetrends.app.simplicity.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.creativetrends.simplicity.app.R;

import static com.creativetrends.app.simplicity.activities.MainActivity.favoriteIcon;
import static com.creativetrends.app.simplicity.activities.MainActivity.webViewTitle;

// We have to use `AppCompatDialogFragment` instead of `DialogFragment` or an error is produced on API <=22.


public class CreateShortcut extends AppCompatDialogFragment {
    EditText shortcutNameEditText;
    ImageView imageShortcut;
    // The public interface is used to send information back to the parent activity.
    public interface CreateHomeScreenSchortcutListener {
        void onCreateHomeScreenShortcut(AppCompatDialogFragment dialogFragment);
    }

    //`createHomeScreenShortcutListener` is used in `onAttach()` and `onCreateDialog()`.
    private CreateHomeScreenSchortcutListener createHomeScreenShortcutListener;

    // Check to make sure that the parent activity implements the listener.
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            createHomeScreenShortcutListener = (CreateHomeScreenSchortcutListener) context;
        } catch(ClassCastException exception) {
            throw new ClassCastException(context.toString() + " must implement CreateHomeScreenShortcutListener.");
        }
    }

    // `@SuppressLing("InflateParams")` removes the warning about using `null` as the parent view group when inflating the `AlertDialog`.
    @SuppressWarnings("ConstantConditions")
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the activity's layout inflater.
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();


        //BitmapDrawable resizedBitmap = new BitmapDrawable(getResources(), favoriteIcon);
        Bitmap resizedBitmap = Bitmap.createBitmap(favoriteIcon);
        // Use `AlertDialog.Builder` to create the `AlertDialog`.
        AlertDialog.Builder dialogBuilder;

        dialogBuilder = new AlertDialog.Builder(getActivity());

        // Set the title and icon.
        dialogBuilder.setTitle(R.string.add_home);
        // Set the view.  The parent view is `null` because it will be assigned by `AlertDialog`.
        dialogBuilder.setView(layoutInflater.inflate(R.layout.activity_shortcut, null));

        // Setup the negative button.  Using `null` closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.cancel, null);

        // Set an `onClick` listener on the positive button.
        dialogBuilder.setPositiveButton(R.string.add, (dialog, which) -> {
            createHomeScreenShortcutListener.onCreateHomeScreenShortcut(CreateShortcut.this);
            try {
                Toast.makeText(getActivity(), shortcutNameEditText.getText().toString() + " " + getString(R.string.added_to_home), Toast.LENGTH_SHORT).show();
            }catch(NullPointerException ignored){
            }catch(Exception i){
                i.printStackTrace();
                Toast.makeText(getActivity(), webViewTitle + " " + getString(R.string.added_to_home), Toast.LENGTH_SHORT).show();
            }
        });


        // Create an `AlertDialog` from the `AlertDialog.Builder`.
        AlertDialog alertDialog = dialogBuilder.create();

        // Remove the warning below that `setSoftInputMode` might produce `java.lang.NullPointerException`.
        assert alertDialog.getWindow() != null;

        // Show the keyboard when the Dialog is displayed on the screen.
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // We need to show `alertDialog` before we can call `setOnKeyListener()` below.
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();

        // Get a handle for `shortcut_name_edittext`.
        shortcutNameEditText = alertDialog.findViewById(R.id.shortcut_name_edittext);
        imageShortcut = alertDialog.findViewById(R.id.fav_imageView);
        imageShortcut.setImageBitmap(StaticUtils.getCircleBitmap(resizedBitmap));
        // Set the current `WebView` title as the text for `shortcutNameEditText`.
        shortcutNameEditText.setText(webViewTitle);

        // Allow the "enter" key on the keyboard to create the shortcut.
        final AlertDialog finalAlertDialog = alertDialog;
        shortcutNameEditText.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down on the "enter" button, select the PositiveButton "Create".
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Trigger the create listener.
                createHomeScreenShortcutListener.onCreateHomeScreenShortcut(CreateShortcut.this);

                // Manually dismiss `alertDialog`.
                finalAlertDialog.dismiss();

                // Consume the event.
                return true;
            } else {  // If any other key was pressed, do not consume the event.
                return false;
            }
        });

        // `onCreateDialog` requires the return of an `AlertDialog`.
        return alertDialog;
    }
}