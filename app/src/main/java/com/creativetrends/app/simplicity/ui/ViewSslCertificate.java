package com.creativetrends.app.simplicity.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.creativetrends.app.simplicity.activities.MainActivity;
import com.creativetrends.simplicity.app.R;

import java.text.DateFormat;
import java.util.Date;

/**Created by Creative Trends Apps on 2/17/2017.*/

public class ViewSslCertificate extends DialogFragment {
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the activity's layout inflater.
        LayoutInflater layoutInflater   = getActivity().getLayoutInflater();

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
            dialogBuilder.setView(layoutInflater.inflate(R.layout.simple_none_https_website, null));

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
            dialogBuilder.setView(layoutInflater.inflate(R.layout.simple_view_https, null));

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

            // Create a blue `ForegroundColorSpan`.  We have to use the deprecated `getColor` until API >= 23.
            @SuppressWarnings("deprecation") ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.colorAccent));

            // Setup the spans to display the certificate information in blue.  `SPAN_INCLUSIVE_INCLUSIVE` allows the span to grow in either direction.
            issuedToCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), issuedToCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            issuedToONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), issuedToONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            issuedToUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), issuedToUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            issuedByCNameStringBuilder.setSpan(blueColorSpan, cNameLabel.length(), issuedByCNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            issuedByONameStringBuilder.setSpan(blueColorSpan, oNameLabel.length(), issuedByONameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            issuedByUNameStringBuilder.setSpan(blueColorSpan, uNameLabel.length(), issuedByUNameStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            startDateStringBuilder.setSpan(blueColorSpan, startDateLabel.length(), startDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            endDateStringBuilder.setSpan(blueColorSpan, endDateLabel.length(), endDateStringBuilder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            // Display the strings.
            issuedToCNameTextView.setText(issuedToCNameStringBuilder);
            issuedToONameTextView.setText(issuedToONameStringBuilder);
            issuedToUNameTextView.setText(issuedToUNameStringBuilder);
            issuedByCNameTextView.setText(issuedByCNameStringBuilder);
            issuedByONameTextView.setText(issuedByONameStringBuilder);
            issuedByUNameTextView.setText(issuedByUNameStringBuilder);
            startDateTextView.setText(startDateStringBuilder);
            endDateTextView.setText(endDateStringBuilder);

            // `onCreateDialog` requires the return of an `AlertDialog`.
            return alertDialog;
        }
    }
}