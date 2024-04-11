package com.social.iweaver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonObject;
import com.social.iweaver.R;
import com.social.iweaver.apiresponse.ConnectUsResponse;
import com.social.iweaver.databinding.ActivityConnectUsBinding;
import com.social.iweaver.utils.CustomLoader;
import com.social.iweaver.utils.ErrorReponseParser;
import com.social.iweaver.utils.InputFieldValidator;
import com.social.iweaver.viewmodel.ConnectUsModel;

public class ConnectUs extends AppCompatActivity implements View.OnClickListener {
    private ActivityConnectUsBinding cBinding;
    private Button back, submitFeedback;
    private TextView titleText;
    private EditText inputText;
    private CustomLoader loader;
    private ConnectUsModel connectModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cBinding = ActivityConnectUsBinding.inflate(getLayoutInflater());
        setContentView(cBinding.getRoot());
        titleText = cBinding.includeLayout.tvTitleText;
        titleText.setText("Connect Us");
        back = cBinding.includeLayout.btBack;
        submitFeedback = cBinding.btSubmit;
        inputText = cBinding.etFeedback;
        loader = CustomLoader.getInstance();
        submitFeedback.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back: {
                finish();
                break;
            }
            case R.id.bt_submit: {
                int errorCode = InputFieldValidator.getInstance(this).isConnectUsStringNull(inputText.getText().toString());
                if (errorCode == 1) {
                    inputText.setError("Please enter your suggestions.");
                    inputText.requestFocus();
                } else {
                    loader.showLoading(this, "Please wait. Processing your request");
                    JsonObject object = new JsonObject();
                    object.addProperty("content", inputText.getText().toString());
                    callConnectUsApi(object);
                }
                break;
            }
        }
    }

    public void callConnectUsApi(JsonObject object) {
        connectModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ConnectUsModel.class);
        connectModel.initiateConnectProcess(ConnectUs.this, object)
                .observe(ConnectUs.this, new Observer<ConnectUsResponse>() {
                    @Override
                    public void onChanged(ConnectUsResponse connectUsResponse) {
                        loader.hideLoading();
                        connectModel = null;
                        if (connectUsResponse != null) {
                            loader.showSnackBar(ConnectUs.this, connectUsResponse.getMessage(), true);
                        } else {
                            loader.showSnackBar(ConnectUs.this, ErrorReponseParser.errorMsg, true);
                        }

                    }
                });
    }
}