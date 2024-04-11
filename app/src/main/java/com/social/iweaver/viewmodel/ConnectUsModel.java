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
import com.social.iweaver.apiresponse.ConnectUsResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectUsModel extends AndroidViewModel {
    MutableLiveData<ConnectUsResponse> dislikeResponseModel = new MutableLiveData<>();
    private final UserPreference mUserPreference;
    private final Context mContext;

    public ConnectUsModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUserPreference = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<ConnectUsResponse> initiateConnectProcess(Context context, JsonObject object) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<ConnectUsResponse> call = retrofitClient.writeUs(mUserPreference.getAuthToken(), object);
        call.enqueue(new Callback<ConnectUsResponse>() {
            @Override
            public void onResponse(Call<ConnectUsResponse> call, Response<ConnectUsResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    ConnectUsResponse connectUsResponse = response.body();
                    dislikeResponseModel.setValue(connectUsResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    dislikeResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ConnectUsResponse> call, Throwable t) {
                dislikeResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return dislikeResponseModel;
    }
}

