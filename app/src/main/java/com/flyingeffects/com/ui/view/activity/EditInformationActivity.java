package com.flyingeffects.com.ui.view.activity;

import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.keyBordUtils;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author ZhouGang
 * @date 2020/10/10
 * 编辑资料
 */
public class EditInformationActivity extends BaseActivity implements AlbumChooseCallback {
    public final static int SELECTALBUMFROMUSERAVATAR = 1;
    private static final int CODE_AVATAR = 11;

    @BindView(R.id.iv_Avatar)
    ImageView ivAvatar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_introduction)
    EditText etIntroduction;

    String avatarPath;
    UserInfo userInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_info;
    }

    @Override
    protected void initView() {
        userInfo = Hawk.get("UserInfo");
        etName.setText(userInfo.getNickname());
        etIntroduction.setText(userInfo.getRemark());
        if(TextUtils.isEmpty(userInfo.getPhotourl())){
            avatarPath ="";
            Glide.with(this)
                    .load(R.mipmap.head)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivAvatar);
        }else {
            avatarPath = userInfo.getPhotourl();
            Glide.with(this)
                    .load(userInfo.getPhotourl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivAvatar);
        }
    }

    @Override
    protected void initAction() {

    }


    @OnClick({R.id.iv_top_back, R.id.rl_Avatar,R.id.tv_enter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.rl_Avatar:
                ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, CODE_AVATAR);
                //AlbumManager.chooseImageAlbum(this,1,SELECTALBUMFROMUSERAVATAR,this,"");

                break;
            case R.id.tv_enter:
                if(TextUtils.isEmpty(avatarPath)){
                    ToastUtil.showToast("请选择一个要修改的头像");
                    return;
                }
                if(TextUtils.isEmpty(etName.getText().toString())){
                    ToastUtil.showToast("请填写昵称");
                    return;
                }
                if (TextUtils.equals(etName.getText().toString().trim(), userInfo.getNickname()) &&
                        TextUtils.equals(avatarPath, userInfo.getPhotourl()) &&
                        TextUtils.equals(etIntroduction.getText().toString().trim(), userInfo.getRemark())) {
                    this.finish();
                }
                submitEditInfo(etName.getText().toString(),etIntroduction.getText().toString(),avatarPath);
                break;
            default:
                break;
        }
    }

    private void uploadFileToHuawei(String videoPath, String copyName) {
        WaitingDialog.openPragressDialog(this);
        Log.d("OOM2", "uploadFileToHuawei" + "当前上传的地址为" + videoPath + "当前的名字为" + copyName);
        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                if (!TextUtils.isEmpty(str)) {
                    String path = str.substring(str.lastIndexOf("=") + 1, str.length() - 1);
                    avatarPath = path;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(EditInformationActivity.this)
                                    .load(avatarPath)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(ivAvatar);
                            WaitingDialog.closeProgressDialog();
                        }
                    });
                }
            }
        })).start();
    }

    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera,ArrayList<AlbumFile> albumFileList) {
        if (!isCancel &&  paths != null && paths.size() > 0) {
            String path = paths.get(0);
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String huaweiAvatarPath = "media/android/user_avatar_img/" + nowTime + "/" + System.currentTimeMillis() + type;
            uploadFileToHuawei(path,huaweiAvatarPath);
        }
    }

    private void submitEditInfo(String name,String introduction,String avatar){
        HashMap<String, String> params = new HashMap<>();
        params.put("photourl", avatar);
        params.put("nickname",name);
        params.put("profile",introduction);
        HttpUtil.getInstance().toSubscribe(Api.getDefault().memberEdit(BaseConstans.getRequestHead(params)),
                new ProgressSubscriber<Object>(EditInformationActivity.this) {
                    @Override
                    protected void onSubError(String message) {
                        ToastUtil.showToast(message);
                    }

                    @Override
                    protected void onSubNext(Object data) {
                        Glide.with(EditInformationActivity.this)
                                .load(avatar)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(ivAvatar);
                        finish();
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                //调用方法判断是否需要隐藏键盘
                keyBordUtils.hideKeyboard(ev, view, EditInformationActivity.this);
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> deniedPermission = new ArrayList<>();
        deniedPermission.clear();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int result = grantResults[i];
            if (result != PackageManager.PERMISSION_GRANTED) {
                deniedPermission.add(permission);
            }
        }
        if (deniedPermission.isEmpty()) {
            if (requestCode == CODE_AVATAR) {
                AlbumManager.chooseImageAlbum(this,1,SELECTALBUMFROMUSERAVATAR,this,"");
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("读取相册必须获取存储权限，如需使用接下来的功能，请同意授权~")
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("去授权", (dialog, which) -> {
                        PermissionUtil.gotoPermission(this);
                        dialog.dismiss();
                    }).create()
                    .show();
        }
    }
}
