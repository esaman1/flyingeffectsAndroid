#include <android/log.h>
#include <jni.h>

#include <vector>
#include <algorithm>
#include <string>
#include <chrono>
#include <cmath>
#include <android/bitmap.h>
#include <time.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <include/SEG_Megvii_Api.h>

#include <stdlib.h>


//#define MGDEBUG 1
//#if  MGDEBUG == 0
//#define LOGE(...)
//#define  LOGV(...)
//#elif MGDEBUG == 1
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,"segjni",__VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,"mgf-c",__VA_ARGS__)
//#endif
static float cost_timej(timeval e, timeval s) {
    return (1000 * (e.tv_sec-s.tv_sec)+(e.tv_usec-s.tv_usec));
}
extern "C"
{

MG_SEG_APIHANDLE *seg_handler= nullptr;
// create handle
JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeCreateSegHandler(JNIEnv *env, jclass type, jobject context, jbyteArray segmentModel_, jint thread_count) {
    jbyte *segmentModel = env->GetByteArrayElements(segmentModel_, NULL);
    int retCode;
    int model_len = env->GetArrayLength(segmentModel_);
    LOGE("model_len = %d \n",model_len);

    if(seg_handler!= nullptr){
        mg_seg.ReleaseApiHandle(seg_handler);
        seg_handler= nullptr;
    }

    seg_handler = (MG_SEG_APIHANDLE *)malloc(sizeof(MG_SEG_APIHANDLE));
    retCode= mg_seg.CreateApiHandle((const MG_BYTE *)segmentModel,(const MG_INT32)model_len,thread_count,seg_handler);
    if(retCode != 0)
    {
        LOGE("CreateApiHandle JNI is failed\n");
    }

    env->ReleaseByteArrayElements(segmentModel_, segmentModel, 0);
    return retCode;
}

// seg for image: binary image are output_
JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeSegImage(JNIEnv *env, jclass type, jbyteArray data_, jint width, jint height, jbyteArray output_,jboolean segment_flag) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    jbyte *output = env->GetByteArrayElements(output_, NULL);

    if (seg_handler != nullptr ) {
        int out_w = 0;
        int out_h = 0;
        MG_IMAGEMODE out_mode;
        MG_BYTE*out = NULL;
        MG_SEG_APICONFIG config;
        config.rotate = 0;
        config.segtype = MG_SEGMENT_POST;
        config.process_flag = true;

        int outWidth;
        int outHeight;
        int ret = mg_seg.SegmentImage(seg_handler,(const MG_BYTE*)data,width,height,MG_IMAGEMODE_RGBA,config,&out,&outWidth,&outHeight,&out_mode,false);
        if(ret != 0 || out == NULL)
        {
            LOGE("SegmentImage is failed \n");
        }

        if (ret != 0){
            env->ReleaseByteArrayElements(data_, data, 0);
            env->ReleaseByteArrayElements(output_, output, 0);
            return -1;
        }

        out_w = outWidth;
        out_h = outHeight;
        memcpy(output,out,out_w*out_h);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    env->ReleaseByteArrayElements(output_, output, 0);
    return 0;
}

// seg for camera: binary image are output width segSize_
JNIEXPORT jbyteArray JNICALL

Java_com_megvii_segjni_SegJni_nativeSegCamera(JNIEnv *env, jclass type, jbyteArray data_, jint width, jint height, jint rotate,jint radius,jint fps,jintArray segSize_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    jint length = env->GetArrayLength(data_);
    jint *segSize = env->GetIntArrayElements(segSize_, NULL);
    jbyteArray jfaSegResult = nullptr ;
    if (seg_handler != nullptr ) {
        MG_IMAGEMODE out_mode;
        MG_BYTE*out = NULL;
        MG_SEG_APICONFIG config;
        config.rotate = rotate;
        config.segtype = MG_SEGMENT_REALTIME;
        config.detect = fps;
        config.radius = radius;
        int outWidth;
        int outHeight;
        int ret = mg_seg.SegmentImage(seg_handler,(const MG_BYTE*)data,width,height,MG_IMAGEMODE_NV21,config,&out,&outWidth,&outHeight,&out_mode,true);
        LOGE("SegmentImage result = %d",ret);
        if(ret != 0)
        {
            env->ReleaseByteArrayElements(data_, data, 0);
            env->ReleaseIntArrayElements(segSize_, segSize, 0);
            return nullptr;
        }
        segSize[0] = outWidth;
        segSize[1] = outHeight;

        // set to given pointer
        jfaSegResult = env->NewByteArray(segSize[0]*segSize[1]);
        env->SetByteArrayRegion(jfaSegResult, 0, segSize[0]*segSize[1], (jbyte*)out);
    }
    env->ReleaseByteArrayElements(data_, data, 0);
    env->ReleaseIntArrayElements(segSize_, segSize, 0);

    return jfaSegResult;
}

// release handle
JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeReleaseSegHandler(JNIEnv *env, jclass type) {

    mg_seg.ReleaseApiHandle(seg_handler);
    free(seg_handler);
    seg_handler= nullptr;
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeBlendImage(JNIEnv *env, jclass type, jobject jObjSrcImg, jbyteArray jObjAlpha) {
    AndroidBitmapInfo  info;
    memset(&info, 0, sizeof(AndroidBitmapInfo));
    AndroidBitmap_getInfo(env, jObjSrcImg, &info);
    if(info.width > 0 && info.height > 0){
        uint8_t *lpSrcdata = nullptr;
        AndroidBitmap_lockPixels(env, jObjSrcImg, (void**)&lpSrcdata);
        if(lpSrcdata != nullptr){
            uint8_t *lpSegMask = (uint8_t*)env->GetByteArrayElements(jObjAlpha, nullptr);
            if(lpSegMask != nullptr){
                uint8_t *lpAlpha = lpSegMask;
                for(int i = 0; i < info.height; i++){
                    uint8_t *lpTmpDst = lpSrcdata;
                    for(int j = 0; j < info.width; j++){

                        {
                            {
                                *lpTmpDst = (uint8_t) (255.0f * (255.0f - *lpAlpha) / 255.0f +
                                                       (*lpTmpDst * *lpAlpha) / 255.0f);

                                lpTmpDst++;
                                *lpTmpDst = (uint8_t) (255.0f * (255.0f - *lpAlpha) / 255.0f +
                                                       (*lpTmpDst * *lpAlpha) / 255.0f);
                                lpTmpDst++;
                                *lpTmpDst = (uint8_t) (255.0f * (255.0f - *lpAlpha) / 255.0f +
                                                       (*lpTmpDst * *lpAlpha) / 255.0f);
                                lpTmpDst++;
                                //   LOGE("alpha %d",*lpAlpha);
                                if (*lpAlpha <50) {
                                    *lpTmpDst++ = 0;
                                } else {
                                    *lpTmpDst++ = 255;
                                }

                            }
                        }


                        lpAlpha++;
                    }
                    lpSrcdata += info.stride;
                }
                env->ReleaseByteArrayElements(jObjAlpha, (jbyte*)lpSegMask, 0);
            }
            AndroidBitmap_unlockPixels(env, jObjSrcImg);
        }
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeCreateImageBuffer(JNIEnv *env, jclass type,int width, int height) {

    mg_seg.CreateImage(seg_handler,width,height,MG_IMAGEMODE_RGBA);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_megvii_segjni_SegJni_nativeReleaseImageBuffer(JNIEnv *env, jclass type) {
    mg_seg.RealseImage(seg_handler);
    return 0;
}

JNIEXPORT jstring JNICALL
Java_com_megvii_segjni_SegJni_nativeGetversion(JNIEnv *env, jclass type) {
    const char *version = mg_seg.GetApiVersion();
    return env->NewStringUTF(version);
}

}