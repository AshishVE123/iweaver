package com.social.iweaver.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.CreatePostResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.imagepost.ImagePostResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityCreatePostBinding;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.GetCurrentLocation;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.CreatePostViewModel;
import com.social.iweaver.viewmodel.PostImageUploadModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAMERA_REQUEST = 1888;
    public static String path = null;
    public static final int GALLERY_REQUEST = 1889;
    public static final int REQUEST_TAKE_GALLERY_VIDEO=111;
    private static final int CAMERA_VIDEO = 222;
    private PostImageUploadModel uploadImageModel;
    private ActivityCreatePostBinding postBinding;
    private CustomAlertDialog dialog;
    private File finalFile;
    private List<String> imageList;
    private DatabaseHelper databaseHelper;
    private LoginAttributeResponse loginDetails;
    private ImageView iv_camera, iv_share_image;

    private PlayerView exoPlayerView;
    private ExoPlayer player;
    private CardView ll_image_layout;
    private Button bt_post;
    private CreatePostViewModel postModel;
    private EditText et_post_text;
    private TextView tv_user_details, tv_title_text;
    private Button back;
    private InputFieldValidator inputValidator;
    private CustomLoader loader;
    private UserPreference mPreference;
    private LinearLayout ll_post_params;
    private String imagePath, picturePath;
    private Uri outPutfileUri;
    private String videoPath;
    String decodableString;
    private AlertDialog uploadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postBinding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(postBinding.getRoot());
        tv_title_text = postBinding.includeCommonToolParam.tvTitleText;
        tv_title_text.setText("Create Post");
        bt_post = postBinding.btShare;
        iv_share_image = postBinding.ivShareImage;
        ll_image_layout = postBinding.cvImageParam;
        iv_camera = postBinding.ivCamera;
        ll_post_params = postBinding.llPostParams;
        et_post_text = postBinding.etPostText;
        back = postBinding.includeCommonToolParam.btBack;
        mPreference = UserPreference.getInstance(this);
        loader = CustomLoader.getInstance();
        databaseHelper = new DatabaseHelper(this);
        loginDetails = databaseHelper.getUserDetails();
        tv_user_details = postBinding.etUserDetails;
        dialog = CustomAlertDialog.getInstance(this);
        inputValidator = InputFieldValidator.getInstance(this);
        tv_user_details.setText(loginDetails.getName() + " " + "is at" + " " + GetCurrentLocation.getCurrentLocation(this));
        bt_post.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        back.setOnClickListener(this);

    }
//    private void initializePlayer() {
//        exoPlayerView = findViewById(R.id.exoPlayerView);
//    }

//    private void initializeHLSPlayer(){
//        DefaultTrackSelector trackSelector = new DefaultTrackSelector(this, new AdaptiveTrackSelection.Factory());
//        DefaultTrackSelector.Parameters trackSelectorParameters = trackSelector.buildUponParameters
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back: {
                finish();
                break;
            }
            case R.id.iv_camera: {
//                dialog.choosePostImage(this);
                dialog.choosePost(this);
                break;
            }
            case R.id.bt_share: {
                if (NetworkUtils.getConnectivityStatusString(CreatePostActivity.this)) {
                    //  loader.showLoading(CreatePostActivity.this, "Please wait. We are sharing your post.");
                    if (!TextUtils.isEmpty(path) | !TextUtils.isEmpty(picturePath)) {
                        loader.showLoading(CreatePostActivity.this, "Please wait. Processing your request. ");
                        uploadImage();
                    } else {
                        int errorType = inputValidator.isInputPostStringNull(et_post_text.getText().toString());
                        if (errorType == -1) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(ll_post_params.getWindowToken(), 0);
                            JsonObject object = new JsonObject();
                            object.addProperty("content", et_post_text.getText().toString());
                            loader.showLoading(CreatePostActivity.this, "Please wait. Processing your request.");
                            createPostApi(object);
                        } else {
                            startActivity(new Intent(CreatePostActivity.this, NetworkIssueActivity.class));
                            finish();
                        }
                    }
                }
                break;
            }

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", "");
        return Uri.parse(path);
    }

    // Method to show uploading alert
    private void showUploadingAlert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                builder.setMessage("Uploading video...");
                builder.setCancelable(false); // Set to true if you want to allow canceling
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle the cancel action according to your logic
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Method to dismiss uploading alert


//    public String getRealPathFromURI(Uri uri) {
//        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        return cursor.getString(idx);
//    }
    private void setThumbnail(String videoPath) {
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        ImageView ivThumbnail = findViewById(R.id.iv_share_image); // Assuming 'iv_share_image' is your ImageView ID
        if (thumbnail != null) {
            ivThumbnail.setImageBitmap(thumbnail);
        } else {
            // Handle the scenario where thumbnail generation fails or returns null
            ivThumbnail.setImageResource(R.drawable.app_icon); // Set a default thumbnail or error image
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            System.out.println("Checking the intent value from gallery::" + resultCode + " " + requestCode);
//            System.out.println("Checking the intent value from gallery::" + data + " ");
//            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//                imageList = new ArrayList<>();
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                ll_image_layout.setVisibility(View.VISIBLE);
//                Glide.with(this).load("file:" + path).into(iv_share_image);
//                imageList.add(path);
//                //   Log.d("TAG", "onActivityResult: " + data.getData() +"\n"+file);
//                //    path=getPathFromUri(outPutfileUri);
//            } else if (requestCode == GALLERY_REQUEST && data != null) {
//                imageList = new ArrayList<>();
//                Uri image = data.getData();
//                ll_image_layout.setVisibility(View.VISIBLE);
//                iv_share_image.setImageURI(image);
//                finalFile = new File(getRealPathFromURI(image));
//                imagePath = finalFile.getPath();
//                picturePath = imagePath;
//                imageList.add(picturePath);
//                System.out.println("Getting the path::" + finalFile.getPath());
//            } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
//                Uri videoUri = data.getData();
//                // Assuming getRealPathFromURI is updated to handle video URIs properly
//                videoPath = getRealPathFromURI(videoUri);
//                Log.d("TAG", "onActivityResult: " + videoPath);
//                showUploadingAlert();
//            }
//            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
//                Uri selectedImageUri = data.getData();
//
//                // OI FILE Manager
//                String filemanagerstring = selectedImageUri.getPath();
//
//
//                // Get the video from data
//                Uri selectedVideo = data.getData();
//                String[] filePathColumn = { MediaStore.Video.Media.DATA };
//                Cursor cursor = getContentResolver().query(selectedVideo,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                decodableString = cursor.getString(columnIndex);
//                cursor.close();
//                File file = new File(decodableString);
//                Log.i("mok","done");
//
//                // MEDIA GALLERY
//                path = getPath(selectedImageUri);
//                if (path != null) {
//                    Log.d("TAG", "onActivityResult: "+file);
//                    Log.d("TAG", "onActivityResult: "+filemanagerstring);
//                    Log.d("TAG", "onActivityResult: "+path);
//
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case CAMERA_REQUEST:
                imageList = new ArrayList<>();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ll_image_layout.setVisibility(View.VISIBLE);
                Glide.with(this).load("file:" + path).into(iv_share_image);
                imageList.add(path);
                //   Log.d("TAG", "onActivityResult: " + data.getData() +"\n"+file);
                //    path=getPathFromUri(outPutfileUri);
                        break;

                    case GALLERY_REQUEST:
                        // Handle gallery selection
                        Uri imageUri = data.getData();
                        if (imageUri != null) {
                            String imagePath = getRealPathFromURI(imageUri);
                            setThumbnail(imagePath);
                        }
                        break;

                    case CAMERA_VIDEO:
                        // Assuming you already have 'videoPath' from the intent
                        if (data != null && data.getData() != null) {
                            String videoPath = getRealPathFromURI(data.getData());
                            setThumbnail(videoPath);
                        }
                        break;

                    case REQUEST_TAKE_GALLERY_VIDEO:
                 Uri selectedImageUri = data.getData();
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();
                // Get the video from data
                Uri selectedVideo = data.getData();
                String[] filePathColumn = { MediaStore.Video.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                decodableString = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(decodableString);
                Log.i("mok","done");

                // MEDIA GALLERY
                path = getPath(selectedImageUri);
                        setThumbnail(path);
                if (path != null) {
                    Log.d("TAG", "onActivityResult: "+file);
                    Log.d("TAG", "onActivityResult: "+filemanagerstring);
                    Log.d("TAG", "onActivityResult: "+path);
                }
                  break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            System.out.println("Checking the intent value from gallery::" + resultCode + " " + requestCode);
//            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//                imageList = new ArrayList<>();
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                ll_image_layout.setVisibility(View.VISIBLE);
//                Glide.with(this).load("file:" + path).into(iv_share_image);
//                imageList.add(path);
//            } else if (requestCode == GALLERY_REQUEST && data != null) {
//                imageList = new ArrayList<>();
//                Uri image = data.getData();
//                ll_image_layout.setVisibility(View.VISIBLE);
//                iv_share_image.setImageURI(image);
//                finalFile = new File(getRealPathFromURI(image));
//                imagePath = finalFile.getPath();
//                picturePath = imagePath;
//                imageList.add(picturePath);
//                System.out.println("Getting the path::" + finalFile.getPath());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    public void createPostApi(JsonObject object) {
        postModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(CreatePostViewModel.class);
        postModel.initiateCreatePostProcess(CreatePostActivity.this, object, loginDetails.getId()).observe(CreatePostActivity.this, new Observer<CreatePostResponse>() {
            @Override
            public void onChanged(CreatePostResponse homeFeedResponse) {
                loader.hideLoading();
                postModel = null;
                if (homeFeedResponse != null) {
                    loader.showSnackBar(CreatePostActivity.this, homeFeedResponse.getMessage(), true);
                } else {
                    loader.showSnackBar(CreatePostActivity.this, ErrorReponseParser.errorMsg, true);
                }
            }
        });
    }

//    public void uploadImage() {
//        uploadImageModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(PostImageUploadModel.class);
//        RequestBody requestFile = null;
//        MultipartBody.Part[] surveyImagesParts = null;
//        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), et_post_text.getText().toString());
//        if (imageList != null && imageList.size() > 0) {
//            surveyImagesParts = new MultipartBody.Part[imageList.size()];
//            for (int i = 0; i < imageList.size(); i++) {
//                File file = new File(imageList.get(i));
//                requestFile = RequestBody.create(MediaType.parse("images/jpg"), file);
//                surveyImagesParts[i] = MultipartBody.Part.createFormData("images[]", file.getName(), requestFile);
//                Log.e("requestFile", requestFile.toString());
//            }
//        }
//        uploadImageModel.initiateUploadPostedImageProcess(CreatePostActivity.this, surveyImagesParts, post)
//                .observe(CreatePostActivity.this, new Observer<ImagePostResponse>() {
//                    @Override
//                    public void onChanged(ImagePostResponse posteImageResponse) {
//                        uploadImageModel = null;
//                        loader.hideLoading();
//                       path = "";
//                        if (posteImageResponse != null) {
//                            loader.showSnackBar(CreatePostActivity.this, posteImageResponse.getMessage(), true);
//                        } else {
//                            loader.showSnackBar(CreatePostActivity.this, ErrorReponseParser.errorMsg, false);
//                        }
//                    }
//                });
//    }

    public void uploadImage() {
        uploadImageModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(PostImageUploadModel.class);
//        RequestBody requestFile = null;
//        MultipartBody.Part[] surveyImagesParts = null;
//        if (imageList != null && imageList.size() > 0) {
//            surveyImagesParts = new MultipartBody.Part[imageList.size()];
//            for (int i = 0; i < imageList.size(); i++) {
//                File file = new File(imageList.get(i));
//                requestFile = RequestBody.create(MediaType.parse("images/jpg"), file);
//                surveyImagesParts[i] = MultipartBody.Part.createFormData("images[]", file.getName(), requestFile);
//                Log.e("requestFile", requestFile.toString());
//            }
//        }
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), et_post_text.getText().toString());

        File file = new File(path);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part[] imageToUpload = new MultipartBody.Part[]{MultipartBody.Part.createFormData("images[]", file.getName(), requestBody)};
        MultipartBody.Part[] videoToUpload = new MultipartBody.Part[]{MultipartBody.Part.createFormData("videos[]", file.getName(), requestBody)};

        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        uploadImageModel.initiateUploadPostedImageProcess(CreatePostActivity.this, imageToUpload,videoToUpload, post)
                .observe(CreatePostActivity.this, new Observer<ImagePostResponse>() {
                    @Override
                    public void onChanged(ImagePostResponse posteImageResponse) {
                        uploadImageModel = null;
                        loader.hideLoading();
                        path = "";
                        if (posteImageResponse != null) {
                            loader.showSnackBar(CreatePostActivity.this, posteImageResponse.getMessage(), true);
                        } else {
                            loader.showSnackBar(CreatePostActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }

}
