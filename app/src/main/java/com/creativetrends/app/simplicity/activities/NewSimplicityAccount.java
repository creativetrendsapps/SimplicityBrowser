package com.creativetrends.app.simplicity.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
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
    ProgressDialog progressDialog;


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

    }


    //part one of sending email verification for new account
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


    //part two of sending email verification for new account
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

        return valid;
    }

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
            accountDialog();
            File bh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + getResources().getString(R.string.app_name) + File.separator + "Your backup location" + File.separator);
            if (!bh.exists()) {
                //noinspection ResultOfMethodCallIgnored
                bh.mkdirs();
            }
            String extStorageDirectory = bh.toString();
            File file = new File(extStorageDirectory, Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".your_extention");
            ExportUtils.writeToFile(file, this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    uploadToFireBase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 5000);
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



    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(intent, "Select Profile Image");
        startActivity(chooserIntent);
    }

    //saving the user to firebase after creating their account
    private void uploadToFireBase() {
        StorageReference proimage = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/your_profile_picture_location/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
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
    }

    
    //make sure the required info is applied
    private void saveUserInfo() {
        String displayName = Objects.requireNonNull(editText0.getText()).toString();
        if (editText0.getText().toString().isEmpty()) {
            editText0.setError("Name required");
            editText0.requestFocus();
            return;
        }
        //sending the users profile image to firebase
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
                            if (!isDestroyed() && progressDialog!= null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }


    //send the first bookmark file to Firebase
    private void createUserFile() {
        final File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String pathToMyAttachedFile = File.separator + getResources().getString(R.string.app_name) + File.separator + "Your backup location" + File.separator + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".your_extention";
        File file = new File(root, pathToMyAttachedFile);
        final Uri uri = Uri.fromFile(file);
        StorageReference proimage = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/your_backup_location/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".your_extention");
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
    }


    //letting the user know their account is being created
    private void accountDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating account");
        progressDialog.setMessage("This will only take a minute.");
        progressDialog.setVolumeControlStream(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    
    //gettting the picture that was sent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            user_pic = data.getData();
            if (user_pic != null) {
                Glide.with(this).asBitmap().load(user_pic).apply(new RequestOptions().circleCrop()).into(appCompatImageView);
            }
        }
    }


}
