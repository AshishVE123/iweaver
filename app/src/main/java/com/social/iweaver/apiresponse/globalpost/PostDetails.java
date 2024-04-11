package com.social.iweaver.apiresponse.globalpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class PostDetails {

    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
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
    @SerializedName("is_post_report")
    @Expose
    private String isPostReport;
    @SerializedName("post_image")
    @Expose
    private List<String> postImage;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getIsPostReport() {
        return isPostReport;
    }

    public void setIsPostReport(String isPostReport) {
        this.isPostReport = isPostReport;
    }

    public List<String> getPostImage() {
        return postImage;
    }

    public void setPostImage(List<String> postImage) {
        this.postImage = postImage;
    }

}