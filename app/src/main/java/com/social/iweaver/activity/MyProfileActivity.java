package com.social.iweaver.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.adapter.ProfileFeedAdapter;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.userpost.Data;
import com.social.iweaver.apiresponse.userpost.Post;
import com.social.iweaver.apiresponse.userpost.ProfileFeedResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.databinding.ActivityMyProfileBinding;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.GetCurrentLocation;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.HandleRequestModel;
import com.social.iweaver.viewmodel.HomeFeedViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static MyProfileActivity instance;
    private ActivityMyProfileBinding profileBinding;
    private HomeFeedViewModel homeModel;
    private HandleRequestModel requestSendModel;
    private ImageView edit, cv_profile_image;
    private CustomLoader loader;
    private TextView titleText, location;
    private RelativeLayout cardLayout;
    private Button back, addFriend, morePost;
    private LinearLayout friendLayout;
    private TextView tv_userName, tv_aboutUs, tv_postCount, tv_friendCount;
    private LoginAttributeResponse loginResponse;
    private ProfileFeedAdapter prfileAdapter;
    private DatabaseHelper databaseHelper;
    private String requestUserId = null;
    private RecyclerView profileRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        edit = profileBinding.ivEdit;
        tv_userName = profileBinding.tvUserName;
        tv_aboutUs = profileBinding.tvAboutUs;
        profileRecyclerView = profileBinding.rvFeed;
        titleText = profileBinding.layoutToolbar.tvTitleText;
        back = profileBinding.layoutToolbar.btBack;
        tv_postCount = profileBinding.tvPostCount;
        tv_friendCount = profileBinding.tvFriendCount;
        cv_profile_image = profileBinding.cvProfileImage;
        cardLayout = profileBinding.rlCard;
        location = profileBinding.tvLocation;
        addFriend = profileBinding.btAddFriend;
        morePost = profileBinding.btMorePost;
        friendLayout = profileBinding.llFriendParam;
        morePost.setOnClickListener(this);
        titleText.setText("My Profile");
        back.setOnClickListener(this);
        cv_profile_image.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        edit.setOnClickListener(this);
        instance = this;
        addFriend.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        profileRecyclerView.setLayoutManager(linearLayoutManager);
        profileRecyclerView.setHasFixedSize(true);
        profileRecyclerView.setItemViewCacheSize(20);
        profileRecyclerView.setDrawingCacheEnabled(true);
        profileRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        databaseHelper = new DatabaseHelper(this);
        loginResponse = databaseHelper.getUserDetails();
        location.setText(GetCurrentLocation.getCurrentLocation(this));
        homeModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(HomeFeedViewModel.class);
        loader.showLoading(this, "Please wait. Retrieving your details.");
        String guestUserId = getIntent().getStringExtra("userId");
        if (NetworkUtils.getConnectivityStatusString(this)) {
            if (TextUtils.isEmpty(guestUserId)) {
                requestUserId = loginResponse.getId();
                titleText.setText("My Profile");
                edit.setVisibility(View.VISIBLE);

            } else {
                requestUserId = guestUserId;
                titleText.setText("Friend Profile");
                edit.setVisibility(View.GONE);

            }
            callUserPostRequest(requestUserId);
        } else {
            startActivity(new Intent(MyProfileActivity.this, NetworkIssueActivity.class));
            finish();
        }
        profileRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    morePost.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    public static MyProfileActivity getInstance() {
        return instance;
    }

    public void callUserPostRequest(String userID) {
        homeModel.getUserPostDetails(MyProfileActivity.this, userID, 1)
                .observe(MyProfileActivity.this, new Observer<ProfileFeedResponse>() {
                    @Override
                    public void onChanged(ProfileFeedResponse profileFeedResponse) {
                        if (profileFeedResponse != null) {
                            Data data = new Data();
                            data = profileFeedResponse.getData();
                            List<Post> post = data.getPost();
                            if (post.size() > 0) {
                                prfileAdapter = new ProfileFeedAdapter(MyProfileActivity.this, post);
                                profileRecyclerView.setAdapter(prfileAdapter);
                            }
                            tv_postCount.setText(data.getPostCount() + "");
                            tv_friendCount.setText(data.getFriendCount() + "");
                            tv_userName.setText(data.getName());
                            tv_aboutUs.setText(data.getAboutUs().toString());

                            Glide.with(MyProfileActivity.this).
                                    load(data.getUserImage()).
                                    diskCacheStrategy(DiskCacheStrategy.NONE).
                                    skipMemoryCache(true).
                                    placeholder(R.drawable.image_progress_animation).
                                    into(cv_profile_image);


                            Bitmap myImage = getBitmapFromURL(data.getUserImage());
                            Drawable dr = new BitmapDrawable(myImage);
                            cardLayout.setBackgroundDrawable(dr);
                            if (data.getIsUserFriend().equalsIgnoreCase("NO")) {
                                addFriend.setVisibility(View.VISIBLE);
                                friendLayout.setVisibility(View.GONE);

                            } else {
                                addFriend.setVisibility(View.GONE);
                                friendLayout.setVisibility(View.VISIBLE);

                            }
                            loader.hideLoading();

                        } else {
                            loader.showSnackBar(MyProfileActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }

    public void updateData() {

        loginResponse = databaseHelper.getUserDetails();
        tv_userName.setText(loginResponse.getName());
        tv_aboutUs.setText(loginResponse.getAboutUs());
        Glide.with(MyProfileActivity.this).
                load(loginResponse.getUserImage()).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                placeholder(R.drawable.image_progress_animation).
                into(cv_profile_image);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap myImage = getBitmapFromURL(loginResponse.getUserImage());
                Drawable dr = new BitmapDrawable(myImage);
                cardLayout.setBackgroundDrawable(dr);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callUserPostRequest(requestUserId);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back: {
                finish();
                break;
            }
            case R.id.iv_edit: {
                startActivity(new Intent(MyProfileActivity.this, EditProfileActivity.class));
                break;
            }

            case R.id.bt_add_friend: {
                sendFriendRequest();
                break;
            }
            case R.id.bt_more_post: {
                Intent intent = new Intent(this, UserAllPostActivity.class);
                intent.putExtra("userId", requestUserId);
                startActivity(intent);
                break;
            }

        }
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendFriendRequest() {
        loader.showLoading(MyProfileActivity.this, "Please wait. Processing your request");
        JsonObject object = new JsonObject();
        object.addProperty("status", "send");
        object.addProperty("request_to", requestUserId);
        sendRequest(object);
    }

    public void sendRequest(JsonObject object) {
        requestSendModel = ViewModelProvider.AndroidViewModelFactory.getInstance(MyProfileActivity.this.getApplication()).create(HandleRequestModel.class);
        requestSendModel.initiateRequestProcess(MyProfileActivity.this, object)
                .observe(MyProfileActivity.this, new Observer<HandleRequestResponse>() {
                    @Override
                    public void onChanged(HandleRequestResponse requestResponse) {
                        loader.hideLoading();
                        requestSendModel = null;
                        if (requestResponse != null) {
                            addFriend.setVisibility(View.GONE);
                            loader.showSnackBar(MyProfileActivity.this, requestResponse.getMessage(), false);
                        } else {
                            loader.showSnackBar(MyProfileActivity.this, ErrorReponseParser.errorMsg, true);
                        }
                    }
                });
    }


}
