package com.creativetrends.app.simplicity.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.ExportUtils;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class NewSimplicityAccount extends BaseActivity {
    private static final int CHOOSE_IMAGE = 22;
    AppCompatEditText editText0, editText1, editText2;
    MaterialButton next, verify;
    Toolbar toolbar;
    AppCompatTextView forgot;
    AppCompatImageView appCompatImageView;
    int attempts = 2;
    Uri user_pic;
    String pic_url;
    AlertDialog PostDialog;
    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (UserPreferences.getBoolean("dark_mode", false)) {
            setTheme(R.style.SimplicityAccountThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        editText0 = findViewById(R.id.name_edit);
        editText1 = findViewById(R.id.email_edit);
        editText2 = findViewById(R.id.password_edit);
        next = findViewById(R.id.next_button);
        verify = findViewById(R.id.verify_button);
        forgot = findViewById(R.id.forgot_pass);
        toolbar = findViewById(R.id.toolbar);
        appCompatImageView = findViewById(R.id.account_picture);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(null);
        }

        AppCompatTextView copyright = findViewById(R.id.copy);
        copyright.setText("");
        try {
            appCompatImageView.setOnClickListener(v -> showImageChooser());
            next.setOnClickListener(v -> createAccount(Objects.requireNonNull(editText0.getText()).toString(), Objects.requireNonNull(editText1.getText()).toString(), Objects.requireNonNull(editText2.getText()).toString()));
            forgot.setOnClickListener(v -> resetPass(Objects.requireNonNull(editText1.getText()).toString()));
            editText2.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    createAccount(Objects.requireNonNull(editText0.getText()).toString(), Objects.requireNonNull(editText1.getText()).toString(), Objects.requireNonNull(editText2.getText()).toString());
                    return true;
                }
                return false;
            });
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }

    }


    private void createAccount(String name, String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification();
                    } else {
                        Cardbar.snackBar(this, "Error creating account.", true).show();

                    }

                });

    }


    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Cardbar.snackBar(this, "Verification email sent to " + user.getEmail(), true).show();

                } else {
                    Cardbar.snackBar(this, "Failed to send verification email.", true).show();

                }

            });
        }
    }


    private boolean validateForm() {
        boolean valid = true;
        try {
            String name = Objects.requireNonNull(editText0.getText()).toString();
            if (TextUtils.isEmpty(name)) {
                editText0.setError("Required.");
                valid = false;
            } else {
                editText0.setError(null);
            }

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

            if(user_pic == null){
                Cardbar.snackBar(this,"Avatar required", false).show();
                valid = false;
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
                            Snackbar.make(editText2, "Password reset email sent to " + Objects.requireNonNull(editText1.getText()).toString(), Snackbar.LENGTH_LONG).show();
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
                deleteCache();
                File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator);
                if (!bh.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    bh.mkdirs();
                }
                String extStorageDirectory = bh.toString();
                File file = new File(extStorageDirectory, currentUser.getUid() + ".sbh");
                ExportUtils.writeToFile(file, this);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        uploadToFireBase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 5000);
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
        finish();
    }


    private void updateUI() {
        loadMain();
    }

    private void showImageChooser() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }

    private void uploadToFireBase() {
        try {
            StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() + "/profilepicture/" + currentUser.getUid() + ".jpg");
            if (user_pic != null) {
                proimage.putFile(user_pic).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return proimage.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            pic_url = downloadUri.toString();
                        }
                        createUserFile();
                        saveUserInfo();
                    } else {
                        Cardbar.snackBar(getApplicationContext(), Objects.requireNonNull(task.getException()).toString(), true).show();

                    }
                });
            }

        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }


    private void saveUserInfo() {
        try {
            String displayName = Objects.requireNonNull(editText0.getText()).toString();
            if (editText0.getText().toString().isEmpty()) {
                editText0.setError("Name required");
                editText0.requestFocus();
                return;
            }

            if (currentUser != null && pic_url != null) {
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .setPhotoUri(Uri.parse(pic_url))
                        .build();
                currentUser.updateProfile(userProfileChangeRequest)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Cardbar.snackBar(getApplicationContext(), getString(R.string.account_created), true).show();
                                finish();
                            }
                        });
            }
        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }


    private void createUserFile() {
        try {
            UserPreferences.putString("user", currentUser.getUid());
            final File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String pathToMyAttachedFile = File.separator + getResources().getString(R.string.app_name) + File.separator + "Simplicity Backups" + File.separator + currentUser.getUid() + ".sbh";
            File file = new File(root, pathToMyAttachedFile);
            final Uri uri = Uri.fromFile(file);
            StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() + "/simplicity_backup/" + currentUser.getUid() + ".sbh");
            if (uri != null) {
                proimage.putFile(uri).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return proimage.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Backed up", "sent history to Firebase");
                    } else {
                        Log.d("Failed backed up", Objects.requireNonNull(task.getException()).toString());
                    }
                });


            }

        } catch (NullPointerException i) {
            i.printStackTrace();
        } catch (Exception ignored) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            user_pic = data.getData();
            if (user_pic != null) {
                Glide.with(this).asBitmap().load(user_pic).apply(new RequestOptions().circleCrop()).into(appCompatImageView);

                //Glide.with(this).load(user_pic).into(add_pic);
                //Glide.with(this).asBitmap().load(user_pic.toString()).into(add_pic);
                // currentUser.
            }
        }
    }

    private void deleteCache() {
        try {
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams")
            View alertLayout = inflater.inflate(R.layout.account_dialog, null);
            progressBar = alertLayout.findViewById(R.id.prog);
            progressBar.setIndeterminate(true);
            AlertDialog.Builder progress = new AlertDialog.Builder(NewSimplicityAccount.this);
            progress.setTitle("Creating account");
            progress.setMessage(getString(R.string.please_wait_account));
            progress.setCancelable(false);
            progress.setView(alertLayout);
            PostDialog = progress.create();
            PostDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
