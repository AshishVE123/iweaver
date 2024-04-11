package com.social.iweaver.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;



public class NetworkUtils {
    private static ConnectivityManager cm;

    public static boolean getConnectivityStatusString(Context context) {
        boolean status = false;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = true;

                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = true;
                return status;
            }

        } else {
            status = false;
            return status;
        }
        return status;
    }


}