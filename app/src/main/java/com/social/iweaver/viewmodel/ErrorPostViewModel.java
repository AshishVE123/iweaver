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
import com.social.iweaver.apiresponse.LogoutResponse;
import com.social.iweaver.apiresponse.ReportPostApiResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ErrorPostViewModel extends AndroidViewModel {
    MutableLiveData<ReportPostApiResponse> blockResponseModel = new MutableLiveData<>();
    private final UserPreference mUserPreference;
    private final Context mContext;

    public ErrorPostViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mUserPreference = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<ReportPostApiResponse> initiateReportPostProcess(Context context, JsonObject body) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        System.out.println("Checking the token::" + mUserPreference.getAuthToken());
        Call<ReportPostApiResponse> call = retrofitClient.repostPostApi(mUserPreference.getAuthToken(),body);
        call.enqueue(new Callback<ReportPostApiResponse>() {
            @Override
            public void onResponse(Call<ReportPostApiResponse> call, Response<ReportPostApiResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    ReportPostApiResponse logoutResponse = response.body();
                    blockResponseModel.setValue(logoutResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    blockResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ReportPostApiResponse> call, Throwable t) {
                blockResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return blockResponseModel;
    }
}
