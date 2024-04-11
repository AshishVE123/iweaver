package com.social.iweaver.apiresponse.usercomment;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Datum {

    @SerializedName("comment_id")
    @Expose
    private Integer commentId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("comment_user")
    @Expose
    private String commentUser;
    @SerializedName("comment_user_image")
    @Expose
    private String commentUserImage;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentUserImage() {
        return commentUserImage;
    }

    public void setCommentUserImage(String commentUserImage) {
        this.commentUserImage = commentUserImage;
    }

}