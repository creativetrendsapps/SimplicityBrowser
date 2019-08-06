package com.creativetrends.app.simplicity.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativetrends.app.simplicity.ui.Cardbar;
import com.creativetrends.app.simplicity.utils.UserPreferences;
import com.creativetrends.simplicity.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SimplicityProfile extends BaseActivity {

    private AppCompatImageView appCompatImageView;
    private TextInputEditText editText;
    AppCompatButton appCompatButton;
    private static final int CHOOSE_IMAGE = 22;
    Uri user_pic;
    String pic_url;
    boolean changed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(UserPreferences.getBoolean("dark_mode", false)){
            setTheme(R.style.SimplicityAccountThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        appCompatButton = findViewById(R.id.next_button);
        editText = findViewById(R.id.name_edit);
        appCompatImageView = findViewById(R.id.account_picture);
        appCompatImageView.setOnClickListener(v -> showImageChooser());

        appCompatButton.setOnClickListener(v -> saveUserInfo());
        changed = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentUser!= null && currentUser.getPhotoUrl() != null && currentUser.getDisplayName() != null && !changed) {
            getUserInfo(appCompatImageView);
        }
    }

    @Override
    public void onBackPressed() {
        try{
            if(editText.getText().toString().isEmpty() && changed){
                editText.setError("Name required");
            }else {
                super.onBackPressed();
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    private void showImageChooser(){
        try{
            if(editText.getText().toString().isEmpty()){
                editText.setError("Name required");
            }else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }


    private void uploadToFireBase(){
        try{
            StorageReference proimage = FirebaseStorage.getInstance().getReference(currentUser.getUid() +"/profilepicture/"+currentUser.getUid()+ ".jpg");

            if(user_pic != null){
                proimage.putFile(user_pic)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return proimage.getDownloadUrl();
                        }).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                pic_url = Objects.requireNonNull(downloadUri).toString();
                                Cardbar.snackBar(SimplicityProfile.this,"Uploaded profile image.", false).show();

                            } else {
                                Cardbar.snackBar(SimplicityProfile.this,Objects.requireNonNull(task.getException()).toString(), true).show();

                            }
                        });


            }

        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    private void saveUserInfo(){
        try{
            if(editText.getText().toString().isEmpty()){
                editText.setError("Name required");
                editText.requestFocus();
                return;
            }

            if(currentUser != null && pic_url!= null){
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(editText.getText().toString())
                        .setPhotoUri(Uri.parse(pic_url))
                        .build();
                currentUser.updateProfile(userProfileChangeRequest)

                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Cardbar.snackBar(SimplicityProfile.this,"Profile updated!", false).show();
                                finish();
                            }
                        });
            }
        }catch (NullPointerException i){
            i.printStackTrace();
        }catch (Exception ignored){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() !=null){
            user_pic = data.getData();
            changed = true;
            if (user_pic != null)
                Glide.with(this).asBitmap().load(user_pic).apply(new RequestOptions().circleCrop()).into(appCompatImageView);
                uploadToFireBase();
        }
    }
}
