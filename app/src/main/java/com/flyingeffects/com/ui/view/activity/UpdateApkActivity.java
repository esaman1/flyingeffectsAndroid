package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ClassName UpdateApkActivity
 * Description 检查更新
 * author HRXA)
 * date 2015年3月16日
 */
public class UpdateApkActivity extends Activity implements OnClickListener {

    private String TAG="UpdateApkActivity";

    @BindView(R.id.right_now_update)
    TextView right_now_update;


    @BindView(R.id.iv_close)
    ImageView iv_close;

    @BindView(R.id.tv_content)
    TextView tv_content;

    private FileOutputStream fos;


    @BindView(R.id.relative_parents)
    RelativeLayout relative_parents;

    private String updataFlag = "0";
    /**
     * 是否需要强制升级 0不强制升级1要强制升级
     */
    private String policy = "0";
    private String content;
    private String loadUrl = ""; //下载地址
    private String is_must_update;// 是否强制更新  1是需要
    private AlertDialog dialog;
    private LayoutInflater inflater;


    /**
     * 需要现在的版本
     */
//    private String vison = "0";
    public File getFileFromServer(String path) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            } catch (Exception ignored) {
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            float length = conn.getContentLength();
            // 获取到文件的大小
            // conn.getContentLength();
            InputStream is = conn.getInputStream();
            //新建一个File，传入文件夹目录
            File file_instalApk = new File(Environment.getExternalStorageDirectory() + "/kadian");
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file_instalApk.exists()) {
                file_instalApk.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/kadian", "kadian" + ".apk");
            fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            float count = 0;
            while ((len = bis.read(buffer)) != -1) {
                Message msg = new Message();
                fos.write(buffer, 0, len);
                count += len;
                msg.arg1 = (int) (count * 100 / length);
                msg.what = 100;
                handler.sendMessage(msg);
            }
            Message msg = new Message();
            msg.what = 101;
            handler.sendMessage(msg);
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }


//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_apk_update;
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_update);
        ButterKnife.bind(this);
        initView();
        initAction();
    }



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
//                    progressDialog.setProgress(msg.arg1);
                    progressbar.setProgress(msg.arg1);

                    break;
                case 101:
//                    progressDialog.cancel();
                    dialog.dismiss();
                    break;
                case 102:
//                    progressDialog.show();

                    showDialog();

                    break;
            }
        }
    };

    protected void initView() {
        inflater = LayoutInflater.from(this);
    }

    protected void initAction() {
        content = getIntent().getStringExtra("content");
        if (content != null && !content.equals("")) {
            tv_content.setText(content);
        }
        policy = getIntent().getExtras().getString("policy");
        is_must_update = getIntent().getExtras().getString("is_must_update");
        if ("0".equals(updataFlag)) {
            loadUrl = getIntent().getExtras().getString("url");
            right_now_update.setOnClickListener(this);
            if (policy.equals("1")) {
                //强制升级
                if (!StringUtil.isNull(loadUrl)) {
                    handler.sendEmptyMessage(102);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                File file = getFileFromServer(loadUrl);
                                if (null != file) {
                                    installApk(file);
                                }
                            } catch (Exception e) {
                                LogUtil.e(TAG, e.getMessage());
                            }
                            super.run();
                        }
                    }.start();
                }
            }
        } else {
            findViewById(R.id.iv_close).setVisibility(View.GONE);
        }
        iv_close.setOnClickListener(this);
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0以上就不可以了
            //之后
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //File file = (new File(apkPath));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(this, "com.mobile.kadian.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            startActivity(intent);
            UpdateApkActivity.this.finish();
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            this.startActivity(i);
            UpdateApkActivity.this.finish();
        }


    }

//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.iv_close:
//                this.finish();
//                break;
//            case R.id.right_now_update:
//                if (!DoubleClick.getInstance().isFastDoubleClick()) {
//                    checkJurisdiction();
//                }
//                break;
//            default:
//                break;
//        }
//    }


    private void downApk() {
        if (!StringUtil.isNull(loadUrl)) {
            handler.sendEmptyMessage(102);
            new Thread() {
                @Override
                public void run() {
                    try {
                        File file = getFileFromServer(loadUrl);
                        if (null != file) {
                            installApk(file);
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("获取下载路径失败，请稍后重试");
        }

    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2019/1/9
     * describe:检查权限 ，需要数据读取权限
     **/

    private void checkJurisdiction() {
        downApk();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (is_must_update.equals("1")) {
                exitPressAgain();
            } else {
                this.finish();
            }
        }
        return true;
    }

    private long exitTime = 0;


    // 再按一次返回键退出
    private void exitPressAgain() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2019/1/11
     * describe:显示进度对话框
     **/


    private NumberProgressBar progressbar;

    private void showDialog() {
        relative_parents.setVisibility(View.INVISIBLE);
        iv_close.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                new ContextThemeWrapper(this, R.style.Theme_Transparent));
        View view = inflater.inflate(R.layout.dialog_update_apk, null);
        progressbar = view.findViewById(R.id.number_progress_bar);
        progressbar.setMax(100);
        progressbar.setProgress(0);
        builder.setView(view);


        builder.setCancelable(false);
        dialog = builder.show();
        final Window window = dialog.getWindow();
        if (window != null) {
            window.getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent));
            window.getDecorView().setPadding(0, 0, 0, 0);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnCancelListener(dialogInterface -> UpdateApkActivity.this.finish());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUpdate();
    }


    private void stopUpdate() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory(), "renwodaiUpdate" + ".apk");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                this.finish();
                break;
            case R.id.right_now_update:
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    checkJurisdiction();
                }
                break;
            default:
                break;
        }
    }
}
