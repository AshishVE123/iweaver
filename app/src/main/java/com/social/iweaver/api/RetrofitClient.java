package com.social.iweaver.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.social.iweaver.constants.AppConstants;
import com.social.iweaver.database.UserPreference;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Context mContext;
    private static UserPreference userPreference;

    /**
     *
     * @param context
     */
    public RetrofitClient(Context context) {
        mContext = context;
        userPreference = UserPreference.getInstance(mContext);
    }

    /**
     *
     * @return Retrofit client
     */
    public static Retrofit getClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
               /* .callTimeout(AppConstants.TIMER_CONNECTION,TimeUnit.MINUTES)*/
                .connectTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.WRITE_TIMEOUT, TimeUnit.MINUTES)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .method(originalRequest.method(), originalRequest.body())
                                .build();

                        Response httpResp = chain.proceed(newRequest);

                        return httpResp;
                    }
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.PRODUCTION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpClient)
                .build();

        return retrofit;
    }

    public static Retrofit getlocalClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                /* .callTimeout(AppConstants.TIMER_CONNECTION,TimeUnit.MINUTES)*/
                .connectTimeout(AppConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.WRITE_TIMEOUT, TimeUnit.MINUTES)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .method(originalRequest.method(), originalRequest.body())
                                .build();

                        Response httpResp = chain.proceed(newRequest);

                        return httpResp;
                    }
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.STAGING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okhttpClient)
                .build();

        return retrofit;
    }
}
