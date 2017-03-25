package com.skydragon.gplay.paysdk.data;

/**
 * Created by lin on 2015/12/24.
 */
public class ResponseGetCode {
    private String serial; //serial for sms verify code
    private String error;
    private String error_description;

    public ResponseGetCode(String serial, String error, String error_description) {
        this.serial = serial;
        this.error = error;
        this.error_description = error_description;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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
