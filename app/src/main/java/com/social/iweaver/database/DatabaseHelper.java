package com.social.iweaver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.JsonObject;
import com.social.iweaver.apiresponse.LoginAttributeResponse;
import com.social.iweaver.apiresponse.loginresponse.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + DatabaseConstants.TABLE_LOGIN + "("
                + DatabaseConstants.KEY_ID + " TEXT," + DatabaseConstants.KEY_NAME + " TEXT," + DatabaseConstants.KEY_Password + " TEXT,"
                + DatabaseConstants.KEY_EMAIL + " TEXT," + DatabaseConstants.KEY_PH_NO + " TEXT," + DatabaseConstants.KEY_BIRTHDATE + " TEXT," + DatabaseConstants.KEY_GENDER + " TEXT," + DatabaseConstants.KEY_ABOUTUS + " TEXT," + DatabaseConstants.KEY_LANGUAGE + " TEXT," + DatabaseConstants.KEY_REMEMBER_ME + " INT ," + DatabaseConstants.KEY_PIC + " TEXT " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * @param response
     * @param isChecked Storing login data
     */
    public void storeLoginDetail(User response, int isChecked, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.KEY_ID, response.getUserId());
        values.put(DatabaseConstants.KEY_NAME, response.getName());
        values.put(DatabaseConstants.KEY_EMAIL, response.getEmail());
        values.put(DatabaseConstants.KEY_PH_NO, response.getContact());
        values.put(DatabaseConstants.KEY_GENDER, response.getGender());
        values.put(DatabaseConstants.KEY_Password, password);
        values.put(DatabaseConstants.KEY_BIRTHDATE, response.getDob());
        values.put(DatabaseConstants.KEY_REMEMBER_ME, isChecked);
        values.put(DatabaseConstants.KEY_ABOUTUS, response.getAbout());
        values.put(DatabaseConstants.KEY_PIC, response.getUserImage());
        values.put(DatabaseConstants.KEY_LANGUAGE, response.getLanguage());
        db.insert(DatabaseConstants.TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    /**
     * @return login data
     */
    public LoginAttributeResponse getUserDetails() {
        LoginAttributeResponse loginData = null;
        String selectQuery = "SELECT  * FROM " + DatabaseConstants.TABLE_LOGIN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                loginData = new LoginAttributeResponse();
                loginData.setEmail(cursor.getString(3));
                loginData.setPassword(cursor.getString(2));
                loginData.setName(cursor.getString(1));
                loginData.setBirthday(cursor.getString(5));
                loginData.setGender(cursor.getString(6));
                loginData.setContact(cursor.getString(4));
                loginData.setId(cursor.getString(0));
                loginData.setRememberMe(cursor.getInt(9));
                loginData.setLanguages(cursor.getString(8));
                loginData.setAboutUs(cursor.getString(7));
                loginData.setUserImage(cursor.getString(10));
            } while (cursor.moveToNext());
        }

        // return contact list
        return loginData;
    }

    /**
     * Deleting table data
     */
    public void deleteTableData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + DatabaseConstants.TABLE_LOGIN);
    }

    public void LoginProfileUpdate(JsonObject object, String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.KEY_NAME, object.get("name").getAsString()); //These Fields should be your String values of actual column names
        cv.put(DatabaseConstants.KEY_ABOUTUS, object.get("aboutUs").getAsString());
        cv.put(DatabaseConstants.KEY_LANGUAGE, object.get("languages").getAsString());
        cv.put(DatabaseConstants.KEY_GENDER, object.get("gender").getAsString());
        cv.put(DatabaseConstants.KEY_BIRTHDATE, object.get("dob").getAsString());
        db.update(DatabaseConstants.TABLE_LOGIN, cv, "id = ?", new String[]{userID});
    }

    public void loginProfileImageUpdate(String imageUrl, String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.KEY_PIC, imageUrl);
        db.update(DatabaseConstants.TABLE_LOGIN, cv, "id = ?", new String[]{userID});
    }


}