package com.creativetrends.app.simplicity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class SimplicityAccount extends BaseActivity {
    TextInputEditText editText1, editText2;
    MaterialButton verify;
    Toolbar toolbar;
    AppCompatTextView forgot, create;
    int attempts = 2;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (UserPreferences.getBoolean("dark_mode", false)) {
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


    }


    //verify account and sign in
    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        accountDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //show welcome message for returning user
                        if (Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName() != null) {
                            Cardbar.snackBar(this, "Welcome back," + " " + mAuth.getCurrentUser().getDisplayName() + "!", false).show();
                        } else {
                            Cardbar.snackBar(this, "Welcome back!", false).show();
                        }
                        new Handler().postDelayed(() -> {
                            try {
                                downloadFromFirebase();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 5000);
                    } else {
                        //something is not right with the sign in
                        if (!isDestroyed() && progressDialog!= null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Cardbar.snackBar(this, "Authentication failed.", true).show();
                        Objects.requireNonNull(editText2.getText()).clear();
                        attempts--;
                        if (attempts == 0) {
                            forgot.setVisibility(View.VISIBLE);
                        } else {
                            forgot.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    //make user all required fields are complete when updating a profile
    private boolean validateForm() {
        boolean valid = true;
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
        return valid;
    }

    //offer reset email after a user fails to login
    private void resetPass(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Cardbar.snackBar(this, "Password reset email sent to " + Objects.requireNonNull(editText1.getText()).toString(), true).show();

                    }
                });
        attempts = 2;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            finish();
        } else if (!Objects.requireNonNull(editText1.getText()).toString().isEmpty() || !Objects.requireNonNull(editText2.getText()).toString().isEmpty()) {
            editText1.getText().clear();
            Objects.requireNonNull(editText2.getText()).clear();
        }
        if (attempts == 0) {
            forgot.setVisibility(View.VISIBLE);
        } else {
            forgot.setVisibility(View.INVISIBLE);
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

    //download and save bookmarks and history
    private void downloadFromFirebase() {
        if(mAuth.getCurrentUser() != null) {
            String fileName = mAuth.getCurrentUser().getUid();
            StorageReference proimage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid() + "/your_backup_location/" + mAuth.getCurrentUser().getUid() + ".your_extention");
            final File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups");
            if (!rootPath.exists()) {
                //noinspection ResultOfMethodCallIgnored
                rootPath.mkdirs();
            }
            final File localFile = new File(rootPath, fileName + ".your_extention");
            proimage.getFile(localFile).addOnSuccessListener(taskSnapshot -> loadMain()).addOnFailureListener(exception ->
                    Log.d("Failed from Firebase", Objects.requireNonNull(exception).toString()));
            loadMain();
        }
    }
    
    //read the file we just downloaded to restore bookmarks and history
    private void loadMain() {
        if(mAuth.getCurrentUser() != null) {
            final File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String pathToMyAttachedFile = File.separator + getResources().getString(R.string.app_name) + File.separator + "Your backup location" + File.separator + mAuth.getCurrentUser().getUid() + ".your_extention";
            File file = new File(root, pathToMyAttachedFile);
            ExportUtils.readFromFile(file, this);
            if (!isDestroyed() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        finish();
    }

    //accounts dialog to show as we download user bookmarks and history
    private void accountDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Account Sync");
        progressDialog.setMessage("Syncing bookmarks and history");
        progressDialog.setVolumeControlStream(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
}
