package com.social.iweaver.apiresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SentFriendRequestResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}