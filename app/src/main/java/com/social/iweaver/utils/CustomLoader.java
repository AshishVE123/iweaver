package com.social.iweaver.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.social.iweaver.R;

import java.util.List;

public class CustomLoader {
    private static CustomLoader mInstance;
    private Dialog dialog;

    public static synchronized CustomLoader getInstance() {
        if (mInstance == null) {
            mInstance = new CustomLoader();
        }
        return mInstance;
    }

    public void showLoading(Context mContext, String msg) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_common_loader, null);
        TextView message = view.findViewById(R.id.message);
        message.setText(msg);
        dialog.setContentView(view);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 60, 0, 60, 0);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(inset);
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    public void showSnackBar(Activity activity, String message, boolean isFinishActivity) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
        View customSnackView = activity.getLayoutInflater().inflate(R.layout.custom_snackbar_view, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        TextView messageText = customSnackView.findViewById(R.id.tv_message_txt);
        messageText.setText(message);
        Button bGotoWebsite = customSnackView.findViewById(R.id.gotoWebsiteButton);
        bGotoWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbarLayout.addView(customSnackView, 0);
        snackbar.show();
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if(isFinishActivity) {
                     activity.finish();
                }
                else{
                    // nothing to do
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {

            }
        });
    }
    public static boolean isAppForground(Context context) {

        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : l) {
            if (info.uid == context.getApplicationInfo().uid && info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}