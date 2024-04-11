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
import com.social.iweaver.apiresponse.RegistrationResponse;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends AndroidViewModel {

    MutableLiveData<RegistrationResponse> registerResponse = new MutableLiveData<>();
    private final Context mContext;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();

    }

    public MutableLiveData<RegistrationResponse> initiateRegisterProcess(Context context, JsonObject registerObject) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<RegistrationResponse> call = retrofitClient.signUp(registerObject);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.code() == 200 || response.code() == 201) {
                    registerResponse.setValue(response.body());
                } else {
                    ErrorReponseParser errorReponseParser = ErrorReponseParser.getInstance();
                    errorReponseParser.getErrorResponseMessage(response);
                    registerResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                registerResponse.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return registerResponse;
    }

}
