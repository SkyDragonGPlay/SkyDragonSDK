package com.skydragon.gplay.paysdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.PayData;
import com.skydragon.gplay.paysdk.global.SdkUtils;
import com.skydragon.gplay.paysdk.global.ThisApp;
import com.skydragon.gplay.paysdk.tool.CommonInfo;
import com.skydragon.gplay.paysdk.global.Core;
import com.pingplusplus.android.PaymentActivity;

/**
 * Created by lin on 2016/1/3.
 */
public class PayFragment extends Fragment {
    private static final String TAG = "PayFragment";

    private static final int REQUEST_CODE_PAYMENT = 111;
    private static final String CHANNEL_ALIPAY = "alipay"; //支付宝
    private static final String CHANNEL_UPACP = "upacp";   //银联

    private TextView textViewAmount;
    private LinearLayout layoutAlipay;
    private LinearLayout layoutUpmp;

    private static Context appContext;
    private PayData payData;
    private Handler handler;

    private static final int SEND_MSG_NONE = 0;
    private static final int SEND_MSG_CHARGE_SUCCESS = 1;
    private static final int SEND_MSG_CHARGE_FAIL = 2;
    private static int sendMsg = SEND_MSG_NONE;
    private static String sendData = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_frament_pay"), container, false);
        textViewAmount = (TextView)view.findViewById(CommonInfo.getId(getContext(), "amount"));
        layoutAlipay = (LinearLayout)view.findViewById(CommonInfo.getId(getContext(), "layoutAlipay"));
        layoutUpmp = (LinearLayout)view.findViewById(CommonInfo.getId(getContext(), "layoutUpmp"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new PayHandler();

        payData = null;
        if(savedInstanceState != null) {
            payData = savedInstanceState.getParcelable("payData");
        }
        if(payData == null) {
            payData = new PayData();
            Bundle arg = getArguments();
            if (arg != null) {
                payData.setOrder(arg.getString("order"));
                payData.setAmount(arg.getString("amount"));
                payData.setName(arg.getString("name"));
                payData.setDesc(arg.getString("desc"));
                payData.setExtra(arg.getString("extra"));
            }
        }

        textViewAmount.setText(payData.getAmount());
        layoutAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payData.setChannel(CHANNEL_ALIPAY);
                payAction();
            }
        });
        layoutUpmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payData.setChannel(CHANNEL_UPACP);
                payAction();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sendMsg != SEND_MSG_NONE) {
            handler.sendEmptyMessage(sendMsg);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("payData", payData);
    }

    private void payAction() {
        //show dialog
        CustomDialogFragment.showDialog(getFragmentManager(), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_createBill")), null, null, null);

        SdkUtils.getCharge(payData, appContext, new SdkUtils.GplayHttpsResult1() {
            @Override
            public void onSuccess(String data) {
                CustomDialogFragment.dismissInstance();
                sendData = data;
                sendMsg = SEND_MSG_CHARGE_SUCCESS;
                handler.sendEmptyMessage(sendMsg);
            }

            @Override
            public void onFailure(String error) {
                CustomDialogFragment.dismissInstance();
                payData.setResultCode(PayData.RESULT_CODE_FAIL);
                payData.setErrorDescription(error);
                Core.getInstance().setPayData(payData);
                sendMsg = SEND_MSG_CHARGE_FAIL;
                handler.sendEmptyMessage(sendMsg);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK) {
            String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
            if(!TextUtils.isEmpty(result)) {
                if(result.contentEquals("success")) {
                    payData.setResultCode(PayData.RESULT_CODE_SUCCESS);
                }
                else if(result.contentEquals("fail")) {
                    payData.setResultCode(PayData.RESULT_CODE_FAIL);
                }
                else if(result.contentEquals("invalid")) {
                    payData.setResultCode(PayData.RESULT_CODE_INVALID);
                }
                else {
                    payData.setResultCode(PayData.RESULT_CODE_CANCEL);
                }
            }
            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
            String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

            if(ThisApp.isDebug)
                Log.d(TAG, "pay result=" + result + " errorMsg=" + errorMsg + " extraMsg=" + extraMsg);

            payData.setErrorDescription(errorMsg + ((!TextUtils.isEmpty(errorMsg) && !TextUtils.isEmpty(extraMsg)) ? " " : "") + extraMsg);
            Core.getInstance().setPayData(payData);

            if(payData.getResultCode() == PayData.RESULT_CODE_SUCCESS) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("payData", payData);
                GplayActivity activity = (GplayActivity) getActivity();
                activity.unloadLastFragment();
                activity.loadPaySucessFragment(bundle);
            }
            else if(payData.getResultCode() == PayData.RESULT_CODE_CANCEL) {
                ((GplayActivity) getActivity()).goBack(true);
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("payData", payData);
                ((GplayActivity)getActivity()).loadPayFailFragment(bundle);
            }
        }
    }

    private static class PayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_MSG_CHARGE_SUCCESS:
                {
                    synchronized (PayFragment.class) {//lock for receive twice
                        if(Core.getGplayActivity() != null && sendMsg == SEND_MSG_CHARGE_SUCCESS) {
                            //call ping++
                            Intent intent = new Intent(Core.getGplayActivity(), PaymentActivity.class);
                            intent.putExtra(PaymentActivity.EXTRA_CHARGE, sendData);
                            PayFragment fragment = (PayFragment)Core.getGplayActivity().getSupportFragmentManager().findFragmentByTag(GplayActivity.FRAGMENT_PAY);
                            if(fragment != null) {
                                fragment.startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                                sendMsg = SEND_MSG_NONE;
                            }
                        }
                    }
                    break;
                }
                case SEND_MSG_CHARGE_FAIL:
                {
                    synchronized (PayFragment.class) {//lock for receive twice
                        if(Core.getGplayActivity() != null && sendMsg == SEND_MSG_CHARGE_FAIL) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("payData", Core.getInstance().getPayData());
                            Core.getGplayActivity().loadPayFailFragment(bundle);
                            sendMsg = SEND_MSG_NONE;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}