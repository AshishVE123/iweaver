package com.social.iweaver.database;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreference {
    public static UserPreference mInstance;
    private SharedPreferences preferences;
    /**
     * @param mContext
     */
    public UserPreference(Context mContext) {
        if (mContext != null) {
            preferences = mContext.getSharedPreferences(PrecerenceConstant.PREFERENCE_KEY, Context.MODE_PRIVATE);
        }
    }
    /**
     * @param context
     * @return
     */
    public static UserPreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserPreference(context);
        }
        return mInstance;
    }

    /**
     * @return authtoken
     */
    public String getAuthToken() {
        return preferences.getString(PrecerenceConstant.PREFERENCE_TOKEN_KEY, null);
    }
    /**
     * @param token
     */
    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrecerenceConstant.PREFERENCE_TOKEN_KEY, token);
        editor.apply();
    }

    public void setFCMToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrecerenceConstant.PREFERENCE_FCM_KEY, token);
        editor.apply();
    }
    public String getFCMToken() {
        return preferences.getString(PrecerenceConstant.PREFERENCE_FCM_KEY, null);
    }
    public void deleteAuthToken() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PrecerenceConstant.PREFERENCE_TOKEN_KEY);
        editor.apply();
    }

    public void deletePolicyStatus() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PrecerenceConstant.PREFERENCE_POLICY_KEY);
        editor.apply();
    }

    public boolean getPolicyStatus() {
        return preferences.getBoolean(PrecerenceConstant.PREFERENCE_POLICY_KEY, false);
    }

    public void setPolicyStatus(boolean isAccepted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PrecerenceConstant.PREFERENCE_POLICY_KEY, isAccepted);
        editor.apply();
    }


}
