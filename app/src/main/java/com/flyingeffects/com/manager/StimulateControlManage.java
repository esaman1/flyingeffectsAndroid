package com.flyingeffects.com.manager;

import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.utils.ChannelUtil;
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


    private int isVideoadvertisingId;
    private boolean nowAppIsInReview = false;

    public void InitRefreshStimulate() {
        String appChannel = ChannelUtil.getChannel(BaseApplication.getInstance());
        String str = Hawk.get("AuditModeConfig");
        UserInfo userInfo = Hawk.get("UserInfo");
        if (userInfo != null && !TextUtils.isEmpty(str)) {
            String nowUserChannel = userInfo.getChannel();
            LogUtil.d("OOM2", "nowUserChannel=" + nowUserChannel);
            LogUtil.d("OOM2", "AuditModeConfig=" + str);
            if (!TextUtils.isEmpty(str)) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(str);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obArray = jsonArray.getJSONObject(i);
                            String Channel = obArray.getString("channel");
                            if ("isVideoadvertising".equals(Channel)) { //控制了版本号
                                isVideoadvertisingId = obArray.getInt("id");
                            }
                            if (!appChannel.equals(nowUserChannel) && appChannel.equals(Channel)) {
                                int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                                boolean audit_on = obArray.getBoolean("audit_on");
                                if (audit_on || isVideoadvertisingId != NowVersion) {
                                    nowAppIsInReview = false;
                                } else {
                                    nowAppIsInReview = true;
                                }
                            }


                            if (Channel.equals(nowUserChannel)) { //最新版的审核模式
                                boolean audit_on = obArray.getBoolean("audit_on");
                                int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                                if (audit_on || isVideoadvertisingId != NowVersion) {
                                    BaseConstans.setHasAdvertising(1);
                                } else {
                                    BaseConstans.setHasAdvertising(0);
                                }
                                boolean video_ad_open = obArray.getBoolean("video_ad_open");
                                LogUtil.d("OOM2", "当前需要激励视频" + video_ad_open);
                                BaseConstans.setIncentiveVideo(video_ad_open);

                                boolean save_video_ad = obArray.getBoolean("save_video_ad");
                                LogUtil.d("OOM2", "当前保存需要激励视频" + save_video_ad);
                                BaseConstans.setSave_video_ad(save_video_ad);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (nowAppIsInReview) {
            //当前版本的审核状态
            BaseConstans.setHasAdvertising(0);
        }
    }
}
