package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.R;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.LogoutResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutViewModel extends AndroidViewModel {
    MutableLiveData<LogoutResponse> logoutResponseModel = new MutableLiveData<>();
    private final UserPreference mUserPreference;
    private final Context mContext;

    public LogoutViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUserPreference = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<LogoutResponse> initiateLogoutProcess(Context context) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        System.out.println("Checking the token::" + mUserPreference.getAuthToken());
        Call<LogoutResponse> call = retrofitClient.logoutUser(mUserPreference.getAuthToken());
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    LogoutResponse logoutResponse = response.body();
                    logoutResponseModel.setValue(logoutResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    logoutResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                logoutResponseModel.postValue(null);
                Toast.makeText(mContext,t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return logoutResponseModel;
    }
}
