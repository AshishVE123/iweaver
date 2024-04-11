package com.social.iweaver.apiresponse.profileimageupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("profileImage")
    @Expose
    private String profileImage;
    @SerializedName("tmpprofileImage")
    @Expose
    private String tmpprofileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTmpprofileImage() {
        return tmpprofileImage;
    }

    public void setTmpprofileImage(String tmpprofileImage) {
        this.tmpprofileImage = tmpprofileImage;
    }

}