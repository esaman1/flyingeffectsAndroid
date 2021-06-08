package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.imaginstudio.imagetools.R;

public class gradientTool_controls extends LinearLayout {
    ImageButton currentColorControl = ((ImageButton) findViewById(R.id.select_color));
    gradientTool_slider sliderPreview;

    public gradientTool_controls(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.gradient_controls, this);
        View select_leftB = findViewById(R.id.select_left);
        View select_RightB = findViewById(R.id.select_right);
        View addColorB = findViewById(R.id.addColor);
        View removeColorB = findViewById(R.id.removeColor);
        View flipPositionsB = findViewById(R.id.flipPos);
        LayoutParams lpa = new LayoutParams(dpToPixels(40), dpToPixels(40));
        lpa.gravity = 17;
        lpa.rightMargin = dpToPixels(3);
        lpa.leftMargin = dpToPixels(3);
        this.currentColorControl.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass1 */

            public void onClick(View view) {
//                new colorPickerDialog(gradientTool_controls.this.getContext(), gradientTool_controls.this.sliderPreview.lastSelectedHandle.getColor(), new colorPickerDialog.OnColorSelectedListener() {
//                    /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass1.AnonymousClass1 */
//
//                    @Override // com.imaginstudio.imagetools.pixellab.colorPickerDialog.OnColorSelectedListener
//                    public void onColorSelected(int color) {
//                        gradientTool_controls.this.changeCurrentColor(color);
//                    }
//                }).show();
            }
        });
        addColorB.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass2 */

            public void onClick(View view) {
                gradientTool_controls.this.sliderPreview.addNewColor();
            }
        });
        removeColorB.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass3 */

            public void onClick(View view) {
                gradientTool_controls.this.sliderPreview.removeCurrentColor();
            }
        });
        select_leftB.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass4 */

            public void onClick(View view) {
                gradientTool_controls.this.sliderPreview.selectLeft();
            }
        });
        select_RightB.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass5 */

            public void onClick(View view) {
                gradientTool_controls.this.sliderPreview.selectRight();
            }
        });
        flipPositionsB.setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_controls.AnonymousClass6 */

            public void onClick(View view) {
                gradientTool_controls.this.sliderPreview.flipPositions();
            }
        });
    }

    public void connectSlider(gradientTool_slider sliderPreview2) {
        this.sliderPreview = sliderPreview2;
        sliderPreview2.connectControls(this);
    }

    public void selectionChanged() {
        if (this.sliderPreview == null || this.sliderPreview.lastSelectedHandle != null) {
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeCurrentColor(int newColor) {
        if (this.sliderPreview != null && this.sliderPreview.lastSelectedHandle != null) {
            this.sliderPreview.lastSelectedHandle.setColor(newColor);
            this.sliderPreview.gradientColorsChanged();
        }
    }

    private void changeCurrentPosition(int newPos) {
        if (this.sliderPreview != null && this.sliderPreview.lastSelectedHandle != null) {
            this.sliderPreview.setCurrentColorPosition(newPos);
            this.sliderPreview.gradientChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }
}
