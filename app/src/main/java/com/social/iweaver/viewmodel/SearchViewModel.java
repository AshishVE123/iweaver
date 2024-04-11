package com.social.iweaver.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.searchresponse.SearchResponse;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.utils.ErrorReponseParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {
    MutableLiveData<SearchResponse> searchResponse = new MutableLiveData<>();
    private final Context mContext;
    private final UserPreference mPreferences;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mPreferences = UserPreference.getInstance(mContext);
    }

    public MutableLiveData<SearchResponse> initiateSearchProcess(Context context, String keyword) {
        EndPointInterface retrofitClient = RetrofitClient.getClient().create(EndPointInterface.class);
        Call<SearchResponse> call = retrofitClient.searchUser(mPreferences.getAuthToken(), keyword);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.code() == 200 | response.code() == 201) {
                    SearchResponse searchRes = response.body();
                    searchResponse.setValue(response.body());

                } else {
                    ErrorReponseParser parser = ErrorReponseParser.getInstance();
                    parser.getErrorResponseMessage(response);
                    searchResponse.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                searchResponse.postValue(null);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return searchResponse;
    }
}
