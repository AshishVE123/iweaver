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
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HandleRequestModel extends AndroidViewModel {
    MutableLiveData<HandleRequestResponse> acceptResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public HandleRequestModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<HandleRequestResponse> initiateRequestProcess(Context context, JsonObject infoRequest) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<HandleRequestResponse> call = retrofitClient.handleRequest(mPreferences.getAuthToken(), infoRequest);
        call.enqueue(new Callback<HandleRequestResponse>() {
            @Override
            public void onResponse(Call<HandleRequestResponse> call, Response<HandleRequestResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    HandleRequestResponse requestResponse = response.body();
                    acceptResponseModel.setValue(requestResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    acceptResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<HandleRequestResponse> call, Throwable t) {
                acceptResponseModel.postValue(null);
                Toast.makeText(mContext,t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return acceptResponseModel;
    }
}
