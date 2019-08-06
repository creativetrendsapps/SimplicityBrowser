package com.creativetrends.app.simplicity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class SimplicityAccount extends BaseActivity {
    TextInputEditText editText1, editText2;
    AppCompatButton verify;
    Toolbar toolbar;
    AppCompatTextView forgot, create;
    int attempts = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(UserPreferences.getBoolean("dark_mode", false)){
            setTheme(R.style.SimplicityAccountThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        editText1 = findViewById(R.id.email_edit);
        editText2 = findViewById(R.id.password_edit);
        verify = findViewById(R.id.verify_button);
        forgot = findViewById(R.id.forgot_pass);
        create = findViewById(R.id.create);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(null);
        }

        AppCompatTextView copyright = findViewById(R.id.copy);
        copyright.setText("");
        try {
            create.setOnClickListener(v -> {
                Intent intent = new Intent(SimplicityAccount.this, NewSimplicityAccount.class);
                startActivity(intent);
                finish();

            });
            verify.setOnClickListener(v -> signIn(Objects.requireNonNull(editText1.getText()).toString(), Objects.requireNonNull(editText2.getText()).toString()));
            forgot.setOnClickListener(v -> resetPass(Objects.requireNonNull(editText1.getText()).toString()));
            editText2.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    signIn(Objects.requireNonNull(editText1.getText()).toString(), Objects.requireNonNull(editText2.getText()).toString());
                    return true;
                }
                return false;
            });
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }


    }




    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        try{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            if(currentUser != null) {
                                Cardbar.snackBar(this, "Welcome back" + " " + mAuth.getCurrentUser().getDisplayName() + "!", false).show();
                            }else{
                                Cardbar.snackBar(this, "Welcome back!", false).show();
                                Log.i("User", mAuth.getCurrentUser().getUid());
                                try{
                                    File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator);
                                    if (!bh.exists()) {
                                        //noinspection ResultOfMethodCallIgnored
                                        bh.mkdirs();
                                    }
                                    String extStorageDirectory = bh.toString();
                                    File file = new File(extStorageDirectory, mAuth.getCurrentUser().getUid() + ".sbh");
                                    ExportUtils.writeToFile(file, this);
                                }catch (Exception i){
                                    i.printStackTrace();

                                }
                                new Handler().postDelayed(() -> {
                                    try {
                                        downloadFromFirebase();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, 4000);
                            }
                            updateUI();
                        } else {
                            Cardbar.snackBar(this,"Authentication failed.", true).show();
                            Objects.requireNonNull(editText2.getText()).clear();
                            attempts--;
                            if(attempts == 0){
                                forgot.setVisibility(View.VISIBLE);
                            }else{
                                forgot.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }


    private boolean validateForm () {
        boolean valid = true;
        try{
            String email = Objects.requireNonNull(editText1.getText()).toString();
            if (TextUtils.isEmpty(email)) {
                editText1.setError("Required.");
                valid = false;
            } else {
                editText1.setError(null);
            }

            String password = Objects.requireNonNull(editText2.getText()).toString();
            if (TextUtils.isEmpty(password)) {
                editText2.setError("Required.");
                valid = false;
            } else {
                editText2.setError(null);
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }

        return valid;
    }

    private void resetPass(String email){
        try{
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Cardbar.snackBar(this,"Password reset email sent to " + Objects.requireNonNull(editText1.getText()).toString(), true).show();

                        }
                    });
            attempts = 2;

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (currentUser != null) {
                finish();
            } else if (!Objects.requireNonNull(editText1.getText()).toString().isEmpty() || !Objects.requireNonNull(editText2.getText()).toString().isEmpty()) {
                editText1.getText().clear();
                editText2.getText().clear();
            }
            if (attempts == 0) {
                forgot.setVisibility(View.VISIBLE);
            } else {
                forgot.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadMain(){
        try{
            if(currentUser != null) {
                File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator, currentUser.getUid() + ".sbh");
                ExportUtils.writeToFile(bh, this);
            }
        }catch (Exception i){
            i.printStackTrace();

        }
        finish();
    }


    private void downloadFromFirebase(){
        try{
            StorageReference proimage = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/simplicity_backup/" + mAuth.getCurrentUser().getUid() + ".sbh");
            File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator, mAuth.getCurrentUser().getUid() + ".sbh");
            ExportUtils.readFromFile(bh, this);
            proimage.getFile(bh).addOnSuccessListener(taskSnapshot -> {
                Log.d("Downloaded", "got backup from Firebase");
            }).addOnFailureListener(exception ->
                    Log.d("Failed from Firebase", Objects.requireNonNull(exception).toString()));
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }


    private void updateUI() {
        loadMain();

    }

}