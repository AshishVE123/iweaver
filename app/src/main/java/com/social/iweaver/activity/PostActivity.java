package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.LikePosteponse;
import com.social.iweaver.apiresponse.postresponse.Data;
import com.social.iweaver.apiresponse.postresponse.PostResponse;
import com.social.iweaver.databinding.ActivityPostBinding;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.viewmodel.LikePostViewModel;
import com.social.iweaver.viewmodel.PostDetailsModel;

import java.util.List;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView userName, userPost, userLikeCount, userCommentCount, postedTime, title;
    private ImageView userImage, postedImage, likeView, userComment, userLike;
    private Button bt_back;
    private LikePostViewModel likeModel;
    private ActivityPostBinding binding;
    private CustomLoader loader;
    private Data data;
    private String postId;
    private PostDetailsModel postModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userImage = binding.ivUserImage;
        postedImage = binding.vpPostImage;
        userName = binding.tvUserName;
        userPost = binding.tvUserPost;
        userLikeCount = binding.tvPostLikeCount;
        userCommentCount = binding.tvPostCommentCount;
        postedTime = binding.tvUserPostTime;
        bt_back = binding.ilIncludeLayout.btBack;
        likeView = binding.ivLike;
        userComment = binding.ivComment;
        userLike = binding.ivLike;
        bt_back.setOnClickListener(this);
        userComment.setOnClickListener(this);
        userLike.setOnClickListener(this);
        title = binding.ilIncludeLayout.tvTitleText;
        title.setText("Liked Post");
        loader = CustomLoader.getInstance();
        postId = getIntent().getStringExtra("postId");

    }

    @Override
    protected void onResume() {
        super.onResume();
        callPostDetailsApi(postId);
    }

    public void callPostDetailsApi(String postId) {
        loader.showLoading(PostActivity.this, "Please wait. Processing your request.");
        postModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(PostDetailsModel.class);
        postModel.initiateInditiualPostProcess(this, postId)
                .observe((LifecycleOwner) this, new Observer<PostResponse>() {
                    @Override
                    public void onChanged(PostResponse postResponse) {

                        if (postResponse != null) {
                            data = new Data();
                            data = postResponse.getData();
                            List<String> imageList = data.getPostImage();
                            if (imageList.size() > 0) {

                                Glide.with(PostActivity.this).
                                        load(imageList.get(0)).
                                        placeholder(R.drawable.image_progress_animation).
                                        into(postedImage);
                            } else {
                                postedImage.setVisibility(View.GONE);
                            }
                            postedTime.setText(data.getPostedOn());
                            userName.setText(data.getUserName());
                            userPost.setText(data.getContent());
                            userCommentCount.setText(data.getPostCommentCount() + "");
                            userLikeCount.setText(data.getPostLikeCount() + "");

                            Glide.with(PostActivity.this).
                                    load(data.getUserImage()).
                                    placeholder(R.drawable.image_progress_animation).
                                    into(userImage);
                            if (data.getPostLikeCount() > 0) {
                                likeView.setImageResource(R.drawable.heart_liked);
                            } else {
                                likeView.setImageResource(R.drawable.ic_heart);
                            }

                        } else {
                            loader.showSnackBar(PostActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                        loader.hideLoading();
                        postModel = null;
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
            case R.id.iv_comment: {
                Intent intent = new Intent(PostActivity.this, CommentActivity.class);
                intent.putExtra("postId", String.valueOf(data.getPostId()));
                startActivity(intent);
                break;
            }
            case R.id.iv_like:
                JsonObject object = new JsonObject();
                object.addProperty("postId", String.valueOf(data.getPostId()));
                callLikeApi(object);

                break;

        }
    }

    public void callLikeApi(JsonObject object) {
        likeModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LikePostViewModel.class);
        likeModel.initiateLikeProcess(PostActivity.this, object)
                .observe((LifecycleOwner) PostActivity.this, new Observer<LikePosteponse>
                        () {
                    @Override
                    public void onChanged(LikePosteponse likePosteponse) {
                        likeModel = null;
                        if (likePosteponse != null) {
                            userLike.setImageResource(R.drawable.heart_liked);
                            userLikeCount.setText(data.getPostLikeCount() + 1 + "");
                        } else {
                            loader.showSnackBar(PostActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }
}