package com.skydragon.gplay.paysdk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.PayData;
import com.skydragon.gplay.paysdk.tool.CommonInfo;

/**
 * Created by lindaojiang on 2016/1/5.
 */
public class PaySuccessFragment extends Fragment {

    private TextView textViewAmount;
    private Button buttonBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_fragment_pay_success"), container, false);
        textViewAmount = (TextView)view.findViewById(CommonInfo.getId(getContext(), "amount"));
        buttonBack = (Button)view.findViewById(CommonInfo.getId(getContext(), "buttonBack"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arg = getArguments();
        if(arg != null) {
            PayData payData = arg.getParcelable("payData");
            if(payData != null) {
                textViewAmount.setText(payData.getAmount());
            }
        }

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GplayActivity) getActivity()).goBack(true);
            }
        });
    }
}