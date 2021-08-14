package com.example.bjb.myapplication.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.bjb.myapplication.MyApplication;
import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;


/**
 * 对WebView的包装,用于显示网页 
 *  
 * @author zhuweiwei
 */
public class WebShower extends WebView implements ViewerCallback {
	
	private boolean transparent;
	private View view;
	private boolean isfirst = true;
	private String url;
	public WebShower(Context context, String url) {
		super(context);
        init();
		initActBar(context);
		loadUrl(url);
	}

	private void registerReceiver() {
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		getContext().registerReceiver(mReceiver, mFilter);
	}



	public WebShower(Context context, String url, boolean transparent) {
		super(context);
		this.transparent = transparent;
		init();
		initActBar(context);
		loadUrl(url);
	}
    
    public WebShower(Context context) {
        super(context);
        init();
    }

    public WebShower(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
	private void initActBar(Context context) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			view = inflater.inflate(R.layout.webview_actionbar, null);
			ImageView goback = (ImageView)view.findViewById(R.id.goback);
			ImageView forward = (ImageView)view.findViewById(R.id.forward);
			ImageView refresh = (ImageView)view.findViewById(R.id.refresh);
			view.setVisibility(View.GONE);
			this.addView(view);

	}
    private void init() {
    	if (transparent) {
    		this.setBackgroundColor(Color.TRANSPARENT);
    	}
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        this.getSettings().setLoadsImagesAutomatically(true);
		this.getSettings().setDatabaseEnabled(true);
		this.getSettings().setGeolocationEnabled(true);
		this.getSettings().setGeolocationDatabasePath(MyApplication.getInstance().getDir("database", Context.MODE_PRIVATE).getPath());
		this.getSettings().setDomStorageEnabled(true);
        this.setWebViewClient(new MyWebViewClient());
        this.setWebChromeClient(new MyWebChromeClient());
		registerReceiver();
    }

	@Override
	public void viewerOnPause(boolean isFinishing) {
		this.onPause();
		this.pauseTimers();
		if (isFinishing) {
			this.loadUrl("about:blank");
		}
	}

	@Override
	public void viewerOnResume() {
		this.onResume();
		this.resumeTimers();
	}

	@Override
	public void viewerOnDestroy() {
		getContext().unregisterReceiver(mReceiver);
	}

    private class MyWebViewClient extends WebViewClient {
        /**
         * 自定义网页加载
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 使用当前的WebView加载页面
            view.loadUrl(url);
            return true;
        }

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//			return super.shouldOverrideKeyEvent(view, event);
			return true;
		}
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(view != null){
					if(view.getVisibility() == View.VISIBLE){
						view.setVisibility(View.GONE);
					}else{
						view.setVisibility(View.VISIBLE);
					}
				}
				break;
		}
		return super.onTouchEvent(event);
	}
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				if(info != null && info.isAvailable()&&info.isConnected()) {
					String name = info.getTypeName();
					Log.d("mark", "当前网络名称：" + name);
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						if(isfirst){
							loadUrl(url);
							isfirst = true;
						}
					}
				} else {
					Log.d("mark", "没有可用网络");
				}
			}
		}
	};
}