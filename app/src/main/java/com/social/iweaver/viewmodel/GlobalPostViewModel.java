package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.globalpost.GlobalPostResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalPostViewModel extends AndroidViewModel {
    MutableLiveData<GlobalPostResponse> userDetailResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public GlobalPostViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<GlobalPostResponse> getGlobalPostDetails(Context context, int page) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<GlobalPostResponse> call = retrofitClient.getGlobalDetails(mPreferences.getAuthToken(), page);
        call.enqueue(new Callback<GlobalPostResponse>() {
            @Override
            public void onResponse(Call<GlobalPostResponse> call, Response<GlobalPostResponse> response) {
                if (response.code() == 200 | response.code() == 201 | response.isSuccessful()) {
                    GlobalPostResponse homeFeedRes = response.body();
                    userDetailResponseModel.setValue(homeFeedRes);
                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    userDetailResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GlobalPostResponse> call, Throwable t) {
                userDetailResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return userDetailResponseModel;
    }
}

