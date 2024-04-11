package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.notificationresponse.NotificationResponse;
import com.social.iweaver.apiresponse.searchresponse.SearchResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationModel extends AndroidViewModel {
    MutableLiveData<NotificationResponse> notificationResponse = new MutableLiveData<>();
    private final Context mContext;
    private final UserPreference mPreferences;

    public NotificationModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<NotificationResponse> initiateNotificationProcess(Context context) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<NotificationResponse> call = retrofitClient.getAllNotification(mPreferences.getAuthToken());
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    NotificationResponse searchRes = response.body();
                    notificationResponse.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    notificationResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                notificationResponse.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return notificationResponse;
    }
}
