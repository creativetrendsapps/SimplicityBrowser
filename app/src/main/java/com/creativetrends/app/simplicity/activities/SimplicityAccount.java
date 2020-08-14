package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.StaticUtils;
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
    AlertDialog PostDialog;
    ProgressBar progressBar;


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
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }


    }


    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        try {
            deleteCache();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName() != null) {
                                Cardbar.snackBar(this, "Welcome back," + " " + Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName() + "!", false).show();
                            } else {
                                Cardbar.snackBar(this, "Welcome back!", false).show();
                            }
                            //Log.i("User", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                            UserPreferences.putString("user", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                            new Handler().postDelayed(() -> {
                                try {
                                    downloadFromFirebase();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, 5000);
                        } else {
                            if (PostDialog != null && PostDialog.isShowing()) {
                                PostDialog.dismiss();
                            }
                            UserPreferences.putString("user", StaticUtils.getRandomString(28));
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
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }


    private boolean validateForm() {
        boolean valid = true;
        try {
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
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }

        return valid;
    }

    private void resetPass(String email) {
        try {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Cardbar.snackBar(this, "Password reset email sent to " + Objects.requireNonNull(editText1.getText()).toString(), true).show();

                        }
                    });
            attempts = 2;

        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

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
                Objects.requireNonNull(editText2.getText()).clear();
            }
            if (attempts == 0) {
                forgot.setVisibility(View.VISIBLE);
            } else {
                forgot.setVisibility(View.INVISIBLE);
            }
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

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

    private void loadMain() {
        try {
            final File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String pathToMyAttachedFile = File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator + UserPreferences.getString("user", "") + ".sbh";
            File file = new File(root, pathToMyAttachedFile);
            ExportUtils.readFromFile(file, this);
            if (PostDialog != null && PostDialog.isShowing()) {
                PostDialog.dismiss();
            }
        } catch (Exception i) {
            i.printStackTrace();
        }
        finish();
    }


    private void downloadFromFirebase() {
        try {
            //Log.e("User?",UserPreferences.getString("user", ""));
            String fileName = UserPreferences.getString("user", "");
            StorageReference proimage = FirebaseStorage.getInstance().getReference(UserPreferences.getString("user", "") + "/simplicity_backup/" + UserPreferences.getString("user", "") + ".sbh");
            final File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups");
            if (!rootPath.exists()) {
                //noinspection ResultOfMethodCallIgnored
                rootPath.mkdirs();
                Log.i("created dir", rootPath.getPath());
            } else {
                Log.i("", "");
            }
            final File localFile = new File(rootPath, fileName + ".sbh");

            proimage.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Log.d("Downloaded", "got backup from Firebase");
                loadMain();
            }).addOnFailureListener(exception ->
                    Log.d("Failed from Firebase", Objects.requireNonNull(exception).toString()));
            loadMain();
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }


    private void deleteCache() {
        try {
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams")
            View alertLayout = inflater.inflate(R.layout.account_dialog, null);
            progressBar = alertLayout.findViewById(R.id.prog);
            progressBar.setIndeterminate(true);
            AlertDialog.Builder progress = new AlertDialog.Builder(SimplicityAccount.this);
            progress.setTitle("Account Sync");
            progress.setMessage("Syncing bookmarks and history");
            progress.setCancelable(false);
            progress.setView(alertLayout);
            PostDialog = progress.create();
            PostDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
