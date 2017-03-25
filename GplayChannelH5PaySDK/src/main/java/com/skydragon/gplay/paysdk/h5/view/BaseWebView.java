package com.skydragon.gplay.paysdk.h5.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;
import com.skydragon.gplay.paysdk.h5.controller.HybridJSInterface;

import java.lang.reflect.Method;

/**
 * Project Name : BaseWebView
 * File Name : BaseWebView.java
 * Package Name : com.skydragon.hybridsdk.view
 * 
 * Description:
 * 
 * @author Y.J.Zhou
 * @date 2016-04-11 14:04:50
 */
public class BaseWebView extends FrameLayout {
	private final String JAVASCRIPT = "javascript:";

	/**
	 * Base webview for this FrameLayout
	 * */
	private WebView mWebView;

	public BaseWebView(Context context){
		super(context);
		initWebView(context, null);
	}

	public BaseWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWebView(context, attrs);
	}

	private void initWebView(Context context, AttributeSet attrs) {

		mWebView = new WebView(context, attrs);
		mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mWebView);

		/**
		 * set the webview settings
		 * */
		initWebViewSettings();

		/**
		 * Cancled srollbar
		 * */
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		/**
		 * set webview text editable
		 */
		setEditable();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebViewSettings() {
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(true);
		settings.setDomStorageEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		try{
			settings.setSavePassword(false);
		} catch(Exception e){
		}
		
		if (Build.VERSION.SDK_INT >= 19) {
			settings.setLoadsImagesAutomatically(true);
		} else {
			settings.setLoadsImagesAutomatically(false);
		}

		mWebView.setWebViewClient(mWebClient);
	}

	/**
	 * set webview text editable
	 */
	private void setEditable() {
		try {
			if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR) {
				Method m = WebView.class.getMethod("emulateShiftHeld", Boolean.TYPE);
				m.invoke(mWebView, false);
			} else {
				Method m = WebView.class.getMethod("emulateShiftHeld");
				m.invoke(mWebView);
			}
		} catch (Exception e) {
			if(SDKConstant.isDebug)
				Log.v("BaseWebView", "BaseWebView#setEditable error : " + e.toString());
			KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(mWebView, getKeyDispatcherState(), mWebView);
		}
	}

	private final WebViewClient mWebClient = new WebViewClient() {

		public void onPageStarted(WebView view, String url,
				android.graphics.Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			LoadingDialog.showLoadingDialog(BaseWebView.this.getContext(), null, 6000, null);
		}

		public void onLoadResource(WebView view, String url) {}

		@Override
		public void onPageFinished(final WebView view, String url) {
			super.onPageFinished(view, url);
			LoadingDialog.closeLoadingDialog();
		}

	};

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP
				&& mWebView.canGoBack()) {

			notifyJsWebviewGoback();

			mWebView.goBack();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void notifyJsWebviewGoback(){
		if(mWebView != null && HybridJSInterface.mJSBackAction != null){

			if(SDKConstant.isDebug)
				Log.d("", "0--0930-1 BaseWebView#notifyJsWebviewGoback  " + HybridJSInterface.mJSBackAction);

			mWebView.loadUrl(JAVASCRIPT + HybridJSInterface.mJSBackAction);
		}

		HybridJSInterface.mJSBackAction = null;
	}

	public void loadUrl(String url) {
		if (mWebView != null) {
			mWebView.loadUrl(url);
		}
	}

	public void destory(){
		LoadingDialog.closeLoadingDialog();
		if (mWebView != null) {
			WebSettings settings = mWebView.getSettings();
			settings.setJavaScriptEnabled(false);
			mWebView.destroy();
			mWebView = null;
		}
	}

	@SuppressLint("JavascriptInterface")
	public void addJavascriptInterface(Object object, String name) {
		if (mWebView != null)
			mWebView.addJavascriptInterface(object, name);
	}
}
