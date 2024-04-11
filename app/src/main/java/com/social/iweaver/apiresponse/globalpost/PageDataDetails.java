package com.social.iweaver.apiresponse.globalpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageDataDetails {

    @SerializedName("current_page")
    @Expose
    private Integer currentPage;
    @SerializedName("last_page")
    @Expose
    private Integer lastPage;
    @SerializedName("data")
    @Expose
    private List<PostDetails> data;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public List<PostDetails> getData() {
        return data;
    }

    public void setData(List<PostDetails> data) {
        this.data = data;
    }

}