package com.social.iweaver.utils;

import com.google.gson.JsonObject;

public class JsonBodyCreator {

    public static JsonBodyCreator instance;

    /**
     * @return
     */
    public static JsonBodyCreator getInstance() {
        if (instance == null) {
            instance = new JsonBodyCreator();
        }
        return instance;
    }

    /**
     * @param email
     * @param password
     * @return
     */
    public static JsonObject getLoginbody(String email, String password, String fcmToken) {
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        object.addProperty("password", password);
        object.addProperty("fcm_token", fcmToken);
        return object;
    }

    /**
     * @param name
     * @param email
     * @param contact
     * @param password
     * @param confirmPassword
     * @param birthDate
     * @param gender
     * @return
     */
    public static JsonObject getSignupBody(String name, String email, String contact, String password, String confirmPassword, String birthDate, String gender) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("email", email);
        object.addProperty("contact", contact);
        object.addProperty("gender", gender);
        object.addProperty("dob", birthDate);
        object.addProperty("password", password);
        object.addProperty("confirmPassword", confirmPassword);
        return object;
    }

    public static JsonObject getForgotPasswordBody(String email) {
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        return object;
    }

    public static JsonObject getAppVersionBody(String version) {
        JsonObject object = new JsonObject();
        object.addProperty("version", version);
        return object;
    }
}
