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
import com.social.iweaver.apiresponse.LikePosteponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikePostViewModel extends AndroidViewModel {
    MutableLiveData<LikePosteponse> likeResponseModel = new MutableLiveData<>();
    private final UserPreference mUserPreference;
    private final Context mContext;

    public LikePostViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUserPreference = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<LikePosteponse> initiateLikeProcess(Context context, JsonObject object) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        System.out.println("Checking the token::" + mUserPreference.getAuthToken());
        Call<LikePosteponse> call = retrofitClient.likePost(mUserPreference.getAuthToken(), object);
        call.enqueue(new Callback<LikePosteponse>() {
            @Override
            public void onResponse(Call<LikePosteponse> call, Response<LikePosteponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    LikePosteponse logoutResponse = response.body();
                    likeResponseModel.setValue(logoutResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    likeResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<LikePosteponse> call, Throwable t) {
                likeResponseModel.postValue(null);
                Toast.makeText(mContext,t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return likeResponseModel;
    }
}

