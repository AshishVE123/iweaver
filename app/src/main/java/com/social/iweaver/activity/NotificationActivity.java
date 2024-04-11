package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.iweaver.R;
import com.social.iweaver.adapter.NotificationAdapter;
import com.social.iweaver.apiresponse.notificationresponse.Data;
import com.social.iweaver.apiresponse.notificationresponse.Datum;
import com.social.iweaver.apiresponse.notificationresponse.NotificationResponse;
import com.social.iweaver.databinding.ActivityNotificationBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.viewmodel.NotificationModel;

import java.util.List;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewClickListener {
    private TextView titleText;
    private ActivityNotificationBinding notificationBinding;
    private RecyclerView nRecyclerView;
    private NotificationAdapter nAdapter;
    private Button back;
    private CustomLoader loader;
    private List<Datum> notificationList;

    private NotificationModel notificationModel;

    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(notificationBinding.getRoot());
        titleText = notificationBinding.inIncludeLayout.tvTitleText;
        titleText.setText("Notifications");
        back = notificationBinding.inIncludeLayout.btBack;
        nRecyclerView = notificationBinding.rvNotificationList;
        back.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        nRecyclerView.setLayoutManager(linearLayoutManager);
        nRecyclerView.setHasFixedSize(true);
        nRecyclerView.setItemViewCacheSize(20);
        nRecyclerView.setDrawingCacheEnabled(true);
        nRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        callNotificationApi();


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

    public void callNotificationApi() {
        loader.showLoading(NotificationActivity.this, "Please wait. Processing your request");
        notificationModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(NotificationModel.class);
        notificationModel.initiateNotificationProcess(NotificationActivity.this)
                .observe(NotificationActivity.this, new Observer<NotificationResponse>() {
                    @Override
                    public void onChanged(NotificationResponse notificationResponse) {
                        loader.hideLoading();
                        notificationModel = null;
                        if (notificationResponse != null) {
                            Data data = new Data();
                            data = notificationResponse.getData();
                            notificationList = data.getData();
                            if (notificationList.size() > 0) {
                                nAdapter = new NotificationAdapter(NotificationActivity.this, notificationList, NotificationActivity.this);
                                nRecyclerView.setAdapter(nAdapter);
                            } else {
                                loader.showSnackBar(NotificationActivity.this, notificationResponse.getMessage(), true);
                            }
                        } else {
                            loader.showSnackBar(NotificationActivity.this, ErrorReponseParser.errorMsg, true);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        if (notificationList.get(position).getType().equalsIgnoreCase("comment")) {
            String postID = String.valueOf(notificationList.get(position).getActionId());
            Intent intent = new Intent(NotificationActivity.this, CommentActivity.class);
            intent.putExtra("postId", postID);
            startActivity(intent);
        } else if (notificationList.get(position).getType().equalsIgnoreCase("like")) {
            String postID = String.valueOf(notificationList.get(position).getActionId());
            Intent intent = new Intent(NotificationActivity.this, PostActivity.class);
            intent.putExtra("postId", postID);
            startActivity(intent);
        } else if (notificationList.get(position).getType().equalsIgnoreCase("friend_request")) {
            Intent intent = new Intent(NotificationActivity.this, RequestActivity.class);
            startActivity(intent);
        }

    }
}