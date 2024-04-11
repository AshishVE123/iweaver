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
import com.social.iweaver.apiresponse.friendlist.FriendListResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListModel extends AndroidViewModel {
    MutableLiveData<FriendListResponse> friendListResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public FriendListModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<FriendListResponse> initiateFriendListProcess(Context context, String userID, int page) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<FriendListResponse> call = retrofitClient.getFriendList(mPreferences.getAuthToken(),page);
        call.enqueue(new Callback<FriendListResponse>() {
            @Override
            public void onResponse(Call<FriendListResponse> call, Response<FriendListResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    FriendListResponse forgotPasswordResponse = response.body();
                    friendListResponseModel.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    friendListResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<FriendListResponse> call, Throwable t) {
                friendListResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return friendListResponseModel;
    }
}
