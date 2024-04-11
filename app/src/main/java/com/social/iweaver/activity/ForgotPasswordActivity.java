package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.ForgotPasswordResponse;
import com.social.iweaver.databinding.ActivityForgotPasswordBinding;
import com.social.iweaver.utils.CustomAlertDialog;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.utils.JsonBodyCreator;
import com.social.iweaver.utils.NetworkUtils;
import com.social.iweaver.viewmodel.ForgotPasswordViewModel;


public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private CustomAlertDialog dialog;
    private InputFieldValidator validator;
    private CustomLoader loader;
    private EditText email;
    private Button resetButton, back;
    private ActivityForgotPasswordBinding resetPswbinding;
    private ForgotPasswordViewModel forgotPasswordViewModel;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetPswbinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(resetPswbinding.getRoot());
        title = resetPswbinding.includeLayout.tvTitleText;
        title.setText(getResources().getString(R.string.forgotpasswordTitle));
        back = resetPswbinding.includeLayout.btBack;
        validator = InputFieldValidator.getInstance(this);
        email = resetPswbinding.etEmail;
        resetButton = resetPswbinding.btReset;
        loader = CustomLoader.getInstance();
        back.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        dialog = CustomAlertDialog.getInstance(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_reset: {
                int errorType = validator.isInputstringNull(email.getText().toString());
                if (errorType == 1) {
                    email.setError("Please enter email");
                    email.requestFocus();
                } else if (errorType == 5) {
                    email.setError("Please enter valid email");
                    email.requestFocus();
                } else {
                    if (NetworkUtils.getConnectivityStatusString(ForgotPasswordActivity.this)) {
                        loader.showLoading(this, "Please wait. Processing your request");
                        callForgotPasswordRequest(JsonBodyCreator.getForgotPasswordBody(email.getText().toString()));
                    } else {
                        startActivity(new Intent(ForgotPasswordActivity.this, NetworkIssueActivity.class));
                        finish();
                    }
                }
                break;
            }
            case R.id.bt_back: {
                finish();
                break;
            }
        }
    }

    public void callForgotPasswordRequest(JsonObject object) {
        forgotPasswordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ForgotPasswordViewModel.class);
        forgotPasswordViewModel.initiateForgotPasswordProcess(ForgotPasswordActivity.this, object)
                .observe(ForgotPasswordActivity.this, new Observer<ForgotPasswordResponse>() {
                    @Override
                    public void onChanged(ForgotPasswordResponse response) {
                        loader.hideLoading();
                        forgotPasswordViewModel = null;
                        if (response != null) {
                            dialog.showInfoAlertDialog(ForgotPasswordActivity.this, response.getMessage());

                        } else {
                            loader.showSnackBar(ForgotPasswordActivity.this,ErrorReponseParser.errorMsg,true);
                        }
                    }
                });
    }


}