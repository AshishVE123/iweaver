package com.social.iweaver.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.social.iweaver.databinding.ActivityCrashBinding;

public class CrashActivity extends AppCompatActivity {
    private TextView tvRelaunch;
    private ActivityCrashBinding crashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crashBinding = ActivityCrashBinding.inflate(getLayoutInflater());
        setContentView(crashBinding.getRoot());
        tvRelaunch = crashBinding.tvRelaunchBtn;
        tvRelaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}