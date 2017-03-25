package com.skydragon.gplay.paysdk.data;

/**
 * Created by lindaojiang on 2015/12/25.
 */
public class ResponseBind {
    private String success;
    private String error;
    private String error_description;

    public ResponseBind(String success, String error, String error_description) {
        this.success = success;
        this.error = error;
        this.error_description = error_description;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
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
