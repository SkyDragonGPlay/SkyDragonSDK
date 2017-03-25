package com.skydragon.gplay.paysdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skydragon.gplay.paysdk.GplayPaySDK;
import com.skydragon.gplay.paysdk.GplayUser;
import com.skydragon.gplay.paysdk.OAuthData;
import com.skydragon.gplay.paysdk.PayData;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        GplayThirdSDK.init(this, "1000", "1000");
        GplayPaySDK.init(this, "56b1d13642672", "CLZ3p3IJlnIgVj7h4DL1ZBr_w9Ij_rY5");


        Button buttonLogin = (Button)findViewById(R.id.buttonLogin);
        Button buttonRegister = (Button)findViewById(R.id.buttonRegister);
        Button buttonBind = (Button)findViewById(R.id.buttonBind);
        Button buttonPay = (Button)findViewById(R.id.buttonPay);
        Button buttonGetUser = (Button)findViewById(R.id.buttonGetUser);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayPaySDK.login(MainActivity.this, new GplayPaySDK.OAuthResponse() {
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
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayPaySDK.register(MainActivity.this, new GplayPaySDK.OAuthResponse() {
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
        });

        buttonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayPaySDK.bind(MainActivity.this, new GplayPaySDK.OAuthResponse() {
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
        });

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayPaySDK.pay(MainActivity.this, "2016010510033433", "10000.0元", "商品名称1", "商品描述2", null, new GplayPaySDK.PayResponse() {
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

        buttonGetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayPaySDK.getUser(new GplayPaySDK.UserResponse() {
                    @Override
                    public void onResponse(GplayUser user) {
                        if(user != null) { //从未登录过为空
                            Log.d(TAG, "最后登录的用户:\n" //+ user.toString() + "\n"
                                            + (TextUtils.isEmpty(user.getPhone())?"未绑定手机":"已绑定手机") + "\n"
                                            + "access token='" + user.getAccessToken() + "'\n"
                                            + "refresh token='" + user.getRefreshToken() + "'\n"
                                            + "expires at=" + user.getExpiresAt() + "\n"
                                            + "access token='" + GplayPaySDK.getAccessToken() + "'\n"
                                            + "refresh token='" + GplayPaySDK.getRefreshToken() + "'"
                            );
                        }
                        else {
                            Log.d(TAG, "无用户");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        GplayPaySDK.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        GplayPaySDK.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GplayPaySDK.onActivityResult(this, requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult requestCode="+requestCode+" resultCode="+resultCode);
        if(resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult RESULT_CANCELED !");
        }
    }
}
