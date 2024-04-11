package com.social.iweaver.apiresponse.postresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("is_author_liked")
    @Expose
    private String isAuthorLiked;
    @SerializedName("post_like_count")
    @Expose
    private Integer postLikeCount;
    @SerializedName("post_comment_count")
    @Expose
    private Integer postCommentCount;
    @SerializedName("posted_on")
    @Expose
    private String postedOn;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("post_image")
    @Expose
    private List<String> postImage;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsAuthorLiked() {
        return isAuthorLiked;
    }

    public void setIsAuthorLiked(String isAuthorLiked) {
        this.isAuthorLiked = isAuthorLiked;
    }

    public Integer getPostLikeCount() {
        return postLikeCount;
    }

    public void setPostLikeCount(Integer postLikeCount) {
        this.postLikeCount = postLikeCount;
    }

    public Integer getPostCommentCount() {
        return postCommentCount;
    }

    public void setPostCommentCount(Integer postCommentCount) {
        this.postCommentCount = postCommentCount;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<String> getPostImage() {
        return postImage;
    }

    public void setPostImage(List<String> postImage) {
        this.postImage = postImage;
    }

}