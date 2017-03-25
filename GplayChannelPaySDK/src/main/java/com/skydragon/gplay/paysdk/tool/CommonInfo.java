package com.skydragon.gplay.paysdk.tool;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonInfo {
    private final static String TAG = "CommonInfo";

	public static String getEditTextValue(EditText editText) {
        String result = "";
        if(editText == null)
            return result;

        Editable editable = editText.getText();
        if(editable == null)
            return  result;

        result = editable.toString();
        if(result == null)
            result = "";
        return result.trim();
    }

    public static class EditTextOnFocusChangeListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) { //光标移动到末尾
                final EditText et = (EditText) v;
                et.post(new Runnable() {
                    @Override
                    public void run() {
                        if(et.getText() != null)
                            et.setSelection(et.getText().length());
                    }
                });
            }
        }
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "md5 NoSuchAlgorithmException " + e.getMessage());
        }
        return "";
    }

    public static int getLayoutId(Context context, String name) {
        return context.getResources().getIdentifier(name, "layout", context.getPackageName());
    }

    public static int getStringId(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    public static int getDrawableId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getStyleId(Context context, String name) {
        return context.getResources().getIdentifier(name, "style", context.getPackageName());
    }

    public static int getId(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }

    public static int getColorId(Context context, String name) {
        return context.getResources().getIdentifier(name, "color", context.getPackageName());
    }
}
