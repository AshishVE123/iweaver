package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.GetUserDetailsResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsViewModel extends AndroidViewModel {
    MutableLiveData<GetUserDetailsResponse> userDetailResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public UserDetailsViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<GetUserDetailsResponse> getUserDetails(Context context, String userID) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<GetUserDetailsResponse> call = retrofitClient.getUserDetails(mPreferences.getAuthToken(), userID);
        call.enqueue(new Callback<GetUserDetailsResponse>() {
            @Override
            public void onResponse(Call<GetUserDetailsResponse> call, Response<GetUserDetailsResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    GetUserDetailsResponse forgotPasswordResponse = response.body();
                    userDetailResponseModel.setValue(response.body());
                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    userDetailResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetUserDetailsResponse> call, Throwable t) {
                userDetailResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return userDetailResponseModel;
    }
}
