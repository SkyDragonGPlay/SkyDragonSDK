package com.skydragon.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skydragon.gplay.paysdk.h5.GplayH5PaySDK;
import com.skydragon.gplay.paysdk.h5.demo.R;
import com.skydragon.gplay.paysdk.h5.model.OAuthData;
import com.skydragon.gplay.paysdk.h5.model.PayData;
import com.skydragon.gplay.paysdk.h5.model.UserInfo;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private GplayH5PaySDK mGplayHybridSDK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mGplayHybridSDK = new GplayH5PaySDK();
//        GplayHybridSDK.init(this, "1000", "1000");
//        mGplayHybridSDK.init(this, "jsusdid", "jsusdsecret");
        mGplayHybridSDK.init(this, "56b1d13642672", "CLZ3p3IJlnIgVj7h4DL1ZBr_w9Ij_rY5");
    }

    public void onLogin(View view){
        mGplayHybridSDK.login(MainActivity.this, new GplayH5PaySDK.OAuthResponse() {
            @Override
            public void onResponse(OAuthData data) {
                if (data.getResultCode() == OAuthData.RESULT_CODE_SUCCESS) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "登录成功 uid=" + data.getUid() + " " + (data.isTrial() ? "试玩帐号" : "正式用户"));
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_FAIL) {
                    Toast.makeText(MainActivity.this, "登录失败" + data.getErrorDescription(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "登录失败 " + data.getErrorDescription());
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_CANCEL) {
                    Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "取消登录");
                }
                Log.d(TAG, "OAuthData=" + data.toString());
            }
        });
    }
    public void onRegister(View view){
        mGplayHybridSDK.register(MainActivity.this, new GplayH5PaySDK.OAuthResponse() {
            @Override
            public void onResponse(OAuthData data) {
                if (data.getResultCode() == OAuthData.RESULT_CODE_SUCCESS) {
                    Toast.makeText(MainActivity.this, "注册登录成功", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "注册登录成功 uid=" + data.getUid() + " " + (data.isTrial() ? "试玩帐号" : "正式用户"));
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_FAIL) {
                    Toast.makeText(MainActivity.this, "登录或注册失败" + data.getErrorDescription(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "登录或注册失败 " + data.getErrorDescription());
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_CANCEL) {
                    Toast.makeText(MainActivity.this, "取消注册", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "取消注册");
                }
                Log.d(TAG, "OAuthData=" + data.toString());
            }
        });
    }
    public void onBindPhone(View view){
        mGplayHybridSDK.bind(MainActivity.this, new GplayH5PaySDK.OAuthResponse() {
            @Override
            public void onResponse(OAuthData data) {
                if (data.getResultCode() == OAuthData.RESULT_CODE_SUCCESS) {
                    Toast.makeText(MainActivity.this, "绑定成功", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "绑定成功 uid=" + data.getUid() + " " + (data.isTrial() ? "试玩帐号" : "正式用户"));
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_FAIL) {
                    Toast.makeText(MainActivity.this, "绑定失败" + data.getErrorDescription(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "绑定失败 " + data.getErrorDescription());
                } else if (data.getResultCode() == OAuthData.RESULT_CODE_CANCEL) {
                    Toast.makeText(MainActivity.this, "取消绑定", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "取消绑定");
                }
                Log.d(TAG, "OAuthData=" + data.toString());
            }
        });
    }
    public void onPay(View view){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OrderId").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String orderId = inputServer.getText().toString();
                mGplayHybridSDK.pay(MainActivity.this, orderId, "100000", "商品名称1", "商品描述2", null, new GplayH5PaySDK.PayResponse() {
                    @Override
                    public void onResponse(PayData data) {
                        if (data.getResultCode() == PayData.RESULT_CODE_SUCCESS) {
                            Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "支付成功");
                        } else if (data.getResultCode() == PayData.RESULT_CODE_FAIL) {
                            Toast.makeText(MainActivity.this, "支付失败" + data.getErrorDescription(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "支付失败" + data.getErrorDescription());
                        } else if (data.getResultCode() == PayData.RESULT_CODE_CANCEL) {
                            Toast.makeText(MainActivity.this, "取消支付", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "取消支付");
                        } else if (data.getResultCode() == PayData.RESULT_CODE_INVALID) {
                            Toast.makeText(MainActivity.this, "插件未安装", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "插件未安装");
                        }
                        Log.d(TAG, "PayData=" + data.toString());
                    }
                });
            }
        });
        builder.show();

    }

    public void onUserInfo(View view){
        mGplayHybridSDK.getUser(new GplayH5PaySDK.UserResponse() {
            @Override
            public void onResponse(UserInfo user) {
                if(user != null) { //从未登录过为空
                    /*
                    Log.d(TAG, "最后登录的用户:\n" //+ user.toString() + "\n"
                            + (TextUtils.isEmpty(user.getPhone())?"未绑定手机":"已绑定手机") + "\n"
                            + "access token='" + user.getAccessToken() + "'\n"
                            + "refresh token='" + user.getRefreshToken() + "'\n"
                            + "expires at=" + user.getExpiresAt() + "\n"
                            + "access token='" + GplayHybridSDK.getAccessToken() + "'\n"
                            + "refresh token='" + GplayHybridSDK.getRefreshToken() + "'"
                    );
                    */
                }
                else {
                    Log.d(TAG, "无用户");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mGplayHybridSDK.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mGplayHybridSDK.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGplayHybridSDK.onActivityResult(this, requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult requestCode="+requestCode+" resultCode="+resultCode);
        if(resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult RESULT_CANCELED !");
        }
    }
}
