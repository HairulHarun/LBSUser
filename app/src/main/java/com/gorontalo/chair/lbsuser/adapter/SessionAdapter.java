package com.gorontalo.chair.lbsuser.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gorontalo.chair.lbsuser.LoginActivity;
import com.gorontalo.chair.lbsuser.MainActivity;

import java.net.UnknownServiceException;

public class SessionAdapter {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Sesi";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_SHOW = "IsShow";
    public static final String KEY_ID = "id";
    public static final String KEY_STATUS= "status";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME= "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHOTO = "photo";
    public static final String FIREBASE_TOKEN = "token";
    public static final String KEY_STATUSLOGIN = "statuslogin";
    public static final String KEY_IDGRUP = "idgrup";
    public static final String KEY_NAMAGRUP = "namagrup";

    public SessionAdapter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id,
                                   String status,
                                   String username,
                                   String password,
                                   String name,
                                   String email,
                                   String photo,
                                   String token,
                                   String statuslogin,
                                   String idgrup,
                                   String namagrup){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHOTO, photo);
        editor.putString(FIREBASE_TOKEN, token);
        editor.putString(KEY_STATUSLOGIN, statuslogin);
        editor.putString(KEY_IDGRUP, idgrup);
        editor.putString(KEY_NAMAGRUP, namagrup);
        editor.commit();
    }

    public void simpanToken(String token){
        editor.putString(FIREBASE_TOKEN, token);
        editor.commit();
    }

    public void createDialogSession(){
        editor.putBoolean(IS_SHOW, true);
        editor.commit();
    }

    public void checkLoginMain(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void checkLogin(){
        if(this.isLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public String getID(){
        String user = pref.getString(KEY_ID, null);
        return user;
    }

    public String getName(){
        String user = pref.getString(KEY_NAME, null);
        return user;
    }

    public String getEmail(){
        String user = pref.getString(KEY_EMAIL, null);
        return user;
    }

    public String getIdGrup(){
        String user = pref.getString(KEY_IDGRUP, null);
        return user;
    }

    public String getNamaGrup(){
        String user = pref.getString(KEY_NAMAGRUP, null);
        return user;
    }

    public String getPhoto(){
        String user = pref.getString(KEY_PHOTO, null);
        return user;
    }

    public String getToken(){
        String token = pref.getString(FIREBASE_TOKEN, null);
        return token;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isShow(){
        return pref.getBoolean(IS_SHOW, false);
    }
}
