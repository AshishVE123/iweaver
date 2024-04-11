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
import com.social.iweaver.apiresponse.SentFriendRequestResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFriendRequestViewModel extends AndroidViewModel {
    MutableLiveData<SentFriendRequestResponse> sendRequest = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public SendFriendRequestViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<SentFriendRequestResponse> initiateRequestProcess(Context context, JsonObject object) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<SentFriendRequestResponse> call = retrofitClient.sendReuest(mPreferences.getAuthToken(), object);
        call.enqueue(new Callback<SentFriendRequestResponse>() {
            @Override
            public void onResponse(Call<SentFriendRequestResponse> call, Response<SentFriendRequestResponse> response) {
                /*if (response.code() == 200 | response.code() == 201) {*/
                if (response.isSuccessful()) {
                    SentFriendRequestResponse forgotPasswordResponse = response.body();
                    sendRequest.setValue(response.body());

                } else {
                    ErrorReponseParser psrser = ErrorReponseParser.getInstance();
                    psrser.getErrorResponseMessage(response);
                    sendRequest.setValue(null);

                }
            }

            @Override
            public void onFailure(Call<SentFriendRequestResponse> call, Throwable t) {
                sendRequest.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return sendRequest;
    }
}