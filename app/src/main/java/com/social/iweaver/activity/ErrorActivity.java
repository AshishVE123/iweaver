package com.social.iweaver.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class ErrorActivity extends Application {
    //CustomAlert alert;
    private static Context context;

    public static Context getAppContext() {
        return ErrorActivity.context;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
    }

    // here you can handle all unexpected crashes
    public void handleUncaughtException(Thread thread, Throwable e) {
        System.out.println("Checkes the issue");
        e.printStackTrace();
        Intent intent = new Intent(getApplicationContext(), CrashActivity.class);
        intent.setAction("com.mydomain.SEND_LOG");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }
}
