package com.social.iweaver.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.usercomment.CommentResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetAllCommentModel extends AndroidViewModel {
    MutableLiveData<CommentResponse> commentResponseModel = new MutableLiveData<>();
    private final UserPreference mPreferences;
    private final Context mContext;

    public GetAllCommentModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<CommentResponse> initiateAllCommentProcess(Activity activity, String postId, int page) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<CommentResponse> call = retrofitClient.getAllComment(mPreferences.getAuthToken(), postId, page);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200 | response.code() == 201) {
                        CommentResponse commentList = response.body();
                        commentResponseModel.setValue(commentList);
                    }
                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    commentResponseModel.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                commentResponseModel.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return commentResponseModel;
    }
}

