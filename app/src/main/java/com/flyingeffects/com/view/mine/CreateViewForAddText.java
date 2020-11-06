package com.flyingeffects.com.view.mine;

import android.app.Activity;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreateTemplateTextEffectAdapter;
import com.flyingeffects.com.adapter.CreateTemplateTextFontAdapter;
import com.flyingeffects.com.adapter.CreateTemplateTextFrameAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.FontEnity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.DownloadVideoManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.keyboard.KeyboardHeightProvider;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * description ：创作页面文字选择框
 * creation date: 2020/9/16
 * user : zhangtongju
 */
public class CreateViewForAddText {
    private ViewPager viewPager;
    private Activity context;
    private ArrayList<FontEnity> listFont = new ArrayList<>();
    private ArrayList<FontEnity> listEffect = new ArrayList<>();
    private ArrayList<FontEnity> listFrame = new ArrayList<>();
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreateTemplateTextEffectAdapter createTemplateTextEffectAdapterEffect;
    private CreateTemplateTextFontAdapter createTemplateTextEffectAdapterFont;
    private CreateTemplateTextFrameAdapter createTemplateTextEffectAdapterFrame;
    private String mTTFFolder;
    private downCallback callback;
    public ImageView iv_down;
    private LinearLayout view;
    private LinearLayout llAddText;
    private View view_line_text;
    private EditText editText;
    private LinearLayout ll_add_child_text;
    private String inputText;
    private int lastSelect = 1;

    private static ArrayList<TextView> listTv = new ArrayList<>();
    private static ArrayList<View> listView = new ArrayList<>();


    public CreateViewForAddText(Activity context, LinearLayout view, downCallback callback) {
        this.context = context;
        this.view = view;
        this.callback = callback;
        FileManager fileManager = new FileManager();
        mTTFFolder = fileManager.getFileCachePath(context, "fontStyle");
    }

    public void hideInput() {
        view.setVisibility(View.GONE);
    }

    private List<String> listTitle = new ArrayList<>();

    public void hideInputTextDialog() {
        hideInput();
        hideKeyboard();
    }


    public void setInputText(String str) {
        if (editText != null) {
            editText.setText(str);
        }
    }

    public void showBottomSheetDialog(String text, String type) {
        inputText = text;
        llAddText = view.findViewById(R.id.ll_add_text);
        iv_down = view.findViewById(R.id.iv_down);
        view_line_text = view.findViewById(R.id.view_line_text);
        editText = view.findViewById(R.id.edit_text);
        ll_add_child_text = view.findViewById(R.id.ll_add_child_text);
        listView.clear();
        listTv.clear();
        listTitle.clear();
        ll_add_child_text.removeAllViews();
        listTitle.add("键盘");
        listTitle.add("热门效果");
        listTitle.add("边框");
        listTitle.add("字体");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                inputText = editText.getText().toString().trim();
                if (callback != null) {
                    if (!TextUtils.isEmpty(inputText)) {
                        callback.setText(inputText);
                    } else {
                        callback.setText("输入文本");
                    }
                }
            }
        });

        if (TextUtils.isEmpty(inputText)||inputText.equals("输入文本")) {
            editText.setText("");
            editText.setHint("输入文本");
        }
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWitchBtn(0);
            }
        });
        iv_down.setOnClickListener(view12 -> {
            hideInput();
            hideKeyboard();
            if (callback != null) {
                callback.setText(inputText);
                callback.hindAddTextStickerView();
            }
        });

        viewPager = view.findViewById(R.id.viewpager);
        //键盘
        ArrayList<View> list = new ArrayList<>();
        View keyboardView = LayoutInflater.from(context).inflate(R.layout.fragment_input, viewPager, false);
        list.add(keyboardView);
        //特效
        View gridViewLayout = LayoutInflater.from(context).inflate(R.layout.view_creat_template_effect_type, viewPager, false);
        GridView gridView = gridViewLayout.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view13, i, l) -> {
            CreateViewForAddText.this.downFile(listEffect.get(i).getImage(), 0, listEffect.get(i).getType(), listEffect.get(i).getColor(), listEffect.get(i).getTitle());
            createTemplateTextEffectAdapterEffect.select(i);
            if ("bj_template".equals(type)) {
                statisticsEventAffair.getInstance().setFlag(context, "20_bj_text_style", listEffect.get(i).getTitle());
            } else if ("OneKey_template".equals(type)) {
                statisticsEventAffair.getInstance().setFlag(context, "20_mb_text_style", listEffect.get(i).getTitle());
            }

        });
        createTemplateTextEffectAdapterEffect = new CreateTemplateTextEffectAdapter(listEffect, context);
        gridView.setAdapter(createTemplateTextEffectAdapterEffect);
        createTemplateTextEffectAdapterEffect.select(0);
        list.add(gridViewLayout);
        //边框
        View gridViewLayoutFrame = LayoutInflater.from(context).inflate(R.layout.view_creat_template_text_type, viewPager, false);
        GridView gridViewFrame = gridViewLayoutFrame.findViewById(R.id.gridView);
        createTemplateTextEffectAdapterFrame = new CreateTemplateTextFrameAdapter(listFrame, context);
        gridViewFrame.setAdapter(createTemplateTextEffectAdapterFrame);
        createTemplateTextEffectAdapterFrame.select(0);
        gridViewFrame.setOnItemClickListener((adapterView, view1, i, l) -> {
            createTemplateTextEffectAdapterFrame.select(i);
            if ("bj_template".equals(type)) {
                statisticsEventAffair.getInstance().setFlag(context, "20_bj_text_border", listFrame.get(i).getTitle());
            }else{
                statisticsEventAffair.getInstance().setFlag(context, "20_mb_text_border", listFrame.get(i).getTitle());
            }
            WaitingDialog.openPragressDialog(context);
            downFileFrame(listFrame.get(i).getImage(), 0, listFrame.get(i).getType(), listFrame.get(i).getColor(), listFrame.get(i).getTitle(), new downFameCallback() {
                @Override
                public void isSuccess(String path1, int type) {

                    downFileFrame(listFrame.get(i).getBorder_image(), 1, listFrame.get(i).getType(), listFrame.get(i).getColor(), listFrame.get(i).getTitle(), new downFameCallback() {
                        @Override
                        public void isSuccess(String path2, int type2) {
                            WaitingDialog.closePragressDialog();
                            if (callback != null) {
                                if (type == 1) {
                                    callback.isSuccess(path1, path2,listFrame.get(i).getTitle());
                                } else {
                                    String[] str = listFrame.get(i).getColor().split(",");
                                    callback.isSuccess("#" + str[1], "#" + str[0], path2,listFrame.get(i).getTitle());

                                }
                            }
                        }
                    });
                }
            });
            if ("bj_template".equals(type)) {
                statisticsEventAffair.getInstance().setFlag(context, "20_bj_text_font", listFont.get(i).getTitle());
            } else if ("OneKey_template".equals(type)) {
                statisticsEventAffair.getInstance().setFlag(context, "20_mb_text_font", listFont.get(i).getTitle());
            }

        });
        list.add(gridViewLayoutFrame);
        //字体
        View gridViewLayoutFont = LayoutInflater.from(context).inflate(R.layout.view_creat_template_text_type, viewPager, false);
        GridView gridViewFont = gridViewLayoutFont.findViewById(R.id.gridView);
        gridViewFont.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
                CreateViewForAddText.this.downFile(listFont.get(i).getFile(), 1, 1, "", listFont.get(i).getTitle());
                createTemplateTextEffectAdapterFont.select(i);
                if ("bj_template".equals(type)) {
                    statisticsEventAffair.getInstance().setFlag(context, "20_bj_text_font", listFont.get(i).getTitle());
                } else if ("OneKey_template".equals(type)) {
                    statisticsEventAffair.getInstance().setFlag(context, "20_mb_text_font", listFont.get(i).getTitle());
                }
            }
        });
        createTemplateTextEffectAdapterFont = new CreateTemplateTextFontAdapter(listFont, context);
        gridViewFont.setAdapter(createTemplateTextEffectAdapterFont);
        createTemplateTextEffectAdapterFont.select(0);
        list.add(gridViewLayoutFont);

        TemplateViewPager adapter = new TemplateViewPager(list);
        viewPager.setAdapter(adapter);

        for (int i = 0; i < listTitle.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_keyboard_text, null);
            TextView tv = view.findViewById(R.id.tv_name_bj_head);
            View view_line = view.findViewById(R.id.view_line_head);
            tv.setText(listTitle.get(i));
            tv.setId(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d("OOM3", "onClick=" + v.getId());
                    showWitchBtn(v.getId());
                }
            });
            listTv.add(tv);
            listView.add(view_line);
            ll_add_child_text.addView(view);
        }
        requestFontImage();
        requestFontList();
        requestFrameList();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (lastViewPagerChoosePosition != position) {
                    selectedPage(position);
                    lastViewPagerChoosePosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        new KeyboardHeightProvider(context).init().setHeightListener(new KeyboardHeightProvider.HeightListener() {
            @Override
            public void onHeightChanged(int height) {
                if (height > 0) {
                    keyboardHeight = height;
                    LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) llAddText.getLayoutParams();
                    layoutParams1.setMargins(0, 0, 0, keyboardHeight);
                    llAddText.setLayoutParams(layoutParams1);
                    view_line_text.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    isKeyboardOpen = true;
                } else {
                    if (isKeyboardOpen) {
                        LogUtil.d("OOM3", "isKeyboardOpen=" + isKeyboardOpen);
                        if (lastSelect == 0) {
                            showWitchBtn(1);
                        }
                        isKeyboardOpen = false;
                    }
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listTitle.size() > 0) {
                    showWitchBtn(0);
                }
            }
        }, 150);
    }

    /**
     * 键盘是否打开
     */
    boolean isKeyboardOpen = false;

    int keyboardHeight;

    private void selectedTab(int position) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) llAddText.getLayoutParams();
        if (position == 0) {
            showInput();
            if (keyboardHeight != 0) {
                layoutParams1.setMargins(0, 0, 0, keyboardHeight);
                llAddText.setLayoutParams(layoutParams1);
            }
            view_line_text.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        } else {
            hideKeyboard();
            editText.setFocusable(false);
            view_line_text.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            if (keyboardHeight != 0) {
                layoutParams1.setMargins(0, 0, 0, 0);
                llAddText.setLayoutParams(layoutParams1);
                layoutParams.height = keyboardHeight;
                viewPager.setLayoutParams(layoutParams);
            }
        }
    }

    private void showWitchBtn(int showWitch) {
        for (int i = 0; i < listTv.size(); i++) {
            TextView tv = listTv.get(i);
            View view = listView.get(i);
            if (i == showWitch) {
                tv.setTextSize(17);
                view.setVisibility(View.VISIBLE);
            } else {
                tv.setTextSize(14);
                view.setVisibility(View.INVISIBLE);
            }
        }
        lastSelect = showWitch;
        LogUtil.d("OOM3", "showWitch=" + showWitch);
        viewPager.setCurrentItem(showWitch);
        selectedTab(showWitch);
    }

    private int lastViewPagerChoosePosition;

    private void selectedPage(int i) {
        if (lastViewPagerChoosePosition != i) {
            if (i <= listTitle.size() - 1) {
                showWitchBtn(i);
            }
        }
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        View v = context.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     */
    private void showInput() {
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * description ：
     * creation date: 2020/9/22
     * param : type 0 热门效果或者 1字体   textType ：热门效果 2表示文字，1 表示图片 3 表示边框
     * user : zhangtongju
     */
    private void downFile(String path, int type, int textType, String color, String title) {
        if (type == 0 && textType == 2) {
            String[] str = color.split(",");
            callback.setTextColor("#" + str[1], "#" + str[0], title);
        } else {
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
                    LogUtil.d("OOM5", "name=" + name);
                    callback.isSuccess(name, type, title);
                }
            } else {
                WaitingDialog.openPragressDialog(context);
                Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
                    DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> {
                        callback.isSuccess(name, type, title);
                        WaitingDialog.closePragressDialog();
                    });
                    manage.DownloadVideo(path, name);
                });
            }
        }
    }


    /**
     * description ：
     * creation date: 2020/9/22
     * param : type 0 热门效果或者 1字体   textType ：热门效果 2表示文字，1 表示图片 3 表示边框
     * user : zhangtongju
     */
    private void downFileFrame(String path, int type, int textType, String color, String title, downFameCallback callback) {


        if (type == 0 && textType == 2) {
//            String[] str = color.split(",");
//            callback.setTextColor("#" + str[1], "#" + str[0], title);
            callback.isSuccess(title, 2);
        } else {
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
                    callback.isSuccess(name, 1);
                }
            } else {
                Observable.just(path).subscribeOn(Schedulers.io()).subscribe(s -> {
                    DownloadVideoManage manage = new DownloadVideoManage(isSuccess -> {
                        callback.isSuccess(name, 1);
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

        void isSuccess(String path, int type, String title);

        void setText(String text);

        void setTextColor(String color0, String color1, String title);

        void isSuccess(String textBjPath, String textFramePath,String frameTitle);

        void isSuccess(String color0, String color1, String textFramePath,String frameTitle);

        default void hindAddTextStickerView(){};
    }


    /**
     * description ：0 是图片 1 是色值
     * creation date: 2020/10/26
     * user : zhangtongju
     */
    public interface downFameCallback {

        void isSuccess(String path, int type);


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
     * description ：请求边框
     * creation date: 2020/9/17
     * user : zhangtongju
     */
    private void requestFrameList() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().fontBorder(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FontEnity>>(context) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(List<FontEnity> data) {
                listFrame.clear();
                listFrame.addAll(data);
                createTemplateTextEffectAdapterFrame.notifyDataSetChanged();
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
