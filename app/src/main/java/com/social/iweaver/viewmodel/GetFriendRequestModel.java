package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.friendrequest.FriendRequestResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetFriendRequestModel extends AndroidViewModel {
    MutableLiveData<FriendRequestResponse> getRequest = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public GetFriendRequestModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<FriendRequestResponse> initiateFriendRequestProcess(Context context, String userId, int page) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<FriendRequestResponse> call = retrofitClient.getAllRequest(mPreferences.getAuthToken(), page);
        call.enqueue(new Callback<FriendRequestResponse>() {
            @Override
            public void onResponse(Call<FriendRequestResponse> call, Response<FriendRequestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FriendRequestResponse requestResponse = response.body();
                    System.out.println("Checking the request reponse:" + requestResponse.getMessage());
                    getRequest.setValue(requestResponse);

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    getRequest.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<FriendRequestResponse> call, Throwable t) {
                getRequest.postValue(null);
                System.out.println("Checking the request reponse:" + t.getMessage());
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return getRequest;
    }
}
