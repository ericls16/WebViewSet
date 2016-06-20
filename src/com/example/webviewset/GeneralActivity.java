package com.example.webviewset;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 原生webview在activity中配置使用
 * 
 * @author eric liu
 *
 */
public class GeneralActivity extends Activity {

	WebView webView;
	WebSettings webSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		webView = (WebView) findViewById(R.id.webView);
		configureWebView();
	}
	
	private void initListener() {

	}

	private void initData() {
		//打开本包内asset目录下的index.html文件
		webView.loadUrl("file:///android_asset/about.html");
		
		//打开本地sd卡内的index.html文件
//		webView.loadUrl("file:///data/data/com.example.webviewset/www/about.html");
		
		//打开指定URL的html文件
//		webView.loadUrl("https://www.github.com/");
	}

	//初始配置webview
	private void configureWebView() {
		webSettings = webView.getSettings();
		//如果访问的页面中有Javascript，则WebView必须设置支持Javascript
		webSettings.setJavaScriptEnabled(true);
		//支持通过JS打开新窗口 
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		//支持缩放
		webSettings.setSupportZoom(true);
		//支持手势缩放
		webSettings.setBuiltInZoomControls(true);
		//是否显示缩放按钮
		webSettings.setDisplayZoomControls(false);
		//API>= 19(SDK4.4)启动硬件加速，否则启动软件加速
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			//支持自动加载图片
			webSettings.setLoadsImagesAutomatically(true); 
		} else {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			//关闭自动加载图片
			webSettings.setLoadsImagesAutomatically(false);
		}
		//设置加载进来的页面自适应手机屏幕(即支持html的meta标签，让html通过meta标签自适应手机屏幕)
		webSettings.setUseWideViewPort(true);
		//设置以宽度为基准缩小到屏幕大小
		webSettings.setLoadWithOverviewMode(true); 
		//尽可能在屏幕宽度内正常显示，兼容性好(推荐设置)，支持内容重新布局。
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		
		//支持Dom存储API（html存储数据）
		webSettings.setDomStorageEnabled(true);
		//设置的WebView是否应该保存的表单数据。默认值是true。
		webSettings.setSaveFormData(true);
		/**
		 * 设置是否WebView中是否支持多个窗口。
		 * 如果设置为true，onCreateWindow(WebView, boolean, boolean, Message)必须由主机应用程序来实现。
		 * 默认为false。
		 * 注意：这里如果设置为true，则网页超链接的文字链接可能无响应。
		 */
		webSettings.setSupportMultipleWindows(false);
		//支持访问文件
		webSettings.setAllowFileAccess(true);
		
		//设置应用程序的缓存API可用（使用它需要设置缓存路径setAppCachePath）
		webSettings.setAppCacheEnabled(true);
		//优先使用缓存，使用可用的并且没有过期的缓存资源,否则加载网络资源。
		//建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
		if(isNetConnected()){
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); 
		}else{
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
		}
		//设置缓存路径
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/appcache";
		webSettings.setAppCachePath(path);
		//设置缓存的最大字节数(超过最大字节数时在方法)
		webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
		//提高渲染的优先级
		webSettings.setRenderPriority(RenderPriority.HIGH);
		
		//------------------------------------------------
		
		//指定水平滚动条是否有重叠的风格
		webView.setHorizontalScrollbarOverlay(true);
		//水平滚动条是否可用，默认不可用
		webView.setHorizontalScrollBarEnabled(false);
		//取消WebView中滚动或拖动到顶部、底部时的阴影
		webView.setOverScrollMode(View.OVER_SCROLL_NEVER); 
		//取消滚动条白边效果
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); 
        //如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。
        webView.requestFocusFromTouch();
        
        //------------------------------------------------
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	//点击链接继续在当前browser中响应;
            	Log.i("url", ""+url);
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
            	super.onPageFinished(view, url);
            	//在页面装入完成后设置加载图片
            	if(!webSettings.getLoadsImagesAutomatically()) {
            		webSettings.setLoadsImagesAutomatically(true);
                }
            }
            
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            	super.onReceivedSslError(view, handler, error);
//            	//handler.cancel(); // Android默认的处理方式
//            	handler.proceed();  // 接受所有网站的证书
//            	//handleMessage(Message msg); // 进行其他处理
//            }
        });
        //设置浏览器的处理
        webView.setWebChromeClient(new WebChromeClient(){
        	@SuppressWarnings("deprecation")
			@Override
        	public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
        		//当缓存超过最大字节数时
        		super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        	}
        });
		
	}
	
	//设置webview浏览网页，按back键返回上一个浏览的网页，也可在webview对象配置里设置
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();//返回上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	/**
	 * 判断是否联网；
	 * @param context
	 */
	public boolean isNetConnected(){
		ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni=cm.getActiveNetworkInfo();
		if(ni==null){ //判断网络是否存在；
			return false;
		}
		return ni.isConnected();//返回是否联网；
	}

}
