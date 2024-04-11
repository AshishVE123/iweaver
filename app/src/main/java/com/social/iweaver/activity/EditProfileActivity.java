package com.social.iweaver.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.EditProfileResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.profileimageupload.Data;
import com.social.iweaver.apiresponse.profileimageupload.UploadProfileImage;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityEditProfileBinding;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.EditProfileViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_REQUEST = 1888;
    public static final int GALLERY_REQUEST = 1889;

    private Button cancel, submit, back;
    private CustomAlertDialog dialog;
    private TextView titleText;
    public static String path;
    private File finalFile;
    private UserPreference mPreference;
    private String imagePath;
    private ActivityEditProfileBinding profileEditBinding;
    private DatabaseHelper databaseHelper;
    private LoginAttributeResponse loginUserDetails;
    private EditProfileViewModel editProfileViewModel;
    private EditText etName, etAboutUs, etLanguage, etGender, etDOB;
    private ImageView profileAvataar, user_avatar;
    private CustomLoader loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileEditBinding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(profileEditBinding.getRoot());
        databaseHelper = new DatabaseHelper(this);
        loginUserDetails = databaseHelper.getUserDetails();
        titleText = profileEditBinding.includeLayout.tvTitleText;
        back = profileEditBinding.includeLayout.btBack;
        titleText.setText("Edit Profile");
        mPreference = UserPreference.getInstance(this);
        back.setOnClickListener(this);
        etName = profileEditBinding.etName;
        user_avatar = profileEditBinding.userAvtaar;
        profileAvataar = profileEditBinding.ivCamera;
        etAboutUs = profileEditBinding.etAboutUs;
        etLanguage = profileEditBinding.etLanguage;
        etGender = profileEditBinding.etGender;
        etDOB = profileEditBinding.etDob;
        cancel = profileEditBinding.btCancel;
        submit = profileEditBinding.btSubmit;
        etName.setText(loginUserDetails.getName());
        etAboutUs.setText(loginUserDetails.getAboutUs());
        etLanguage.setText(loginUserDetails.getLanguages());
        etGender.setText(loginUserDetails.getGender());
        etDOB.setText(loginUserDetails.getBirthday());
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        dialog = CustomAlertDialog.getInstance(this);
        profileAvataar.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        Glide.with(this).
                load(loginUserDetails.getUserImage()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).
                placeholder(R.drawable.image_progress_animation).
                into(user_avatar);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancel: {
                finish();
                break;
            }
            case R.id.bt_back: {
                finish();
                break;
            }
            case R.id.bt_submit: {
                int errorCode = InputFieldValidator.getInstance(this).isInputsProfileStringNull(etName.getText().toString(), etAboutUs.getText().toString(), etLanguage.getText().toString(), etGender.getText().toString(), etDOB.getText().toString());
                if (errorCode == 1) {
                    etName.setError("Please enter name");
                    etName.requestFocus();
                } else if (errorCode == 2) {
                    etAboutUs.setError("Please enter about us");
                    etAboutUs.requestFocus();
                } else if (errorCode == 3) {
                    etLanguage.setError("Please enter language");
                    etLanguage.requestFocus();
                } else if (errorCode == 4) {
                    etGender.setError("Please enter gender");
                    etGender.requestFocus();
                } else if (errorCode == 5) {
                    etDOB.setError("Please enter dob");
                    etDOB.requestFocus();
                } else {
                    if (NetworkUtils.getConnectivityStatusString(EditProfileActivity.this)) {
                        JsonObject object = new JsonObject();
                        object.addProperty("name", etName.getText().toString());
                        object.addProperty("aboutUs", etAboutUs.getText().toString());
                        object.addProperty("languages", etLanguage.getText().toString());
                        object.addProperty("gender", etGender.getText().toString());
                        object.addProperty("dob", etDOB.getText().toString());
                        callUpdateProfileRequest(object, loginUserDetails.getId());
                    } else {
                        startActivity(new Intent(EditProfileActivity.this, NetworkIssueActivity.class));
                        finish();
                    }
                }
                break;
            }
            case R.id.iv_camera: {
                dialog.chooseProfileImage(EditProfileActivity.this);
                break;
            }
        }

    }

    public void callUpdateProfileRequest(JsonObject object, String userID) {
        loader.showLoading(EditProfileActivity.this, "Please wait. Processing your request");
        editProfileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(EditProfileViewModel.class);
        editProfileViewModel.initiateEditProfileProcess(EditProfileActivity.this, object, userID)
                .observe(EditProfileActivity.this, new Observer<EditProfileResponse>() {
                    @Override
                    public void onChanged(EditProfileResponse editProfileResponse) {
                        loader.hideLoading();
                        editProfileViewModel = null;
                        if (editProfileResponse != null) {
                            databaseHelper.LoginProfileUpdate(object, userID);
                            MyProfileActivity.getInstance().updateData();
                            loader.showSnackBar(EditProfileActivity.this, editProfileResponse.getMessage(), true);
                        } else {
                            loader.showSnackBar(EditProfileActivity.this, ErrorReponseParser.errorMsg, true);
                        }
                    }
                });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", "");
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            System.out.println("Checking the intent value from gallery::" + resultCode + " " + requestCode);
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Glide.with(this).load("file:" + path).into(user_avatar);
                uploadImage();
            } else if (requestCode == GALLERY_REQUEST && data != null) {
                Uri image = data.getData();
                user_avatar.setImageURI(image);
                finalFile = new File(getRealPathFromURI(image));
                imagePath = finalFile.getPath();
                path = imagePath;
                uploadImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void uploadImage() {
        loader.showLoading(EditProfileActivity.this, "Please wait.Processing your request.");
        editProfileViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(EditProfileViewModel.class);
        File file = new File(path.trim());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profileImageUrl", +System.currentTimeMillis() + " " + file.getName(), requestFile);
        Log.e("requestFile", requestFile.toString());
        editProfileViewModel.initiateUploadImageProcess(EditProfileActivity.this, body)
                .observe(EditProfileActivity.this, new Observer<UploadProfileImage>() {
                    @Override
                    public void onChanged(UploadProfileImage editProfileResponse) {
                        editProfileViewModel = null;
                        if (editProfileResponse != null) {
                            Data data = new Data();
                            data = editProfileResponse.getData();
                            databaseHelper.loginProfileImageUpdate(data.getTmpprofileImage(), loginUserDetails.getId());
                            MyProfileActivity.getInstance().updateData();
                            // finalFile.delete();
                            loader.hideLoading();
                            loader.showSnackBar(EditProfileActivity.this, editProfileResponse.getMessage(), false);
                        } else {
                            loader.showSnackBar(EditProfileActivity.this, ErrorReponseParser.errorMsg, true);
                        }
                    }
                });
    }


}