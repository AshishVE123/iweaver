package com.social.iweaver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.loginresponse.Data;
import com.social.iweaver.apiresponse.loginresponse.LoginResponse;
import com.social.iweaver.apiresponse.loginresponse.User;
import com.social.iweaver.database.DatabaseHelper;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityLoginBinding;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.utils.JsonBodyCreator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelper databaseHelper;
    private UserPreference userPreference;
    private JsonBodyCreator jsonCreator;
    private CustomLoader loader;
    private LoginViewModel loginViewmodel;
    private ActivityLoginBinding loginBinding;
    private TextView signUp, forgotPassword;
    private Button signIn, back;
    private TextView title;
    private CheckBox rememberMe;
    private InputFieldValidator validator;
    private int isChecked = -1;
    private EditText inputEmail, inputPassword;
    private LinearLayout ll_child_params;
    private Data tokenData;
    private User userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        validator = InputFieldValidator.getInstance(this);
        title = loginBinding.layoutInclude.tvTitleText;
        back = loginBinding.layoutInclude.btBack;
        back.setVisibility(View.GONE);
        forgotPassword = loginBinding.tvForgotPsw;
        signUp = loginBinding.tvSignUp;
        inputEmail = loginBinding.etEmail;
        inputPassword = loginBinding.etPassword;
        signIn = loginBinding.btSignIn;
        rememberMe = loginBinding.cbRememberme;
        ll_child_params = loginBinding.llChildParams;
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);
        databaseHelper = new DatabaseHelper(this);
        loader = CustomLoader.getInstance();
        jsonCreator = JsonBodyCreator.getInstance();
        userPreference = UserPreference.getInstance(this);
        checkRememberMe();
    }
    /**
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_signIn:  {
                int errorType = validator.isInputstringNull(inputEmail.getText().toString(), inputPassword.getText().toString());
                if (errorType == 1) {
                    inputEmail.setError("Please enter email");
                    inputEmail.requestFocus();
                } else if (errorType == 2) {
                    inputPassword.setError("Please enter password");
                    inputPassword.requestFocus();
                } else if (errorType == 3) {
                    inputEmail.setError("Please enter valid email address");
                    inputEmail.requestFocus();
                } else if (errorType == -1) {
                    if (rememberMe.isChecked()) {
                        isChecked = 1;

                    } else {
                        isChecked = 0;
                    }
                    if (NetworkUtils.getConnectivityStatusString(LoginActivity.this)) {
                        databaseHelper.deleteTableData();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ll_child_params.getWindowToken(), 0);
                        System.out.println("Checking the FCM token::"+userPreference.getFCMToken()+"     "+userPreference.getPolicyStatus()+"    "+userPreference.getAuthToken());
                        loader.showLoading(LoginActivity.this, "Please wait. We are verifying your details");
                        callLoginRequest(JsonBodyCreator.getLoginbody(inputEmail.getText().toString(), inputPassword.getText().toString(), userPreference.getFCMToken()));
                    } else {
                        startActivity(new Intent(LoginActivity.this, NetworkIssueActivity.class));
                        finish();
                    }
                }
            }
            break;

            case R.id.tv_forgot_psw: {
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            }
            case R.id.tv_signUp: {
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            }
        }
    }

    public void callLoginRequest(JsonObject object) {
        loginViewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginViewModel.class);
        loginViewmodel.initiateLoginProcess(LoginActivity.this, object)
                .observe(LoginActivity.this, new Observer<LoginResponse>() {
                    @Override
                    public void onChanged(LoginResponse loginResponse) {
                        loader.hideLoading();
                        loginViewmodel = null;
                        if (loginResponse != null) {
                            tokenData = new Data();
                            tokenData = loginResponse.getData();
                            userPreference.setAuthToken("Bearer" + " " + tokenData.getToken());
                            userInfo = new User();
                            userInfo = loginResponse.getData().getUser();
                            databaseHelper.storeLoginDetail(userInfo, isChecked, inputPassword.getText().toString());
                            Intent intent = new Intent(LoginActivity.this, PolicyActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            loader.hideLoading();
                            finish();
                        } else {
                            loader.hideLoading();
                            loader.showSnackBar(LoginActivity.this,ErrorReponseParser.errorMsg,false);
                        }

                    }
                });
    }
    /**
     * Checking if remember me is checked
     */
    private void checkRememberMe() {
        DatabaseHelper helper = new DatabaseHelper(this);
        LoginAttributeResponse response = helper.getUserDetails();
        if (response != null) {
            if (response.getRememberMe() == 1) {
                if (response != null) {
                    inputEmail.setText(response.getEmail());
                    inputPassword.setText(response.getPassword());
                }
            } else {
                inputEmail.setText("");
                inputPassword.setText("");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}


