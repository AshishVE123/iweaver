package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.adapter.SearchAdapter;
import com.social.iweaver.apiresponse.HandleRequestResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.searchresponse.Data;
import com.social.iweaver.apiresponse.searchresponse.Datum;
import com.social.iweaver.apiresponse.searchresponse.SearchResponse;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.databinding.ActivitySearchBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.HandleRequestModel;
import com.social.iweaver.viewmodel.SearchViewModel;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewClickListener {
    private ActivitySearchBinding sBinding;
    private LoginAttributeResponse loginResponse;
    private DatabaseHelper helper;
    private RecyclerView searchList;
    private SearchViewModel searchModel;
    private List<Datum> searchListResponse;

    private CustomLoader loader;
    private SearchView searchView;
    private TextView title;
    private Button back;
    private LinearLayoutManager linearLayoutManager;
    private SearchResponse searchResponse;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(sBinding.getRoot());
        title = sBinding.llToolbareParams.tvTitleText;
        back = sBinding.llToolbareParams.btBack;
        searchView = sBinding.svSearchView;
        title.setText("Search");
        back = sBinding.llToolbareParams.btBack;
        helper = new DatabaseHelper(this);
        loginResponse = helper.getUserDetails();
        searchList = sBinding.rvSearchList;
        loader = CustomLoader.getInstance();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchList.setLayoutManager(linearLayoutManager);
        searchList.setHasFixedSize(true);
        searchList.setItemViewCacheSize(20);
        searchList.setDrawingCacheEnabled(true);
        searchList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchList.setLayoutManager(linearLayoutManager);
        searchList.setHasFixedSize(true);
        searchList.setItemViewCacheSize(20);
        searchList.setDrawingCacheEnabled(true);
        searchList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        back.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loader.showLoading(SearchActivity.this, "Searching your network");
                searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                      searchQuery(newText);
                }
                return false;
            }
        });



    }

    public void searchQuery(String searchQuery) {
        if (NetworkUtils.getConnectivityStatusString(SearchActivity.this)) {
            callSearchRequest(searchQuery);
        } else {
            startActivity(new Intent(SearchActivity.this, NetworkIssueActivity.class));
            finish();
        }
    }

    public void callSearchRequest(String keyword) {
        searchModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(SearchViewModel.class);
        searchModel.initiateSearchProcess(SearchActivity.this, keyword)
                .observe(SearchActivity.this, new Observer<SearchResponse>() {
                    @Override
                    public void onChanged(SearchResponse apiSearchResponse) {
                        loader.hideLoading();
                        searchModel = null;
                        if (apiSearchResponse != null) {
                            searchResponse = apiSearchResponse;
                            Data data = new Data();
                            data = apiSearchResponse.getData();
                            searchListResponse = data.getData();
                            if (searchListResponse.size() > 0) {
                                adapter = new SearchAdapter(SearchActivity.this, searchListResponse, SearchActivity.this);
                                SearchActivity.this.searchList.setAdapter(adapter);
                            } else {
                                loader.showSnackBar(SearchActivity.this,"No Match Found.",false);
                            }
                        } else {
                            loader.showSnackBar(SearchActivity.this, ErrorReponseParser.errorMsg,false);
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
        Intent intent = new Intent(SearchActivity.this,MyProfileActivity.class);
        intent.putExtra("userId",searchListResponse.get(position).getId()+"");
        startActivity(intent);


    }


}