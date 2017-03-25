package com.skydragon.gplay.paysdk.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.global.ThisApp;
import com.skydragon.gplay.paysdk.global.Core;
import com.skydragon.gplay.paysdk.tool.CommonInfo;

/**
 * Created by lindaojiang on 2016/1/6.
 */
public class CustomDialogFragment extends DialogFragment {
    private static final String TAG = "CustomDialogFragment";
    private static final String ARG_TITLE = "title";
    private static final String ARG_HINT = "hint";
    private static final String ARG_CANCEL = "cancel";
    private static boolean needDismiss = false;

    public static void showDialog(FragmentManager manager, String title, String hint, String cancel, Timing callback){
        needDismiss = false;
        timing = callback;

        Bundle arg = new Bundle();
        arg.putString(ARG_TITLE, title);
        arg.putString(ARG_HINT, hint);
        arg.putString(ARG_CANCEL, cancel);
        CustomDialogFragment fragment = new CustomDialogFragment();
        fragment.setArguments(arg);
        fragment.show(manager, GplayActivity.FRAGMENT_DIALOG);
    }

    public static CustomDialogFragment getInstance() {
        if(Core.getGplayActivity() != null && Core.getGplayActivity().getSupportFragmentManager() != null) { //if getActivity()=null-->getSupportFragmentManager()=null
            return (CustomDialogFragment)Core.getGplayActivity().getSupportFragmentManager().findFragmentByTag(GplayActivity.FRAGMENT_DIALOG);
        }
        return null;
    }

    public static void dismissInstance() {
        needDismiss = true;
        CustomDialogFragment dialog = getInstance();
        if(dialog != null) {
            dialog.dismiss();
        }
        else {
            if(ThisApp.isDebug)
                Log.d(TAG, "dismissInstance instance is null");
        }
    }

    private CircleProgressBar progressbar;
    private TextView textViewTitle;
    private TextView textViewHint;
    private Button buttonCancel;

    private String title;
    private String hint;
    private String cancel;
    private int seconds;
    private Handler handler;
    private static Timing timing = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        setStyle(STYLE_NO_TITLE, CommonInfo.getStyleId(getContext(), "GplayThirdSdk_DialogTheme"));
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_dialog"), container, false);
        progressbar = (CircleProgressBar) view.findViewById(CommonInfo.getId(getContext(), "progressbar"));
        textViewTitle = (TextView) view.findViewById(CommonInfo.getId(getContext(), "title"));
        textViewHint = (TextView) view.findViewById(CommonInfo.getId(getContext(), "hint"));
        buttonCancel = (Button) view.findViewById(CommonInfo.getId(getContext(), "cancel"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(needDismiss) {
            dismiss();
            return;
        }
        handler = new Handler();

        progressbar.setColorSchemeResources(CommonInfo.getColorId(getContext(), "GplayThirdSdk_holo_green_light"), CommonInfo.getColorId(getContext(), "GplayThirdSdk_holo_orange_light"), CommonInfo.getColorId(getContext(), "GplayThirdSdk_holo_red_light"));

        title = null;
        hint = null;
        cancel = null;
        Bundle arg = getArguments();
        if(arg != null) {
            title = arg.getString(ARG_TITLE);
            hint = arg.getString(ARG_HINT);
            cancel = arg.getString(ARG_CANCEL);
        }
        seconds = 0;

        if(savedInstanceState != null) {
            title = savedInstanceState.getString(ARG_TITLE);
            hint = savedInstanceState.getString(ARG_HINT);
            cancel = savedInstanceState.getString(ARG_CANCEL);
            seconds = savedInstanceState.getInt("seconds");
        }

        setTitle(title);
        setHint(hint);
        setCancelButton(cancel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TITLE, title);
        outState.putString(ARG_HINT, hint);
        outState.putString(ARG_CANCEL, cancel);
        outState.putInt("seconds", seconds);
    }

    public void setTitle(String title) {
        this.title = title;
        if(!TextUtils.isEmpty(title)) {
            textViewTitle.setVisibility(View.VISIBLE);
            textViewTitle.setText(title);
        }
        else {
            textViewTitle.setVisibility(View.GONE);
        }
    }

    public void setHint(String hint){
        this.hint = hint;
        if(!TextUtils.isEmpty(hint)) {
            textViewHint.setVisibility(View.VISIBLE);
            textViewHint.setText(hint);
        }
        else{
            textViewHint.setVisibility(View.GONE);
        }
    }

    public void setCancelButton(String cancel) {
        this.cancel = cancel;
        if(!TextUtils.isEmpty(cancel)) {
            buttonCancel.setText(cancel);
            buttonCancel.setVisibility(View.VISIBLE);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialogFragment.this.dismiss();
                }
            });
        }
        else {
            buttonCancel.setVisibility(View.GONE);
        }
    }

    /**
     * must set before CustomDialogFragment lifecycle onStart
     */
    public interface Timing{
        /**
         * @param dialog CustomDialogFragment.this
         * @param second seconds start from 1
         * @return true:continue callback this interface, false:stop call this interface
         */
        boolean onSecond(CustomDialogFragment dialog, int second);
    }

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            if(timing != null) {
                boolean ret = timing.onSecond(CustomDialogFragment.this, seconds);
                if(ret)
                    handler.postDelayed(this, 1000);
                else
                    timing = null;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        if(needDismiss) {
            dismiss();
            return;
        }

        if(timing != null)
            handler.postDelayed(timeRunnable, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(timing != null)
            handler.removeCallbacks(timeRunnable);
    }
}