package com.social.iweaver.apiresponse.addcomment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("comment")
    @Expose
    private Comment comment;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}