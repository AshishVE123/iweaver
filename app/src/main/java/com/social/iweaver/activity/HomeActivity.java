package com.social.iweaver.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.adapter.FriendListAdapter;
import com.social.iweaver.adapter.HomeFeedAdapter;
import com.social.iweaver.apiresponse.ForgotPasswordResponse;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.LogoutResponse;
import com.social.iweaver.apiresponse.ReportPostApiResponse;
import com.social.iweaver.apiresponse.appversion.AppVersion;
import com.social.iweaver.apiresponse.friendlist.Data;
import com.social.iweaver.apiresponse.friendlist.Datum;
import com.social.iweaver.apiresponse.globalpost.GlobalPostResponse;
import com.social.iweaver.apiresponse.globalpost.PageDataDetails;
import com.social.iweaver.apiresponse.globalpost.PostDetails;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityHomeBinding;
import com.social.iweaver.databinding.NavHeaderDashboardBinding;
import com.social.iweaver.listener.RecyclerViewClickListener;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.JsonBodyCreator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.AppVersionModel;
import com.social.iweaver.viewmodel.ErrorPostViewModel;
import com.social.iweaver.viewmodel.ForgotPasswordViewModel;
import com.social.iweaver.viewmodel.GlobalPostViewModel;
import com.social.iweaver.viewmodel.LogoutViewModel;


import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, RecyclerViewClickListener {
    private DatabaseHelper databaseHelper;
    private CustomAlertDialog alertDialog;
    private CustomLoader loader;
    private CustomAlertDialog dialog;
    private LogoutViewModel logoutViewModel;
    private ErrorPostViewModel errorPostModel;
    private DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView userText;
    private ImageView searchView, userImage, userProfileImage;
    private UserPreference userPreference;
    private ActivityHomeBinding binding;
    //private SwipeRefreshLayout refreshView;
    private Toolbar toolbar;
    private EditText editPost;
    private NavHeaderDashboardBinding headerBinding;
    private GlobalPostViewModel homeModel;
    private FloatingActionButton traslateButton;
    private LoginAttributeResponse loginDetails;
    private HomeFeedAdapter globalFeedAdapter;
    private RecyclerView feedRecyclerView;
    private GlobalPostResponse globalPostResponse;
    private List<PostDetails> finalPostList;
    private int callingPage = 1;
    private List<PostDetails> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View headerView = binding.navView.getHeaderView(0);
        headerBinding = NavHeaderDashboardBinding.bind(headerView);
        feedRecyclerView = binding.appBarParam.contentInclude.rvFeed;
        userText = headerBinding.tvUserName;
        userImage = headerBinding.ivProfile;
        searchView = binding.appBarParam.ivSearchView;
        toolbar = binding.appBarParam.toolbar;
        drawer = binding.drawerLayout;
        // refreshView = binding.appBarParam.contentInclude.swipeRefreshLayout;
        userProfileImage = binding.appBarParam.contentInclude.ivLoginUserImage;
        editPost = binding.appBarParam.contentInclude.etPost;
        alertDialog = CustomAlertDialog.getInstance(this);
        loader = CustomLoader.getInstance();
        databaseHelper = new DatabaseHelper(this);
        userPreference = UserPreference.getInstance(this);
        editPost.setOnClickListener(this);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        traslateButton = binding.appBarParam.fab;
        traslateButton.setOnClickListener(this);
        searchView.setOnClickListener(this);
        dialog = CustomAlertDialog.getInstance(this);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        logoutViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LogoutViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        feedRecyclerView.setLayoutManager(linearLayoutManager);
        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setItemViewCacheSize(20);
        feedRecyclerView.setDrawingCacheEnabled(true);
        feedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        finalPostList = null;
        postList = null;
        callingPage = 1;

       /* callHomeFeedRequest(callingPage);
        loader.showLoading(HomeActivity.this, "Preparing Dashboard. Please wait");
       refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  refreshView.setRefreshing(false);

            }
        });*/

        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (callingPage != -1) {
                        // loader.showLoading(HomeActivity.this, "Please wait. Retrieving more posts.");
                        //  Toast.makeText(HomeActivity.this, page+1+""+"RecyclerView Page test", Toast.LENGTH_SHORT).show();
                        callHomeFeedRequest(callingPage);
                    } else {
                        //
                    }

                }
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.getConnectivityStatusString(HomeActivity.this)) {
            setUpNavHeader();
            loader.showLoading(HomeActivity.this, "Preparing Dashboard. Please wait");
            finalPostList = null;
            postList = null;
            callingPage = 1;
            callHomeFeedRequest(callingPage);
        } else {
            startActivity(new Intent(HomeActivity.this, NetworkIssueActivity.class));
            finish();
        }
    }

    /**
     * @param item The selected item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            alertDialog.showLogOutAlertDialog(HomeActivity.this, "Are you sure to exit the iWeaver", logoutViewModel);
        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(HomeActivity.this, FriendListActivity.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(HomeActivity.this, RequestActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Get Install I-Weaver to connect people";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Join I-Weaver Network");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(HomeActivity.this, ConnectUs.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(HomeActivity.this, HelpActivity.class));
        } else if (id == R.id.nav_rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_view: {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                break;
            }
            case R.id.et_post: {
                startActivity(new Intent(this, CreatePostActivity.class));
                break;
            }
            case R.id.fab: {
                Toast.makeText(this, "Translator API under development", Toast.LENGTH_SHORT).show();
                break;
            }

        }
    }

    /**
     *
     */
    public void setUpNavHeader() {
        loginDetails = databaseHelper.getUserDetails();
        if (loginDetails != null) {
            userText.setText(loginDetails.getName());
            Glide.with(this).
                    load(loginDetails.getUserImage()).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    skipMemoryCache(true).
                    placeholder(R.drawable.image_progress_animation).
                    into(userImage);

            Glide.with(this).
                    load(loginDetails.getUserImage()).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    skipMemoryCache(true).
                    placeholder(R.drawable.image_progress_animation).
                    into(userProfileImage);
        }
    }

    /**
     * @param activity
     * @param model
     */
    public void initiateLogoutProcess(Activity activity, LogoutViewModel model) {
        model.initiateLogoutProcess(this)
                .observe((LifecycleOwner) this, new Observer<LogoutResponse>() {
                    @Override
                    public void onChanged(LogoutResponse logOutResponse) {
                        //loader.hideLoading();
                        if (logOutResponse != null) {
                            System.out.println("Login Response from login Activity::" + logOutResponse.getMessage());
                            Toast.makeText(HomeActivity.this, logOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            userPreference.deleteAuthToken();
                            userPreference.deletePolicyStatus();
                            startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();

                        } else {
                            Toast.makeText(HomeActivity.this, "We are experiencing some issue. Please try after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertDialog.showExitAlertDialog(HomeActivity.this, "Are you sure to exit I-Weaver");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void callHomeFeedRequest(int page) {
        homeModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(GlobalPostViewModel.class);
        homeModel.getGlobalPostDetails(HomeActivity.this, page)
                .observe(HomeActivity.this, new Observer<GlobalPostResponse> () {
                    @Override
                    public void onChanged(GlobalPostResponse homeFeedResponse) {
                        loader.hideLoading();
                        homeModel = null;
                        if (homeFeedResponse != null) {
                            globalPostResponse = homeFeedResponse;
                            PageDataDetails data = new PageDataDetails();
                            data = globalPostResponse.getData();
                            callingPage = data.getCurrentPage() + 1;
                            postList = data.getData();
                            if (postList.size() > 0) {
                                if (finalPostList == null) {
                                    //    Toast.makeText(HomeActivity.this, "Ohh this is the first set of data", Toast.LENGTH_SHORT).show();
                                    finalPostList = postList;
                                } else {
                                    //    Toast.makeText(HomeActivity.this, "now adding the data", Toast.LENGTH_SHORT).show();
                                    finalPostList.addAll(postList);
                                }
                                System.out.println(postList.size() + "" + "getting size of data" + finalPostList.size());
                                globalFeedAdapter = new HomeFeedAdapter(HomeActivity.this, finalPostList, loginDetails, HomeActivity.this);
                                feedRecyclerView.setAdapter(globalFeedAdapter);
                            } else {
                                callingPage = -1;
                                loader.hideLoading();
                                loader.showSnackBar(HomeActivity.this, globalPostResponse.getMessage(), false);
                            }

                        } else {
                            loader.showSnackBar(HomeActivity.this, ErrorReponseParser.errorMsg, false);
                        }

                    }

                });
    }


    @Override
    public void onItemClick(int position) {
        String postID = String.valueOf(finalPostList.get(position).getPostId());
        Intent intent = new Intent(HomeActivity.this, CommentActivity.class);
        intent.putExtra("postId", postID);
        startActivity(intent);
    }

    public void refreshAdapter(int position) {
        finalPostList.remove(position);
        // globalFeedAdapter.notifyItemRemoved(position);
        globalFeedAdapter.notifyItemRemoved(position);


    }

    public void callBlockPostApi(int postId, int position) {
        loader.showLoading(HomeActivity.this, "Please wait. Processing your request..");
        JsonObject object = new JsonObject();
        object.addProperty("post_id", postId);
        errorPostModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ErrorPostViewModel.class);
        errorPostModel.initiateReportPostProcess(HomeActivity.this, object)
                .observe((LifecycleOwner) this, new Observer<ReportPostApiResponse>() {
                    @Override
                    public void onChanged(ReportPostApiResponse blockPostResponse) {
                        loader.hideLoading();
                        errorPostModel = null;
                        if (blockPostResponse != null) {
                            loader.showSnackBar(HomeActivity.this, blockPostResponse.getMessage(), false);
                            refreshAdapter(position);

                        } else {
                            loader.showSnackBar(HomeActivity.this, ErrorReponseParser.errorMsg, false);
                        }
                    }
                });
    }


}