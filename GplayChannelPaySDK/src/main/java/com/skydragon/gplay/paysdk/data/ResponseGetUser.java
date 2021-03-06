package com.skydragon.gplay.paysdk.data;

/**
 * Created by lindaojiang on 2016/1/4.
 */
public class ResponseGetUser {
    private String username;
    private String user_id;
    private String phone;
    private String error;
    private String error_description;

    public ResponseGetUser(String username, String user_id, String phone, String error, String error_description) {
        this.username = username;
        this.user_id = user_id;
        this.phone = phone;
        this.error = error;
        this.error_description = error_description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
