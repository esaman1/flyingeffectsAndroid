package com.shixing.sxve.ui.model;

import android.content.Context;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * description ：assetModle 把图片的展示图片的数据全部放在了TextUiModel或MediaUiModel中
 * date: ：2019/5/7 20:06
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class AssetModel {
    public static final int TYPE_MEDIA = 1;
    public static final int TYPE_TEXT = 2;
    public final Size size;
    public final int type;
    public final AssetUi ui;
    public int groupId;






    public AssetModel(String folder, JSONObject asset, AssetDelegate delegate, Context context) throws JSONException {

        JSONArray sizeArray = asset.getJSONArray("size");
        int width=sizeArray.getInt(0);
        int height=sizeArray.getInt(1);
        size = new Size(width%2==0?width:width-1, height%2==0?height:height-1);
        JSONObject ui = asset.getJSONObject("ui");
        groupId=ui.getInt("group");
        type = ui.getInt("type");
        if (type == TYPE_TEXT) {  //如果是图片就会生成mediaUiModel
            this.ui = new TextUiModel(folder, asset, delegate, size);
        } else {
            this.ui = new MediaUiModel(folder, ui,context, delegate, size,"","","");
        }
    }



    public AssetModel(String folder, JSONObject asset, AssetDelegate delegate, Context context,String startFrame,String endFrame,String slowLevel) throws JSONException {

        JSONArray sizeArray = asset.getJSONArray("size");
        int width=sizeArray.getInt(0);
        int height=sizeArray.getInt(1);
        size = new Size(width%2==0?width:width-1, height%2==0?height:height-1);
        JSONObject ui = asset.getJSONObject("ui");
        groupId=ui.getInt("group");
        type = ui.getInt("type");
        if (type == TYPE_TEXT) {  //如果是图片就会生成mediaUiModel
            this.ui = new TextUiModel(folder, asset, delegate, size);
        } else {
            this.ui = new MediaUiModel(folder, ui,context, delegate, size,startFrame,endFrame,slowLevel);
        }
    }








}
