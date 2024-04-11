package com.social.iweaver.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.addcomment.AddCommentResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCommentModel extends AndroidViewModel {
    MutableLiveData<AddCommentResponse> addCommentResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public AddCommentModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<AddCommentResponse> initiateAddCommentProcess(Activity activity, JsonObject object) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<AddCommentResponse> call = retrofitClient.addComment(mPreferences.getAuthToken(), object);
        call.enqueue(new Callback<AddCommentResponse>() {
            @Override
            public void onResponse(Call<AddCommentResponse> call, Response<AddCommentResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 | response.code() == 201) {
                        AddCommentResponse addCommentApiResponse = response.body();
                        addCommentResponseModel.setValue(addCommentApiResponse);
                    }
                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    addCommentResponseModel.setValue(null);


                }
            }

            @Override
            public void onFailure(Call<AddCommentResponse> call, Throwable t) {
                addCommentResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return addCommentResponseModel;
    }
}

