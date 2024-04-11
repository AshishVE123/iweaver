package com.social.iweaver.apiresponse.userpost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("dob")
    @Expose
    private Object dob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("about_us")
    @Expose
    private Object aboutUs;
    @SerializedName("languages")
    @Expose
    private String languages;
    @SerializedName("postCount")
    @Expose
    private Integer postCount;
    @SerializedName("friendCount")
    @Expose
    private Integer friendCount;
    @SerializedName("is_user_friend")
    @Expose
    private String isUserFriend;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("post")
    @Expose
    private List<Post> post;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getDob() {
        return dob;
    }

    public void setDob(Object dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Object getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(Object aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(Integer friendCount) {
        this.friendCount = friendCount;
    }

    public String getIsUserFriend() {
        return isUserFriend;
    }

    public void setIsUserFriend(String isUserFriend) {
        this.isUserFriend = isUserFriend;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<Post> getPost() {
        return post;
    }

    public void setPost(List<Post> post) {
        this.post = post;
    }

}