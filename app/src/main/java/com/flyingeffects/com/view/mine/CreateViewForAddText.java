package com.flyingeffects.com.view.mine;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreateTemplateTextEffectAdapter;
import com.flyingeffects.com.adapter.CreateTemplateTextFontAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.FontEnity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


/**
 * description ：创作页面文字选择框
 * creation date: 2020/9/16
 * user : zhangtongju
 */
public class CreateViewForAddText {
    private ViewPager viewPager;
    private Context context;
    private ArrayList<FontEnity> listFont = new ArrayList<>();
    private ArrayList<FontEnity> listEffect = new ArrayList<>();
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreateTemplateTextEffectAdapter createTemplateTextEffectAdapterEffect;
    private CreateTemplateTextFontAdapter createTemplateTextEffectAdapterFont;
    private String mTTFFolder;
    private downCallback callback;
    private EditText edit_text;
    private LinearLayout view;

    public CreateViewForAddText(Context context,LinearLayout view, downCallback callback) {
        this.context = context;
        this.view=view;
        this.callback = callback;
        FileManager fileManager = new FileManager();
        mTTFFolder = fileManager.getFileCachePath(context, "fontStyle");
    }


//    private BottomSheetDialog bottomSheetDialog;

    private ArrayList<TextView> listTitle = new ArrayList<>();

    public void showBottomSheetDialog() {
//        if (bottomSheetDialog == null) {
//            bottomSheetDialog = new BottomSheetDialog(context, R.style.gaussianDialog);
//            View view = LayoutInflater.from(context).inflate(R.layout.view_add_text, null);
            ImageView iv_down = view.findViewById(R.id.iv_down);
            edit_text = view.findViewById(R.id.edit_text);
            iv_down.setOnClickListener(view12 -> dismissDialog());
            TextView tv_hot = view.findViewById(R.id.tv_hot);
            TextView tv_complete = view.findViewById(R.id.tv_complete);
            tv_complete.setOnClickListener(listener);
            tv_hot.setOnClickListener(listener);
            TextView tv_font = view.findViewById(R.id.tv_font);
            tv_font.setOnClickListener(listener);
            viewPager = view.findViewById(R.id.viewpager);
            ArrayList<View> list = new ArrayList<>();
            View gridViewLayout = LayoutInflater.from(context).inflate(R.layout.view_creat_template_effect_type, viewPager, false);
            GridView gridView = gridViewLayout.findViewById(R.id.gridView);
            gridView.setOnItemClickListener((adapterView, view13, i, l) -> downFile(listEffect.get(i).getImage(), 0,listEffect.get(i).getType(),listEffect.get(i).getColor()));
            createTemplateTextEffectAdapterEffect = new CreateTemplateTextEffectAdapter(listEffect, context);
            gridView.setAdapter(createTemplateTextEffectAdapterEffect);
            list.add(gridViewLayout);
            View gridViewLayoutFont = LayoutInflater.from(context).inflate(R.layout.view_creat_template_text_type, viewPager, false);
            GridView gridViewFont = gridViewLayoutFont.findViewById(R.id.gridView);
            gridViewFont.setOnItemClickListener((adapterView, view1, i, l) -> {
                downFile(listFont.get(i).getFile(), 1,1,"");
            });
            createTemplateTextEffectAdapterFont = new CreateTemplateTextFontAdapter(listFont, context);
            gridViewFont.setAdapter(createTemplateTextEffectAdapterFont);
            list.add(gridViewLayoutFont);
            TemplateViewPager adapter = new TemplateViewPager(list);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    setTextColor(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
//            bottomSheetDialog.setContentView(view);
//            bottomSheetDialog.setCancelable(true);
//            bottomSheetDialog.setCanceledOnTouchOutside(true);
//            bottomSheetDialog.setOnDismissListener(dialog -> {
//            });
//            View parent = (View) view.getParent();     //处理高度显示完全  https://www.jianshu.com/p/38af0cf77352
//            parent.setBackgroundResource(android.R.color.transparent);
//            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
//            view.measure(0, 0);
//            behavior.setPeekHeight(view.getMeasuredHeight());
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
//            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//            parent.setLayoutParams(params);
//            bottomSheetDialog.show();
            listTitle.add(tv_hot);
            listTitle.add(tv_font);
            requestFontImage();
            requestFontList();
//        }
    }


    /**
     * description ：
     * creation date: 2020/9/22
     * param : type 0 热门效果或者 1字体   textType ：热门效果 2表示文字，1 表示图片
     * user : zhangtongju
     */
    private void downFile(String path, int type,int textType,String color) {

        if(type==0&&textType==2){
            String[]str=color.split(",");
            callback.setTextColor("#"+str[1],"#"+str[0]);
        }else{
            LogUtil.d("OOM4", "downFilePath=" + path);
            int index = path.lastIndexOf("/");
            String newStr = path.substring(index);
            LogUtil.d("OOM2", "newStr=" + newStr);
            String name = mTTFFolder + newStr;
            File file = new File(name);
            LogUtil.d("OOM2", "name=" + name);
            if (file.exists()) {
                LogUtil.d("OOM2", "已下载");
                if (callback != null) {
                    callback.isSuccess(name, type);
                }
            } else {
                WaitingDialog.openPragressDialog(context);
                Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
                    DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> {
                        callback.isSuccess(name, type);
                        WaitingDialog.closePragressDialog();
                    });
                    manage.DownloadVideo(path, name);
                });
            }
        }
    }


    /**
     * description ：下载回调  ，path 下载后的地址
     * type  0表示热门效果，1 表示来自字体
     * creation date: 2020/9/17
     * user : zhangtongju
     */
    public interface downCallback {

        void isSuccess(String path, int type);

        void setText(String text);

        void setTextColor(String color0,String color1);

    }


    private void dismissDialog() {

        if(view!=null){
            view.setVisibility(View.GONE);
        }

//        try {
//            if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
//                bottomSheetDialog.dismiss();
//                bottomSheetDialog = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    View.OnClickListener listener = view -> {
        switch (view.getId()) {
            case R.id.tv_hot:
                viewPager.setCurrentItem(0);
                setTextColor(0);

                break;
            case R.id.tv_font:
                viewPager.setCurrentItem(1);
                setTextColor(1);

                break;
            case R.id.tv_complete:
                String text = edit_text.getText().toString();
                if (callback != null) {
                    callback.setText(text);
                }
                dismissDialog();
                break;

            default:
                break;
        }
    };

    private void setTextColor(int chooseItem) {
        for (int i = 0; i < 2; i++) {
            listTitle.get(i).setTextColor(context.getResources().getColor(R.color.white));
        }
        listTitle.get(chooseItem).setTextColor(Color.parseColor("#5496FF"));
    }


    /**
     * description ：请求文字
     * creation date: 2020/9/17
     * user : zhangtongju
     */
    private void requestFontList() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().fontList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FontEnity>>(context) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(List<FontEnity> data) {
                listFont.clear();
                listFont.addAll(data);
                createTemplateTextEffectAdapterFont.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }


    /**
     * description ：请求图片
     * creation date: 2020/9/17
     * user : zhangtongju
     */
    private void requestFontImage() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().fontImage(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FontEnity>>(context) {
            @Override
            protected void _onError(String message) {

            }

            @Override
            protected void _onNext(List<FontEnity> data) {
                listEffect.clear();
                listEffect.addAll(data);
                createTemplateTextEffectAdapterEffect.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

}
