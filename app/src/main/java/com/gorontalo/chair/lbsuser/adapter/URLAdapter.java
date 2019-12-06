package com.gorontalo.chair.lbsuser.adapter;

public class URLAdapter {
    private String URL = "http://192.168.43.163/cee/webservices/";

    private String URL_PHOTO = "http://192.168.43.163/cee/images/anggota/";

    public String loginUsers() {
        return URL = URL + "ws-login-users.php";
    }

    public String updateLokasiUsers() {
        return URL = URL + "ws-update-lokasi-users.php";
    }

    public String updateStatusLogin() {
        return URL = URL + "ws-update-status-login.php";
    }
}
