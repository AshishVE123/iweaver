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
import com.social.iweaver.apiresponse.userpost.ProfileFeedResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFeedViewModel extends AndroidViewModel {
    MutableLiveData<ProfileFeedResponse> userDetailResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public HomeFeedViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<ProfileFeedResponse> getUserPostDetails(Context context, String userID, int page) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<ProfileFeedResponse> call = retrofitClient.getUserPostsDetails(mPreferences.getAuthToken(), userID, page);
        call.enqueue(new Callback<ProfileFeedResponse>() {
            @Override
            public void onResponse(Call<ProfileFeedResponse> call, Response<ProfileFeedResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    ProfileFeedResponse homeFeedRes = response.body();
                    userDetailResponseModel.setValue(homeFeedRes);
                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    userDetailResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ProfileFeedResponse> call, Throwable t) {
                userDetailResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return userDetailResponseModel;
    }
}

