package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.adapter.CommentAdapter;
import com.social.iweaver.apiresponse.addcomment.AddCommentResponse;
import com.social.iweaver.apiresponse.usercomment.CommentResponse;
import com.social.iweaver.apiresponse.usercomment.Data;
import com.social.iweaver.apiresponse.usercomment.Datum;
import com.social.iweaver.databinding.ActivityCommentBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.AddCommentModel;
import com.social.iweaver.viewmodel.GetAllCommentModel;

import java.util.List;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewClickListener {
    private String postId;
    private List<Datum> commentList;
    private List<Datum> finalCommentList;
    private ActivityCommentBinding commentBinding;
    private CommentAdapter commentAdapter;
    private RecyclerView commentView;
    private TextView titleText;
    private EditText addComment;
    private Button submitComment;
    private int callingPage = 1;
    private Button back;
    private CustomLoader loader;
    private GetAllCommentModel commentModel;
    private AddCommentModel addCommentModel;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(commentBinding.getRoot());
        titleText = commentBinding.includeLayout.tvTitleText;
        titleText.setText("Comments");
        back = commentBinding.includeLayout.btBack;
        loader = CustomLoader.getInstance();
        commentView = commentBinding.rvCommentList;
        addComment = commentBinding.etAddComment;
        submitComment = commentBinding.btSubmitComment;
        submitComment.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentView.setLayoutManager(linearLayoutManager);
        commentView.setHasFixedSize(true);
        commentView.setItemViewCacheSize(20);
        commentView.setDrawingCacheEnabled(true);
        commentView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        back.setOnClickListener(this);
        postId = getIntent().getStringExtra("postId");
        commentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (callingPage != -1) {
                        loader.hideLoading();
                        callCommentApi(postId);
                    } else {

                    }

                }
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
            case R.id.bt_submit_comment: {
                if (TextUtils.isEmpty(addComment.getText().toString())) {
                    addComment.setError("Please enter comment");
                    addComment.requestFocus();
                } else {
                    if (NetworkUtils.getConnectivityStatusString(CommentActivity.this)) {
                        JsonObject object = new JsonObject();
                        object.addProperty("postId", postId);
                        object.addProperty("comment", addComment.getText().toString());
                        addComment.setText("");
                        submitCommentApi(object);
                    } else {
                        startActivity(new Intent(CommentActivity.this, NetworkIssueActivity.class));
                    }
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.getConnectivityStatusString(CommentActivity.this)) {

            finalCommentList = null;
            commentList = null;
            callingPage = 1;
            callCommentApi(postId);
        } else {
            startActivity(new Intent(CommentActivity.this, NetworkIssueActivity.class));
        }
    }

    private void callCommentApi(String postId) {
        System.out.println("Chekcking the loader:::" + "loader initiated");
        loader.showLoading(this, "Retrieving your comments. Please wait..");
        commentModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(GetAllCommentModel.class);
        commentModel.initiateAllCommentProcess(CommentActivity.this, postId, callingPage).observe(CommentActivity.this, new Observer<CommentResponse>() {
            @Override
            public void onChanged(CommentResponse commentListResponse) {
                loader.hideLoading();
                commentModel = null;
                if (commentListResponse != null) {
                    Data data = new Data();
                    data = commentListResponse.getData();
                    commentList = data.getData();
                    if (commentList.size() > 0) {
                        callingPage = data.getCurrentPage() + 1;
                        if (finalCommentList == null) {
                            finalCommentList = commentList;

                        } else {
                            finalCommentList.addAll(commentList);

                        }
                        commentAdapter = new CommentAdapter(CommentActivity.this, finalCommentList, CommentActivity.this);
                        commentView.setAdapter(commentAdapter);
                    } else {
                        loader.showSnackBar(CommentActivity.this, commentListResponse.getMessage(), false);
                        callingPage = -1;
                    }

                } else {
                    callingPage = -1;
                    loader.showSnackBar(CommentActivity.this, ErrorReponseParser.errorMsg, true);
                }

            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    public void submitCommentApi(JsonObject object) {
        loader.showLoading(CommentActivity.this, "Please wait. Submitting your comment");
        addCommentModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(AddCommentModel.class);
        addCommentModel.initiateAddCommentProcess(CommentActivity.this, object).observe(CommentActivity.this, new Observer<AddCommentResponse>() {
            @Override
            public void onChanged(AddCommentResponse commentListResponse) {
                loader.hideLoading();
                addCommentModel = null;
                if (commentListResponse != null) {
                    callingPage = 1;
                    finalCommentList = null;
                    commentList = null;
                    callCommentApi(postId);
                    loader.showSnackBar(CommentActivity.this, commentListResponse.getMessage(), false);
                } else {
                    loader.showSnackBar(CommentActivity.this, ErrorReponseParser.errorMsg, false);
                }
            }
        });
    }
}