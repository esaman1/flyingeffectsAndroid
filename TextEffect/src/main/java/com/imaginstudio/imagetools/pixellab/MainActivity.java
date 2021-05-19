//package com.imaginstudio.imagetools.pixellab;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatSeekBar;
//import androidx.databinding.DataBindingUtil;
//import androidx.databinding.ViewDataBinding;
//
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.google.android.material.slider.Slider;
//import com.imaginstudio.imagetools.R;
//import com.imaginstudio.imagetools.databinding.ActivityMainBinding;
//import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
//import com.imaginstudio.imagetools.pixellab.imageinfo.displayInfo;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class MainActivity extends AppCompatActivity implements textContainer.OnSelectionChangedListener {
//
//    public static displayInfo helperClass;
//    public FrameLayout workingArea;
//    public ZoomWidget zoomWidget;
//    public textContainer textContain;
//    public LinearLayout content;
//    private TextView tvSizeLable;
//    private ActivityMainBinding viewDataBinding;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//
//
//        tvSizeLable = findViewById(R.id.textSizeTv);
//
//        this.content = (LinearLayout) findViewById(R.id.content);
//        this.workingArea = (FrameLayout) findViewById(R.id.workingArea);
//        this.textContain = new textContainer(getApplicationContext());
//
//        helperClass = new displayInfo(this.workingArea, this.zoomWidget);
//        helperClass.setTextContain(this.textContain);
//
//        this.workingArea.addView(this.textContain, new FrameLayout.LayoutParams(-1, -1));
//        this.textContain.setSelectionListener(this);
//        this.textContain.addNewText(Color.parseColor("#252B3B"));
//
//        viewDataBinding.sizeSeek.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setTextSize(progress);
//                viewDataBinding.textSizeTv.setText("字体大小:" + progress);
//            }
//        });
//
//
//        viewDataBinding.innerRadius.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setInnerShadow(true, progress, viewDataBinding.innerDx.getValue(), viewDataBinding.innerDy.getValue(), Color.parseColor("#38D631"));
//                viewDataBinding.innerRadiusLabel.setText("半径:" + progress);
//            }
//        });
//        viewDataBinding.innerDx.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setInnerShadow(true, viewDataBinding.innerRadius.getValue(), progress, viewDataBinding.innerDy.getValue(), Color.parseColor("#38D631"));
//                viewDataBinding.innerDxLabel.setText("x偏移:" + progress);
//
//            }
//        });
//        viewDataBinding.innerDy.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setInnerShadow(true, viewDataBinding.innerRadius.getValue(), viewDataBinding.innerDx.getValue(), progress, Color.parseColor("#38D631"));
//                viewDataBinding.innerDyLabel.setText("y偏移:" + progress);
//
//            }
//        });
//
//
//        viewDataBinding.emossArc.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setEmboss(true, ((int) progress), ((int) viewDataBinding.emossQiangdu.getValue()), 40, 20, ((int) viewDataBinding.emossXieijiao.getValue()));
//                viewDataBinding.arcLabel.setText("光照角度:" + progress);
//            }
//        });
//        viewDataBinding.emossQiangdu.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setEmboss(true, ((int) viewDataBinding.emossArc.getValue()), ((int) progress), 40, 20, ((int) viewDataBinding.emossXieijiao.getValue()));
//                viewDataBinding.qiangduLabel.setText("光照强度:" + progress);
//            }
//        });
//        viewDataBinding.emossXieijiao.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().setEmboss(true, ((int) viewDataBinding.emossArc.getValue()), ((int) viewDataBinding.emossQiangdu.getValue()), 40, 20, ((int) progress));
//                viewDataBinding.xiejiaoLable.setText("斜角:" + progress);
//            }
//        });
//
//
//        viewDataBinding.dddShengdu.addOnChangeListener(new Slider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
//                MainActivity.this.textContain.getCurrentText().set3dEnabled(true);
//                MainActivity.this.textContain.getCurrentText().set3dDepth(((int) progress), 30, 1, true);
//                viewDataBinding.dddShengduLabel.setText("3d深度:" + progress);
//            }
//        });
//    }
//
//    @Override
//    public void objectTouch() {
//
//    }
//
//    @Override
//    public void onObjectZChanged(String str, int i) {
//
//    }
//
//    @Override
//    public void onShapeCreate(String str) {
//
//    }
//
//    @Override
//    public void onShapeDelete(Bundle bundle, int i, String str) {
//
//    }
//
//    @Override
//    public void onShapeMoveResize(float f, float f2, float f3, float f4, boolean z, String str) {
//
//    }
//
//    @Override
//    public void onShapeSelectionChanged(boolean z, int i) {
//
//    }
//
//    @Override
//    public void onTextCreate(String str) {
//
//    }
//
//    @Override
//    public void onTextDelete(Bundle bundle, int i, String str) {
//
//    }
//
//    @Override
//    public void onTextDoubleTap() {
//
//    }
//
//    @Override
//    public void onTextMove(float f, float f2, float f3, boolean z, String str) {
//
//    }
//
//    @Override
//    public void onTextSelectionChanged(boolean z) {
//
//    }
//}