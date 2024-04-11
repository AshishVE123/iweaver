package com.social.iweaver.utils;

import org.json.JSONObject;

import retrofit2.Response;

public class ErrorReponseParser {
    public static String errorMsg = null;
    public static ErrorReponseParser instance;

    public static ErrorReponseParser getInstance() {
        if (instance == null) {
            instance = new ErrorReponseParser();
        }
        return instance;
    }

    public String getErrorResponseMessage(Response<?> response) {
        try {
            JSONObject jObjError = new JSONObject(response.errorBody().string());
            errorMsg = jObjError.getString("message");
            System.out.println("Checking the error response::" + errorMsg);
            return errorMsg;
        } catch (Exception e) {
        }
        return errorMsg;

    }

}
