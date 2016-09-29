package com.creativetrends.simplicity.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.creativetrends.simplicity.app.R;

//Created by Family on 9/3/2016.

public class DonationActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    private BillingProcessor bp;
    private static final String PRODUCT_ID_1 = "product1";
    private static final String PRODUCT_ID_2 = "product2";
    private static final String PRODUCT_ID_3 = "product3";
    private static final String LICENSE_KEY = "YOUR_KEY";

    private RelativeLayout small, medium, large;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        bp = new BillingProcessor(this, LICENSE_KEY, this);

        small = (RelativeLayout) findViewById(R.id.smallDonation);
        medium = (RelativeLayout) findViewById(R.id.mediumDonation);
        large = (RelativeLayout) findViewById(R.id.largeDonation);

        setUpClicks();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpClicks() {
        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(DonationActivity.this, PRODUCT_ID_1);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(DonationActivity.this, PRODUCT_ID_2);
            }
        });

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(DonationActivity.this, PRODUCT_ID_3);
            }
        });

    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
}
