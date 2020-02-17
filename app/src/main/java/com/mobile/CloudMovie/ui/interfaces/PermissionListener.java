package com.mobile.CloudMovie.ui.interfaces;

import java.util.List;

/**
 * 作者:Administrator on 2019/1/24 17:18
 * 邮箱:jutongzhang@sina.com
 */

public interface PermissionListener {
    //授权成功
    void onGranted();

    //授权部分
    void onGranted(List<String> grantedPermission);

    //拒绝授权
    void onDenied(List<String> deniedPermission);
}



