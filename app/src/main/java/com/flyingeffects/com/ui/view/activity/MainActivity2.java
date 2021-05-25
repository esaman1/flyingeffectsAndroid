package com.flyingeffects.com.ui.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.databinding.ActivityMain2Binding;
import com.google.android.material.slider.Slider;
import com.imaginstudio.imagetools.pixellab.TextObject.StickerItemOnitemclick;
import com.imaginstudio.imagetools.pixellab.ZoomWidget;
import com.imaginstudio.imagetools.pixellab.imageinfo.displayInfo;
import com.imaginstudio.imagetools.pixellab.textContainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

public class MainActivity2 extends AppCompatActivity implements textContainer.OnSelectionChangedListener {

    private displayInfo helperClass;
    public FrameLayout workingArea;
    public ZoomWidget zoomWidget;
    public textContainer textContain;
    public LinearLayout content;
    private TextView tvSizeLable;
    private ActivityMain2Binding viewDataBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2);


        tvSizeLable = findViewById(R.id.textSizeTv);

        this.content = (LinearLayout) findViewById(R.id.content);
        this.workingArea = (FrameLayout) findViewById(R.id.workingArea);
        this.textContain = new textContainer(getApplicationContext());

        helperClass = new displayInfo(this.workingArea, this.zoomWidget);
        helperClass.setTextContain(this.textContain);

        this.workingArea.addView(this.textContain, new FrameLayout.LayoutParams(-1, -1));
        this.textContain.setSelectionListener(this);
        StickerItemOnitemclick stickerItemOnitemclick = new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                textContain.removeView(textContain.getCurrentText());
            }

            @Override
            public void stickerMove() {

            }
        };
        this.workingArea.post(new Runnable() {
            @Override
            public void run() {
                textContain.addNewText(Color.parseColor("#252B3B"), helperClass,
                        ContextCompat.getDrawable(MainActivity2.this, R.drawable.sticker_delete),
                        ContextCompat.getDrawable(MainActivity2.this, R.mipmap.sticker_redact), stickerItemOnitemclick);
            }
        });

        viewDataBinding.sizeSeek.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setTextSize(progress);
                viewDataBinding.textSizeTv.setText("字体大小:" + progress);
            }
        });


        viewDataBinding.innerRadius.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setInnerShadow(true, progress, viewDataBinding.innerDx.getValue(), viewDataBinding.innerDy.getValue(), Color.parseColor("#38D631"));
                viewDataBinding.innerRadiusLabel.setText("半径:" + progress);
            }
        });
        viewDataBinding.innerDx.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setInnerShadow(true, viewDataBinding.innerRadius.getValue(), progress, viewDataBinding.innerDy.getValue(), Color.parseColor("#38D631"));
                viewDataBinding.innerDxLabel.setText("x偏移:" + progress);

            }
        });
        viewDataBinding.innerDy.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setInnerShadow(true, viewDataBinding.innerRadius.getValue(), viewDataBinding.innerDx.getValue(), progress, Color.parseColor("#38D631"));
                viewDataBinding.innerDyLabel.setText("y偏移:" + progress);

            }
        });


        viewDataBinding.emossArc.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setEmboss(true, ((int) progress), ((int) viewDataBinding.emossQiangdu.getValue()), 40, 20, ((int) viewDataBinding.emossXieijiao.getValue()));
                viewDataBinding.arcLabel.setText("光照角度:" + progress);
            }
        });
        viewDataBinding.emossQiangdu.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setEmboss(true, ((int) viewDataBinding.emossArc.getValue()), ((int) progress), 40, 20, ((int) viewDataBinding.emossXieijiao.getValue()));
                viewDataBinding.qiangduLabel.setText("光照强度:" + progress);
            }
        });
        viewDataBinding.emossXieijiao.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().setEmboss(true, ((int) viewDataBinding.emossArc.getValue()), ((int) viewDataBinding.emossQiangdu.getValue()), 40, 20, ((int) progress));
                viewDataBinding.xiejiaoLable.setText("斜角:" + progress);
            }
        });


        viewDataBinding.dddShengdu.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                MainActivity2.this.textContain.getCurrentText().set3dEnabled(true);
                MainActivity2.this.textContain.getCurrentText().set3dDepth(((int) progress), 30, 1, true);
                viewDataBinding.dddShengduLabel.setText("3d深度:" + progress);
            }
        });
    }

    @Override
    public void objectTouch() {

    }

    @Override
    public void onObjectZChanged(String str, int i) {

    }

    @Override
    public void onShapeCreate(String str) {

    }

    @Override
    public void onShapeDelete(Bundle bundle, int i, String str) {

    }

    @Override
    public void onShapeMoveResize(float f, float f2, float f3, float f4, boolean z, String str) {

    }

    @Override
    public void onShapeSelectionChanged(boolean z, int i) {

    }

    @Override
    public void onTextCreate(String str) {

    }

    @Override
    public void onTextDelete(Bundle bundle, int i, String str) {

    }

    @Override
    public void onTextDoubleTap() {

    }

    @Override
    public void onTextMove(float f, float f2, float f3, boolean z, String str) {

    }

    @Override
    public void onTextSelectionChanged(boolean z) {

    }
}