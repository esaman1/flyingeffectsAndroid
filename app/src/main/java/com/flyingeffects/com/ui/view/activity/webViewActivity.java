package com.flyingeffects.com.ui.view.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseWebActivity;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;

/**
 * user :TongJu  ;描述：公共的webview
 * 时间：2018/5/14
 **/
public class webViewActivity extends BaseWebActivity {

    private String webUrl;

    @BindView(R.id.webView)
    WebView webView;

    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;


    @Override
    protected int getLayoutId() {
        return R.layout.act_webview;
    }

    @Override
    protected void initView() {
        WaitingDialog.openPragressDialog(this);
        webUrl = getIntent().getStringExtra("webUrl");
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initAction(String cacheUrl) {
        //设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setJavaScriptEnabled(true);
        //是否可以缩放，默认true
        webView.getSettings().setSupportZoom(true);
        //是否显示缩放按钮，默认false
        webView.getSettings().setBuiltInZoomControls(true);

        // 打开本地缓存提供JS调用,至关重要
        webView.getSettings().setDomStorageEnabled(true);
        // 解决用户设置字体大小影响html5适配
        webView.getSettings().setTextZoom(100);

        //设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setUseWideViewPort(true);
        //和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setLoadWithOverviewMode(true);
        // 实现8倍缓存
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //解决5.0 系统+ https 网页嵌套http  图片不显示的问题
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.addJavascriptInterface(new androidJs(), "androidJs"); //第二个参数为js 的调用对象
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (url.contains("tel")) {
                    if (ActivityCompat.checkSelfPermission(webViewActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        url = url.replaceAll("-", "");
                        Uri uri = Uri.parse("tel:" + url);
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                        return true;
                    }
                }


                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }


                view.loadUrl(url);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//接受证书
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WaitingDialog.closePragressDialog();
            }
        });

        //设置支持弹出图片选择，input
        webView.setWebChromeClient(new WebChromeClient() {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            WaitingDialog.closePragressDialog();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }

        });


        webView.loadUrl(webUrl);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != webViewActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nowWebUrl", "error");
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * user :TongJu  ;描述：给js 调用的代码,退出当前界面
     * 时间：2018/6/6
     **/
    public class androidJs extends Object {
        // 被JS调用的方法必须加入@JavascriptInterface注解

        @JavascriptInterface
        public void cardUrl(String url) {
        }

    }





}
