package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.EditProfileResponse;
import com.social.iweaver.apiresponse.profileimageupload.UploadProfileImage;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class EditProfileViewModel extends AndroidViewModel {
    MutableLiveData<EditProfileResponse> editProfileResponseModel = new MutableLiveData<>();
    MutableLiveData<UploadProfileImage> uploadImageResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<EditProfileResponse> initiateEditProfileProcess(Context context, JsonObject infoRequest, String userID) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<EditProfileResponse> call = retrofitClient.editProfileInfo(mPreferences.getAuthToken(), infoRequest);
        call.enqueue(new Callback<EditProfileResponse>() {
            @Override
            public void onResponse(Call<EditProfileResponse> call, Response<EditProfileResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    EditProfileResponse forgotPasswordResponse = response.body();
                    editProfileResponseModel.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    editProfileResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                editProfileResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return editProfileResponseModel;
    }

    public MutableLiveData<UploadProfileImage> initiateUploadImageProcess(Context context, @Part MultipartBody.Part files) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<UploadProfileImage> call = retrofitClient.userImageUpload(mPreferences.getAuthToken(), files);
        call.enqueue(new Callback<UploadProfileImage>() {
            @Override
            public void onResponse(Call<UploadProfileImage> call, Response<UploadProfileImage> response) {
                if (response.code() == 200 | response.code() == 201) {
                    UploadProfileImage forgotPasswordResponse = response.body();
                    uploadImageResponseModel.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    uploadImageResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UploadProfileImage> call, Throwable t) {
                editProfileResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return uploadImageResponseModel;
    }
}
