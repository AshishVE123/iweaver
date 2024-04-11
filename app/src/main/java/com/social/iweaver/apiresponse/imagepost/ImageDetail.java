package com.social.iweaver.apiresponse.imagepost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageDetail {

    @SerializedName("postId")
    @Expose
    private String postId;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}