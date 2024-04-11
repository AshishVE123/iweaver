package com.social.iweaver.apiresponse.imagepost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImagePostResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("postId")
    @Expose
    private String postId;
    @SerializedName("imageDetails")
    @Expose
    private List<ImageDetail> imageDetails;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<ImageDetail> getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(List<ImageDetail> imageDetails) {
        this.imageDetails = imageDetails;
    }

}