package com.social.iweaver.activity;

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
import com.social.iweaver.adapter.RequestAdapter;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.friendrequest.Data;
import com.social.iweaver.apiresponse.friendrequest.Datum;
import com.social.iweaver.apiresponse.friendrequest.FriendRequestResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.databinding.ActivityRequestBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.viewmodel.GetFriendRequestModel;

import java.util.List;

public class RequestActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewClickListener {
    private ActivityRequestBinding requestListBinding;
    private RecyclerView requestList;
    private TextView title;
    private LoginAttributeResponse loginDetails;
    private CustomLoader loader;
    private Button back;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseHelper databaseHelper;
    private GetFriendRequestModel requestModel;

    private int callingPage = 1;
    private List<Datum> requestListData = null;

    private List<Datum> finalRequestList = null;

    private RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestListBinding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(requestListBinding.getRoot());
        requestList = requestListBinding.rvRequestList;
        title = requestListBinding.llToolbareParams.tvTitleText;
        back = requestListBinding.llToolbareParams.btBack;
        title.setText("Friend Request");
        loader = CustomLoader.getInstance();
        databaseHelper = new DatabaseHelper(this);
        loginDetails = databaseHelper.getUserDetails();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        requestList.setLayoutManager(linearLayoutManager);
        requestList.setHasFixedSize(true);
        requestList.setItemViewCacheSize(20);
        requestList.setDrawingCacheEnabled(true);
        requestList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        back.setOnClickListener(this);
        loader.showLoading(this, "Please wait. Processing your request..");
        callFriendRequestApi();
        requestList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (callingPage != -1) {
                        // loader.showLoading(HomeActivity.this, "Please wait. Retrieving more posts.");
                        //  Toast.makeText(HomeActivity.this, page+1+""+"RecyclerView Page test", Toast.LENGTH_SHORT).show();
                        callFriendRequestApi();
                    } else {
                        //
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
        }
    }


    @Override
    public void onItemClick(int position) {

    }

    public void callFriendRequestApi() {
        requestModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(GetFriendRequestModel.class);
        requestModel.initiateFriendRequestProcess(RequestActivity.this, loginDetails.getId(), callingPage)
                .observe(RequestActivity.this, new Observer<FriendRequestResponse>() {
                    @Override
                    public void onChanged(FriendRequestResponse requestResponse) {
                        loader.hideLoading();
                        requestModel = null;
                        if (requestResponse != null) {
                            Data data = new Data();
                            data = requestResponse.getData();
                            callingPage = callingPage + data.getCurrentPage();
                            requestListData = data.getData();
                            if (requestListData.size() > 0) {
                                if (finalRequestList == null) {
                                    finalRequestList = requestListData;
                                } else {
                                    finalRequestList.addAll(requestListData);
                                }
                                adapter = new RequestAdapter(RequestActivity.this, finalRequestList);
                                requestList.setAdapter(adapter);
                            } else {
                                callingPage = -1;
                                loader.showSnackBar(RequestActivity.this, requestResponse.getMessage(), true);
                            }
                        } else {
                            loader.showSnackBar(RequestActivity.this, ErrorReponseParser.errorMsg, true);
                        }

                    }
                });
    }
}