package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.social.iweaver.R;
import com.social.iweaver.database.UserPreference;
import com.social.iweaver.databinding.ActivityPolicyBinding;
import com.social.iweaver.utils.CustomLoader;

public class PolicyActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPolicyBinding policyBinding;
    private WebView webView;
    private UserPreference userPreference;
    private TextView titleText;
    private Button bt_accept, bt_decline, back;
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyBinding = ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(policyBinding.getRoot());
        bt_decline = policyBinding.btDecline;
        bt_accept = policyBinding.btAccept;
        webView = policyBinding.webView;
        back = policyBinding.toolbarLayout.btBack;
        titleText = policyBinding.toolbarLayout.tvTitleText;
        userPreference = UserPreference.getInstance(this);
        titleText.setText("Terms & Policy");
        bt_accept.setOnClickListener(this);
        bt_decline.setOnClickListener(this);
        userPreference.setPolicyStatus(false);
        loader = CustomLoader.getInstance();
        loader.showLoading(this, "Please wait. Preparing Terms&Conditions Page ..");
        back.setOnClickListener(this);
        webView.loadUrl("https://iweaver.net/terms-and-condition");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loader.hideLoading();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_accept: {
                userPreference.setPolicyStatus(true);
                finish();
                startActivity(new Intent(PolicyActivity.this, HomeActivity.class));

                break;
            }
            case R.id.bt_decline: {
                userPreference.setPolicyStatus(false);
                finish();
                finishAffinity();
                break;
            }
            case R.id.bt_back: {
                finish();
                break;
            }
        }

    }
}