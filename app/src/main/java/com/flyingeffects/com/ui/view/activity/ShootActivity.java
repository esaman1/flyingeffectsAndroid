package com.flyingeffects.com.ui.view.activity;


import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.utils.LogUtils;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.FUBaseActivity;
import com.flyingeffects.com.utils.FuLive.AudioObserver;
import com.flyingeffects.com.utils.FuLive.EffectEnum;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * description ：拍摄activity
 * creation date: 2021/1/25
 * user : zhangtongju
 */
public class ShootActivity  extends FUBaseActivity implements FURenderer.OnBundleLoadCompleteListener {

    public static final String EFFECT_TYPE = "effect_type";
    protected int mEffectType;
    private ArrayList<Effect> effects;



    @Override
    protected void onCreate() {


    }

    @Override
    protected FURenderer initFURenderer() {
        LogUtils.debug("OOM","initFURenderer");
        mEffectType = getIntent().getIntExtra(EFFECT_TYPE, Effect.EFFECT_TYPE_STICKER);
        if (mEffectType == Effect.EFFECT_TYPE_MUSIC_FILTER) {
            AudioObserver audioObserver = new AudioObserver(this);
            getLifecycle().addObserver(audioObserver);
        }

        effects = EffectEnum.getEffectsByEffectType(mEffectType);
        boolean isActionRecognition = mEffectType == Effect.EFFECT_TYPE_ACTION_RECOGNITION;
        boolean isPortraitSegment = mEffectType == Effect.EFFECT_TYPE_PORTRAIT_SEGMENT;
        boolean isGestureRecognition = mEffectType == Effect.EFFECT_TYPE_GESTURE_RECOGNITION;
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .inputImageOrientation(mFrontCameraOrientation)
                .setLoadAiHumanProcessor(isActionRecognition || isPortraitSegment)
                .maxHumans(1)
                .setNeedFaceBeauty(!(isActionRecognition))
                .setLoadAiHandProcessor(isGestureRecognition)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setOnBundleLoadCompleteListener(this)
                .build();
    }





    @OnClick({R.id.tv_login, R.id.iv_close, R.id.ll_weixin})
    public void onClick(View view) {
        switch (view.getId()) {
            //道具
            case R.id.iv_stage_property:
                Effect effect = effects.get(3);
                mFURenderer.onEffectSelected(effect);
                break;

            default:

                break;


        }
    }


















    //----------------------初始化3大回调 onBundleLoadComplete  --------------------------

    /**
     * description ：
     * creation date: 2021/1/26
     * param : 
     * user : zhangtongju
     */
    @Override
    public void onBundleLoadComplete(int what) {

    }

    
    
    
    /**
     * description ：
     * creation date: 2021/1/26
     * param : 
     * user : zhangtongju
     */
    @Override
    public int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        return 0;
    }

    
    
    /**
     * description ：
     * creation date: 2021/1/26
     * param : 
     * user : zhangtongju
     */
    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {

    }
}
