package com.social.iweaver.apiresponse.globalpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalPostResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private PageDataDetails data;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PageDataDetails getData() {
        return data;
    }

    public void setData(PageDataDetails data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}