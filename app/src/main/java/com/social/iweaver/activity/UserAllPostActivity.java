package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.iweaver.R;
import com.social.iweaver.adapter.ProfileFeedAdapter;
import com.social.iweaver.apiresponse.userpost.Data;
import com.social.iweaver.apiresponse.userpost.Post;
import com.social.iweaver.apiresponse.userpost.ProfileFeedResponse;
import com.social.iweaver.databinding.ActivityUserAllPostBinding;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.HomeFeedViewModel;

import java.util.List;

public class UserAllPostActivity extends AppCompatActivity implements View.OnClickListener {
    private Button back;
    private ActivityUserAllPostBinding userAllPostBinding;
    private RecyclerView postList;
    private HomeFeedViewModel homeModel;
    private ProfileFeedAdapter prfileAdapter;
    private CustomLoader loader;
    private List<Post> post;
    private List<Post> finalPostList;
    private TextView title;
    private int callingPage = 2;

    private String userID;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAllPostBinding = ActivityUserAllPostBinding.inflate(getLayoutInflater());
        setContentView(userAllPostBinding.getRoot());
        postList = userAllPostBinding.rvAllPostList;
        title = userAllPostBinding.llToolParams.tvTitleText;
        back = userAllPostBinding.llToolParams.btBack;
        back.setOnClickListener(this);
        title.setText("More Posts");
        loader = CustomLoader.getInstance();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        postList.setLayoutManager(linearLayoutManager);
        postList.setHasFixedSize(true);
        postList.setItemViewCacheSize(20);
        postList.setDrawingCacheEnabled(true);
        postList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        userID = getIntent().getStringExtra("userId");
        postList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(callingPage!=-1) {
                        callingPage = callingPage + 1;
                        callUserPostRequest(userID, callingPage);
                    }
                    else{
                        //
                    }

                }
            }
        });


    }

    public void callUserPostRequest(String userID, int page) {
        homeModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(HomeFeedViewModel.class);
        loader.showLoading(UserAllPostActivity.this, "Please wait. Processing your request..");
        homeModel.getUserPostDetails(UserAllPostActivity.this, userID, page)
                .observe(UserAllPostActivity.this, new Observer<ProfileFeedResponse>() {
                    @Override
                    public void onChanged(ProfileFeedResponse profileFeedResponse) {
                        loader.hideLoading();
                        homeModel = null;
                        if (profileFeedResponse != null) {
                            Data data = new Data();
                            data = profileFeedResponse.getData();
                            post = data.getPost();
                            if (post.size() > 0) {
                                if (finalPostList == null) {
                                    finalPostList = post;
                                } else {
                                    finalPostList.addAll(post);
                                }
                                prfileAdapter = new ProfileFeedAdapter(UserAllPostActivity.this, finalPostList);
                                postList.setAdapter(prfileAdapter);
                            } else {
                                callingPage = -1;
                                loader.showSnackBar(UserAllPostActivity.this, profileFeedResponse.getMessage(), false);
                            }
                        } else {
                            loader.showSnackBar(UserAllPostActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.getConnectivityStatusString(UserAllPostActivity.this)) {
            callingPage = 2;
            finalPostList = null;
            post = null;
            callUserPostRequest(userID, callingPage);
        } else {
            startActivity(new Intent(UserAllPostActivity.this, NetworkIssueActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back: {
                finish();
                break;
            }
        }
    }
}