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
import com.social.iweaver.apiresponse.ForgotPasswordResponse;
import com.social.iweaver.apiresponse.appversion.AppVersion;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AppVersionModel extends AndroidViewModel {
    MutableLiveData<AppVersion> appVersionModel = new MutableLiveData<>();
    private final Context mContext;
    private final UserPreference mPreferences;
    public AppVersionModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<AppVersion> initiateAppVersionProcess(Context context, JsonObject versionReq) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<AppVersion> call = retrofitClient.postAppVersion(mPreferences.getAuthToken(), versionReq);
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                if (response.code() == 200 | response.code() == 201) {
                    AppVersion appVersionRespo = response.body();
                    appVersionModel.setValue(appVersionRespo);

                } else {
                    ErrorReponseParser errorParser = ErrorReponseParser.getInstance();
                    errorParser.getErrorResponseMessage(response);
                    appVersionModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                appVersionModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return appVersionModel;
    }
}
