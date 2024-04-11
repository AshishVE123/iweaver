package com.social.iweaver.utils;

import android.content.Context;
import android.text.TextUtils;

public class InputFieldValidator {
    private static InputFieldValidator mInstance;

    /**
     * @param context
     * @return
     */
    public static InputFieldValidator getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new InputFieldValidator();
        }
        return mInstance;
    }

    /**
     * checking login input field are not null
     *
     * @param inputEmail
     * @param inputPassword
     * @return
     */
    public int isInputstringNull(String inputEmail, String inputPassword) {
        int errorCode = 0;
        if (TextUtils.isEmpty(inputEmail)) {
            errorCode = 1;
        } else if (!isValidEmail(inputEmail)) {
            errorCode = 3;
        } else if (TextUtils.isEmpty(inputPassword)) {
            errorCode = 2;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }

    /**
     * checking Email pattern
     *
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * checking input field are not null
     *
     * @param inputName
     * @param inputEmail
     * @param phoneNumber
     * @param password
     * @param confirmPassword
     * @return
     */
    public int isInputstringNull(String inputName, String inputEmail, String phoneNumber, String password, String confirmPassword, String gender, String birthDate) {
        int errorCode = 0;
        if (TextUtils.isEmpty(inputName)) {
            errorCode = 1;
        } else if (!isValidEmail(inputEmail)) {
            errorCode = 6;
        } else if (TextUtils.isEmpty(inputEmail)) {
            errorCode = 2;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            errorCode = 3;
        } else if (TextUtils.isEmpty(password)) {
            errorCode = 4;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            errorCode = 5;
        } else if (!password.equals(confirmPassword)) {
            errorCode = 7;
        } else if (TextUtils.isEmpty(birthDate)) {
            errorCode = 8;
        } else if (TextUtils.isEmpty(gender)) {
            errorCode = 9;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }

    /**
     * @param inputEmail
     * @return
     */
    public int isInputstringNull(String inputEmail) {
        int errorCode = 0;
        if (TextUtils.isEmpty(inputEmail)) {
            errorCode = 1;
        } else if (!isValidEmail(inputEmail)) {
            errorCode = 5;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }

    public int isInputPostStringNull(String post) {
        int errorCode = 0;
        if (TextUtils.isEmpty(post)) {
            errorCode = 1;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }
    public int isConnectUsStringNull(String post) {
        int errorCode = 0;
        if (TextUtils.isEmpty(post)) {
            errorCode = 1;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }
    public int isInputsProfileStringNull(String name, String aboutUs, String language, String male, String dob) {
        int errorCode = 0;
        if (TextUtils.isEmpty(name)) {
            errorCode = 1;
        } else if (TextUtils.isEmpty(aboutUs)) {
            errorCode = 2;
        } else if (TextUtils.isEmpty(language)) {
            errorCode = 3;
        } else if (TextUtils.isEmpty(male)) {
            errorCode = 4;
        } else if (TextUtils.isEmpty(dob)) {
            errorCode = 5;
        } else {
            errorCode = -1;
        }
        return errorCode;
    }

}
