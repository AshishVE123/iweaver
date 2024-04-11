package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.CreatePostResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostViewModel extends AndroidViewModel {
    MutableLiveData<CreatePostResponse> createPostResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public CreatePostViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<CreatePostResponse> initiateCreatePostProcess(Context context, JsonObject infoRequest, String userID) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<CreatePostResponse> call = retrofitClient.createUserPost(mPreferences.getAuthToken(), userID, infoRequest);
        call.enqueue(new Callback<CreatePostResponse>() {
            @Override
            public void onResponse(Call<CreatePostResponse> call, Response<CreatePostResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    createPostResponseModel.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    createPostResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CreatePostResponse> call, Throwable t) {
                createPostResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return createPostResponseModel;
    }
}