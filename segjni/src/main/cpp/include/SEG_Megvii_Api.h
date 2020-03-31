/**
 * @file MG_Seg.h
 * @brief 抠像算法头文件
 *
 * 包含 抠像 的算法相关的接口，可以实现图片抠像，实时抠像
 */

#ifndef _SEG_MEGVII_API_H_
#define _SEG_MEGVII_API_H_

#include "MG_Common.h"

#define _OUT

#ifdef __cplusplus
extern "C" {
#endif


struct _MG_SEG_API;
/**
 * @brief 抠像算法句柄
 */
typedef struct _MG_SEG_API *MG_SEG_APIHANDLE;

/**
 * @brief 抠像类型设置
 *
 *  SDK支持的抠像类型
 */
typedef enum{
    MG_SEGMENT_REALTIME = 0,      ///< 前景实时抠像

	MG_SEGMENT_POST,             ///< 前景图片抠像

	MG_SEGMENT_POSTBODAY,        ///< 人体图片抠像 （暂不支持）

    MG_SEGMENTMODE_COUNT          ///< 支持图像总数
} MG_SEG_TYPE;

/**
 * @brief (DEPRECATED: 目前参数的效果都已经被删除)抠像参数配置类型
 *
 * 可以对抠像算法进行配置。
 */
typedef struct {
	MG_SEG_TYPE segtype;			 ///< 抠像类型 如MG_SEG_TYPE
    MG_UINT32 radius;                ///< radius 羽化半径 （取值范围1-20） (仅实时抠像有效)
    MG_UINT32 detect; 				 ///< detect 检测间隔 （取值范围1-20） （仅实时抠像有效）
    MG_BOOL process_flag; 		     ///< （DEPRECATEprocess_flag true是加后处理，false不带后处理 （默认是有后处理）
    MG_UINT32 rotate;			     ///< rotate 输入图片角度 （90 180 270 360 只支持90的整数倍）
} MG_SEG_APICONFIG;

/**
 * @brief 抠像算法函数集合
 *
 * 所有的算法函数都表示为该类型的一个变量，可以用形如：
 *   mg_seg.Function(...)
 * 的形式进行调用。
 */
typedef struct {

    /**
    * @brief 创建抠像算法句柄（handle）
    *
    * 传入算法模型数据，创建一个算法句柄。
    *
    * @param[in] model_data        算法模型的二进制数据
    * @param[in] model_length      算法模型的字节长度
    * @param[out] api_handle_ptr   算法句柄的指针，成功创建后会修改其值
    *
    * @return 成功则返回 MG_RETCODE_OK
    */

    MG_RETCODE (*CreateApiHandle)(
            const MG_BYTE *model_data,
			const MG_INT32 model_length,
            MG_SEG_APIHANDLE _OUT *api_handle_ptr);

    /**
     * @brief 释放抠像算法句柄（handle）
     *
     * 释放一个算法句柄。
     *
     * @param[in] api_handle 抠像算法句柄
     *
     * @return 成功则返回 MG_RETCODE_OK
     */
    MG_RETCODE (*ReleaseApiHandle)(
            MG_SEG_APIHANDLE *api_handle);

    /**
     * @brief 获取算法版本信息
     *
     * @return 返回一个字符串，表示算法版本号
     */
    const char* (*GetApiVersion)();


    /**
     * @brief （DEPRECATED：已经不需要这个操作，目前为空操作）创建图片数据缓存
     *
     *
     * @param[in] api_handle 算法句柄
     * @param[in] width  图片的高度
     * @param[in] height 图片的宽度
     * @param[in] image_mode 图像格式 (目前只支持nv21图像输入)
     *
     * @return 成功则返回 MG_RETCODE_OK
     */
    MG_RETCODE (*CreateImage) (
        MG_SEG_APIHANDLE *api_handle,
        MG_INT32 width,
        MG_INT32 height,
        MG_IMAGEMODE image_mode
        );


   /**
     * @brief （DEPRECATED：已经不需要这个操作，目前为空操作）释放数据缓存
     *
     * @param[in] api_handle 算法句柄
     *
     * @return 成功则返回 MG_RETCODE_OK
     */
    MG_RETCODE (*RealseImage) (MG_SEG_APIHANDLE *api_handle);


    /**
     * @brief 抠像算法
     *
     *
     * @param[in] api_handle 算法句柄
     * @param[in] image_data 图像数据指针，
     *                      （如：一张YUV的图，其数据大小应该为width*height*3）
     * @param[in] src_width  输入图片的高度
     * @param[in] src_height 输入图片的宽度 
     * @param[in] image_mode 图像格式
     * @param[in] config （DEPRECATED: 虽然仍然要传递，但并不起作用）抠像配置 如MG_SEG_APICONFIG 所示
     * @param[out] out_image_data 抠像mask，单通道，取值范围为0-255，数字越大表示越不透明，或者说人体接近255
     *                            而背景接近0
     *        
     * @param[in] out_width  (DEPRECATED, 必须为0或者src_width) 输出图片的高度
     * @param[in] out_height (DEPRECATED, 必须为0或者src_height) 输出图片的宽度
     * @param[out] image_mode 图像格式（固定为gray）
     *
     * @return 成功则返回 MG_RETCODE_OK
     */
    MG_RETCODE (*SegmentImage) (
        MG_SEG_APIHANDLE *api_handle,
        const MG_BYTE *src_image_data,
        MG_INT32 src_width,
        MG_INT32 src_height,
        MG_IMAGEMODE src_mode,
		MG_SEG_APICONFIG config,
        MG_BYTE **out_image_data,
        MG_INT32 out_width,
        MG_INT32 out_height,
        MG_IMAGEMODE *out_mode
        );

}MG_SEG_API_FUNCTIONS_TYPE;

/**
 * @brief 抠像算法域
 *
 * Example:
 *      mg_seg.CreateApiHandle(...
 */
extern MG_EXPORT MG_SEG_API_FUNCTIONS_TYPE mg_seg;

#ifdef __cplusplus
}
#endif

#undef _OUT
#endif // _MG_SEG_H_
