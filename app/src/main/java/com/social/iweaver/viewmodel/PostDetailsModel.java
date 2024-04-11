package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.postresponse.PostResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailsModel extends AndroidViewModel {
    MutableLiveData<PostResponse> postResponse = new MutableLiveData<>();
    private final Context mContext;
    private final UserPreference mPreferences;

    public PostDetailsModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<PostResponse> initiateInditiualPostProcess(Context context, String postId) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<PostResponse> call = retrofitClient.getIndivitualPostDetails(mPreferences.getAuthToken(), postId);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    postResponse.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    postResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                postResponse.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return postResponse;
    }
}