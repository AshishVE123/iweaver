package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.EditProfileResponse;
import com.social.iweaver.apiresponse.imagepost.ImagePostResponse;
import com.social.iweaver.apiresponse.profileimageupload.UploadProfileImage;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class PostImageUploadModel extends AndroidViewModel {
    MutableLiveData<ImagePostResponse> editProfileResponseModel = new MutableLiveData<>();
    MutableLiveData<ImagePostResponse> uploadImageResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public PostImageUploadModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

//    public MutableLiveData<ImagePostResponse> initiateUploadPostedImageProcess(Context context, @Part MultipartBody.Part[] files, RequestBody post) {
//        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
//        Call<ImagePostResponse> call = retrofitClient.postImageFeed(mPreferences.getAuthToken(), files,post);
//        call.enqueue(new Callback<ImagePostResponse>() {
//            @Override
//            public void onResponse(Call<ImagePostResponse> call, Response<ImagePostResponse> response) {
//                if (response.code() == 200 | response.code() == 201) {
//                    ImagePostResponse forgotPasswordResponse = response.body();
//                    uploadImageResponseModel.setValue(response.body());
//
//                } else {
//                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
//                    parser.getErrorResponseMessage(response);
//                    uploadImageResponseModel.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ImagePostResponse> call, Throwable t) {
//                editProfileResponseModel.postValue(null);
//                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//        return uploadImageResponseModel;
//    }

    public MutableLiveData<ImagePostResponse> initiateUploadPostedImageProcess(Context context, @Part MultipartBody.Part[] imageFiles,@Part MultipartBody.Part[] videoFiles, RequestBody post) {
        Log.d("TAG", "initiateUploadPostedImageProcess: "+imageFiles+"\n"+post);
//        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        EndPointInterface retrofitClient = RetrofitClient.getlocalClient().create(EndPointInterface.class);
        Call<ImagePostResponse> call = retrofitClient.postImageFeed(mPreferences.getAuthToken(),imageFiles, videoFiles,post);
        call.enqueue(new Callback<ImagePostResponse>() {
            @Override
            public void onResponse(Call<ImagePostResponse> call, Response<ImagePostResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    ImagePostResponse forgotPasswordResponse = response.body();
                    uploadImageResponseModel.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    uploadImageResponseModel.setValue(null);
                    Log.d("TAG", "onResponse: "+response);
                }
            }

            @Override
            public void onFailure(Call<ImagePostResponse> call, Throwable t) {
                editProfileResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("TAG", "onResponse: "+t.getMessage());

            }
        });
        return uploadImageResponseModel;
    }

}

