package com.social.iweaver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.adapter.FriendListAdapter;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.friendlist.Data;
import com.social.iweaver.apiresponse.friendlist.Datum;
import com.social.iweaver.apiresponse.friendlist.FriendListResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.databinding.ActivityFriendListBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.viewmodel.FriendListModel;
import com.social.iweaver.viewmodel.HandleRequestModel;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, RecyclerViewClickListener {
    private HandleRequestModel handleRequestModel;
    private SearchView listSearch;

    private CustomLoader loader;
    private LoginAttributeResponse loginResponse;
    private FriendListModel friendModel;
    private ActivityFriendListBinding friendListBinding;
    private RecyclerView friendList;
    private TextView title;
    private Button back;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseHelper databaseHelper;
    private FriendListAdapter adapter;
    private List<Datum> listFriend = null;

    private List<Datum> finalList = null;
    private int callingPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendListBinding = ActivityFriendListBinding.inflate(getLayoutInflater());
        setContentView(friendListBinding.getRoot());
        title = friendListBinding.incldeLayout.tvTitleText;
        back = friendListBinding.incldeLayout.btBack;
        title.setText("Friend List");
        friendList = friendListBinding.rvFriendList;
        listSearch = friendListBinding.svSearch;
        back.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        databaseHelper = new DatabaseHelper(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        friendList.setHasFixedSize(true);
        friendList.setItemViewCacheSize(20);
        friendList.setDrawingCacheEnabled(true);
        friendList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        back.setOnClickListener(this);
        listSearch.setOnQueryTextListener(this);
        loginResponse = databaseHelper.getUserDetails();
        loader.showLoading(this, "Preparing your friend list.");
        callFriendListApi(loginResponse.getId());
        friendList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (callingPage != -1) {
                        callFriendListApi(loginResponse.getId());
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

    private void callFriendListApi(String userID) {
        friendModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(FriendListModel.class);
        friendModel.initiateFriendListProcess(FriendListActivity.this, userID, callingPage)
                .observe(FriendListActivity.this, new Observer<FriendListResponse>() {
                    @Override
                    public void onChanged(FriendListResponse friendListResponse) {
                        loader.hideLoading();
                        friendModel = null;
                        if (friendListResponse != null) {
                            Data data = new Data();
                            data = friendListResponse.getData();
                            callingPage = callingPage + data.getCurrentPage();
                            listFriend = data.getData();
                            if (listFriend.size() > 0) {
                                if(finalList==null){
                                    finalList = listFriend;
                                }
                                else{
                                    finalList.addAll(listFriend);
                                }
                                adapter = new FriendListAdapter(FriendListActivity.this, finalList, FriendListActivity.this);
                                friendList.setAdapter(adapter);
                            } else {
                                callingPage = -1;
                                loader.showSnackBar(FriendListActivity.this, friendListResponse.getMessage(), true);
                                //  finish();
                            }
                        } else {
                            loader.showSnackBar(FriendListActivity.this, ErrorReponseParser.errorMsg, true);

                        }
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return false;
    }

    private void filter(String text) {
        ArrayList<Datum> filteredlist = new ArrayList<Datum>();
        for (Datum item : listFriend) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {

                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {

            adapter.filterList(filteredlist);
        }
    }

    @Override
    public void onItemClick(int position) {
        JsonObject object = new JsonObject();
        object.addProperty("status", "unfriend");
        object.addProperty("request_to", listFriend.get(position).getUserId());
        callUnfriendApi(object, position);
    }

    public void callUnfriendApi(JsonObject object, int position) {
        loader.showLoading(FriendListActivity.this, "Please wait. Processing your request");
        handleRequestModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(HandleRequestModel.class);
        handleRequestModel.initiateRequestProcess(FriendListActivity.this, object)
                .observe(FriendListActivity.this, new Observer<HandleRequestResponse>() {
                    @Override
                    public void onChanged(HandleRequestResponse unFollowResponse) {
                        loader.hideLoading();
                        if (unFollowResponse != null) {
                            adapter.refreshData(position);
                            loader.showSnackBar(FriendListActivity.this, unFollowResponse.getMessage(), false);
                            handleRequestModel = null;
                            loader.hideLoading();
                        } else {
                            loader.showSnackBar(FriendListActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }
}