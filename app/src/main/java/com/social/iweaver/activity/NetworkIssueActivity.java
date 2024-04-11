package com.social.iweaver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.social.iweaver.databinding.ActivityNetworkIssueBinding;

public class NetworkIssueActivity extends AppCompatActivity {
    ActivityNetworkIssueBinding nBinding;
    private TextView titleText;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nBinding = ActivityNetworkIssueBinding.inflate(getLayoutInflater());
        setContentView(nBinding.getRoot());
        titleText = nBinding.rlToolbarParam.tvTitleText;
        retryButton = nBinding.btRetry;
        titleText.setText("Network Issue");
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                startActivity(new Intent(NetworkIssueActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}