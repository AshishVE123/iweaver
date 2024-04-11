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
import com.social.iweaver.apiresponse.loginresponse.LoginResponse;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    MutableLiveData<LoginResponse> loginResponse = new MutableLiveData<>();
    private final Context mContext;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
    }

    public MutableLiveData<LoginResponse> initiateLoginProcess(Context context, JsonObject loginReq) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<LoginResponse> call = retrofitClient.login(loginReq);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    LoginResponse apiResponse = response.body();
                    loginResponse.setValue(apiResponse);


                } else {
                    ErrorReponseParser errorParser = ErrorReponseParser.getInstance();
                    errorParser.getErrorResponseMessage(response);
                    loginResponse.setValue(null);

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResponse.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return loginResponse;
    }
}
