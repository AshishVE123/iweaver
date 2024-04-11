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
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends AndroidViewModel {
    MutableLiveData<ForgotPasswordResponse> forgotPasswordResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<ForgotPasswordResponse> initiateForgotPasswordProcess(Context context, JsonObject loginReq) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<ForgotPasswordResponse> call = retrofitClient.forgotPassword(mPreferences.getAuthToken(), loginReq);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                    forgotPasswordResponseModel.setValue(forgotPasswordResponse);

                } else {
                    ErrorReponseParser errorParser = ErrorReponseParser.getInstance();
                    errorParser.getErrorResponseMessage(response);
                    forgotPasswordResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                forgotPasswordResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return forgotPasswordResponseModel;
    }
}
