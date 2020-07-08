package com.flyingeffects.com.manager;

import android.text.TextUtils;

import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.utils.LogUtil;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StimulateControlManage {

    private static StimulateControlManage thisModel;

    public static StimulateControlManage getInstance() {

        if (thisModel == null) {
            thisModel = new StimulateControlManage();
        }
        return thisModel;

    }


    public void InitRefreshStimulate() {
        String str = Hawk.get("AuditModeConfig");
        UserInfo userInfo=Hawk.get("UserInfo");
        if(userInfo!=null&&!TextUtils.isEmpty(str)){
            String nowUserChannel= userInfo.getChannel();
            if (!TextUtils.isEmpty(str)) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(str);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obArray = jsonArray.getJSONObject(i);
                            String Channel = obArray.getString("channel");
                            if (Channel.equals(nowUserChannel)) { //最新版的审核模式
                                boolean audit_on = obArray.getBoolean("audit_on");
                                if (audit_on) {
                                    BaseConstans.setHasAdvertising(1);
                                } else {
                                    BaseConstans.setHasAdvertising(0);
                                }
                                boolean video_ad_open = obArray.getBoolean("video_ad_open");
                                LogUtil.d("OOM","当前需要激励视频"+video_ad_open);
                                BaseConstans.setIncentiveVideo(video_ad_open);

                                boolean save_video_ad = obArray.getBoolean("save_video_ad");
                                LogUtil.d("OOM","当前保存需要激励视频"+save_video_ad);
                                BaseConstans.setSave_video_ad(save_video_ad);





                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



    }


}
