package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    boolean shouldSync = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
    }

    public void getUserInfo(AppCompatImageView imageView){
        if (currentUser != null) {
            if (currentUser.getPhotoUrl() != null) {
                Log.e("Profile", UserPreferences.getString("pro_picture", ""));
                Glide.with(this)
                        .load(currentUser.getPhotoUrl().toString())
                        .apply(RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageView);
            }
        }

    }
}
