package com.social.iweaver.activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;
import com.social.iweaver.BuildConfig;
import com.social.iweaver.adapter.FriendListAdapter;
import com.social.iweaver.api.EndPointInterface;
import com.social.iweaver.api.RetrofitClient;
import com.social.iweaver.apiresponse.LikePosteponse;
import com.social.iweaver.apiresponse.appversion.AppVersion;
import com.social.iweaver.apiresponse.appversion.Datum;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityMainBinding;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.JsonBodyCreator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.AppVersionModel;
import com.social.iweaver.viewmodel.LogoutViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Splash Activity
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ActivityMainBinding mainBinding;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView appVersion;
    private UserPreference userPreference;
    private DatabaseHelper databaseHelper;
    private AppVersionModel appVersionViewModel;
    private List<Datum> versionUpdate = null;
    private CustomLoader loader;
    private CustomAlertDialog alertDialog;
    private LogoutViewModel logoutViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        appVersion = mainBinding.tvAppVersion;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        userPreference = UserPreference.getInstance(this);
        databaseHelper = new DatabaseHelper(MainActivity.this);
        loader = CustomLoader.getInstance();
        alertDialog = CustomAlertDialog.getInstance(this);
        logoutViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LogoutViewModel.class);
        appVersion.setText("App Version :" + " " + BuildConfig.VERSION_NAME);
        if (!checkPermission()) {
            requestPermission();
        } else {
            initApplication();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean notificationAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted && storageAccepted && notificationAccepted) {
                        initApplication();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void initApplication() {
        if (NetworkUtils.getConnectivityStatusString(MainActivity.this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(userPreference.getAuthToken())) {
                        callAppVersionRequest(JsonBodyCreator.getAppVersionBody(BuildConfig.VERSION_NAME));
                    } else {
                        if (!TextUtils.isEmpty(userPreference.getAuthToken())) {
                            if (!userPreference.getPolicyStatus()) {
                                startActivity(new Intent(MainActivity.this, PolicyActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        } else {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }

                    }
                }

            }, 5 * 1000);
        } else {
            startActivity(new Intent(MainActivity.this, NetworkIssueActivity.class));
//            finish();
        }
    }

    public void callAppVersionRequest(JsonObject object) {
        appVersionViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(AppVersionModel.class);
        appVersionViewModel.initiateAppVersionProcess(MainActivity.this, object)
                .observe(MainActivity.this, new Observer<AppVersion>() {
                    @Override
                    public void onChanged(AppVersion response) {
                        loader.hideLoading();
                        appVersionViewModel = null;
                        if (response != null) {
                            String data = String.valueOf(response.getData().get(0).isUpdateRequired);
                            String dataT = String.valueOf(response.getData().get(0).isForceUpdate);
                            if (data.equals(true) || dataT.equals(true)) {
                                alertDialog.showVersionUpdateAlertDialog(MainActivity.this, "New version of iWeaver on Play Store please update!!");
                            } else {
                                if (!TextUtils.isEmpty(userPreference.getAuthToken())) {
                                    if (!userPreference.getPolicyStatus()) {
                                        startActivity(new Intent(MainActivity.this, PolicyActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                } else {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }

                            }

                        } else {
                            loader.showSnackBar(MainActivity.this, ErrorReponseParser.errorMsg, true);
                        }
                    }
                });
    }
}