package com.social.iweaver.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.social.iweaver.R;
import com.social.iweaver.databinding.ActivityHelpBinding;
import com.social.iweaver.utils.CustomLoader;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityHelpBinding helpBinding;
    private WebView webView;
    private TextView titleText;
    private Button closeButton, back;
    private CustomLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        helpBinding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(helpBinding.getRoot());
        webView = helpBinding.webView;
        titleText = helpBinding.toolbarLayout.tvTitleText;
        titleText.setText("Help");
        back = helpBinding.toolbarLayout.btBack;
        closeButton = helpBinding.btClose;
        closeButton.setOnClickListener(this);
        loader = CustomLoader.getInstance();
        back.setOnClickListener(this);
        webView.getSettings().setJavaScriptEnabled(true);
        loader.showLoading(this, "Processing your request. Please wait.");
        webView.loadUrl("https://iweaver.net/faq");
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
            case R.id.bt_close:{
                finish();
                break;
            }
            case R.id.bt_back: {
                finish();
            }
        }
    }
}