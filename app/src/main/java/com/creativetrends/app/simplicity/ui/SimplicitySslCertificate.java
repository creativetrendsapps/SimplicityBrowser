package com.creativetrends.app.simplicity.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.simplicity.app.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

/**Created by Creative Trends Apps on 2/17/2017.*/

public class SimplicitySslCertificate extends DialogFragment {
    @NonNull
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the activity's layout inflater.
        LayoutInflater layoutInflater   = Objects.requireNonNull(getActivity()).getLayoutInflater();

        // Create a drawable version of the favorite icon.
        Drawable favoriteIconDrawable = new BitmapDrawable(getResources(), MainActivity.favoriteIcon);

        // Use `AlertDialog.Builder` to create the `AlertDialog`.  `R.style.LightAlertDialog` formats the color of the button text.
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setIcon(favoriteIconDrawable);

        // Set an `onClick` listener on the negative button.  Using `null` closes the dialog without doing anything else.
        dialogBuilder.setNegativeButton(R.string.ok, null);

        // Check to see if the website is encrypted.
        if (MainActivity.sslCertificate == null) {  // The website is not encrypted.
            // Set the title.
            dialogBuilder.setTitle(R.string.unencrypted_website);

            // Set the Layout.  The parent view is `null` because it will be assigned by `AlertDialog`.
            dialogBuilder.setView(layoutInflater.inflate(R.layout.layout_none_https, null));

            // Create an `AlertDialog` from the `AlertDialog.Builder`
            final AlertDialog alertDialog = dialogBuilder.create();

            // Show `alertDialog`.
            alertDialog.show();

            // `onCreateDialog` requires the return of an `AlertDialog`.
            return alertDialog;

        } else {  // Display the SSL certificate information
            // Set the title.
            dialogBuilder.setTitle(R.string.ssl_certificate);

            // Set the layout.  The parent view is `null` because it will be assigned by `AlertDialog`.
            dialogBuilder.setView(layoutInflater.inflate(R.layout.layout_https, null));

            // Create an `AlertDialog` from the `AlertDialog.Builder`
            final AlertDialog alertDialog = dialogBuilder.create();

            // We need to show the `AlertDialog` before we can modify items in the layout.
            alertDialog.show();

            // Get handles for the `TextViews`.
            TextView issuedToCNameTextView = alertDialog.findViewById(R.id.issued_to_cname);
            TextView issuedToONameTextView = alertDialog.findViewById(R.id.issued_to_oname);
            TextView issuedToUNameTextView = alertDialog.findViewById(R.id.issued_to_uname);
            TextView issuedByCNameTextView = alertDialog.findViewById(R.id.issued_by_cname);
            TextView issuedByONameTextView = alertDialog.findViewById(R.id.issued_by_oname);
            TextView issuedByUNameTextView = alertDialog.findViewById(R.id.issued_by_uname);
            TextView startDateTextView = alertDialog.findViewById(R.id.start_date);
            TextView endDateTextView = alertDialog.findViewById(R.id.end_date);

            // Setup the labels.
            String cNameLabel = getString(R.string.common_name) + "  ";
            String oNameLabel = getString(R.string.organization) + "  ";
            String uNameLabel = getString(R.string.organizational_unit) + "  ";
            String startDateLabel = getString(R.string.start_date) + "  ";
            String endDateLabel = getString(R.string.end_date) + "  ";

            // Get the SSL certificate.
            SslCertificate sslCertificate = MainActivity.sslCertificate;

            // Get the strings from the SSL certificate.
            String issuedToCNameString = sslCertificate.getIssuedTo().getCName();
            String issuedToONameString = sslCertificate.getIssuedTo().getOName();
            String issuedToUNameString = sslCertificate.getIssuedTo().getUName();
            String issuedByCNameString = sslCertificate.getIssuedBy().getCName();
            String issuedByONameString = sslCertificate.getIssuedBy().getOName();
            String issuedByUNameString = sslCertificate.getIssuedBy().getUName();
            Date startDate = sslCertificate.getValidNotBeforeDate();
            Date endDate = sslCertificate.getValidNotAfterDate();

            // Create a `SpannableStringBuilder` for each `TextView` that needs multiple colors of text.
            SpannableStringBuilder issuedToCNameStringBuilder = new SpannableStringBuilder(cNameLabel + issuedToCNameString);
            SpannableStringBuilder issuedToONameStringBuilder = new SpannableStringBuilder(oNameLabel + issuedToONameString);
            SpannableStringBuilder issuedToUNameStringBuilder = new SpannableStringBuilder(uNameLabel + issuedToUNameString);
            SpannableStringBuilder issuedByCNameStringBuilder = new SpannableStringBuilder(cNameLabel + issuedByCNameString);
            SpannableStringBuilder issuedByONameStringBuilder = new SpannableStringBuilder(oNameLabel + issuedByONameString);
            SpannableStringBuilder issuedByUNameStringBuilder = new SpannableStringBuilder(uNameLabel + issuedByUNameString);
            SpannableStringBuilder startDateStringBuilder = new SpannableStringBuilder(startDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(startDate));
            SpannableStringBuilder endDateStringBuilder = new SpannableStringBuilder(endDateLabel + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(endDate));



            // Display the strings.
            if (issuedToCNameTextView != null) {
                issuedToCNameTextView.setText(issuedToCNameStringBuilder);
            }
            if (issuedToONameTextView != null) {
                issuedToONameTextView.setText(issuedToONameStringBuilder);
            }
            if (issuedToUNameTextView != null) {
                issuedToUNameTextView.setText(issuedToUNameStringBuilder);
            }
            if (issuedByCNameTextView != null) {
                issuedByCNameTextView.setText(issuedByCNameStringBuilder);
            }
            if (issuedByONameTextView != null) {
                issuedByONameTextView.setText(issuedByONameStringBuilder);
            }
            if (issuedByUNameTextView != null) {
                issuedByUNameTextView.setText(issuedByUNameStringBuilder);
            }
            if (startDateTextView != null) {
                startDateTextView.setText(startDateStringBuilder);
            }
            if (endDateTextView != null) {
                endDateTextView.setText(endDateStringBuilder);
            }

            // `onCreateDialog` requires the return of an `AlertDialog`.
            return alertDialog;
        }
    }
}