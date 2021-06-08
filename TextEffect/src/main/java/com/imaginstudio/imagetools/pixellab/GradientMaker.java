package com.imaginstudio.imagetools.pixellab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

public class GradientMaker extends AlertDialog {
    View dialogLayout;
    gradientTool_controls gradientControls;
    gradientTool_Preview gradientPreview;
    gradientTool_slider gradientSlider;
    private OnClickListener onClickListener = new OnClickListener() {
        /* class com.imaginstudio.imagetools.pixellab.GradientMaker.AnonymousClass2 */

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case -2:
                    dialog.dismiss();
                    return;
                case -1:
                    if (GradientMaker.this.onGradientSelectedListener != null) {
                        GradientMaker.this.onGradientSelectedListener.onGradientSelected(GradientMaker.this.gradientPreview.getCurrentGradient());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private OnGradientSelectedListener onGradientSelectedListener;

    public interface OnGradientSelectedListener {
        void onGradientSelected(GradientFill gradientFill);
    }

    public GradientMaker(Context context, GradientFill importedGradient, OnGradientSelectedListener onGradientSelectedListener2) {
        super(context);
//        LayoutInflater inflater = getLayoutInflater();
//        this.onGradientSelectedListener = onGradientSelectedListener2;
//        this.dialogLayout = inflater.inflate(R.layout.activity_gradient_maker, (ViewGroup) null);
//        setButton(-1, context.getString(17039370), this.onClickListener);
//        setButton(-2, context.getString(17039360), this.onClickListener);
//        setView(this.dialogLayout);
//        this.gradientSlider = (gradientTool_slider) this.dialogLayout.findViewById(R.id.gradientSlider);
//        this.gradientPreview = (gradientTool_Preview) this.dialogLayout.findViewById(R.id.gradientPreview);
//        this.gradientPreview.setDirection(importedGradient.getGradV2_type(), importedGradient.getGradV2_direction());
//        this.gradientSlider.initializeColors(importedGradient.getGradV2_colors(), importedGradient.getGradV2_positions());
//        this.gradientControls = (gradientTool_controls) this.dialogLayout.findViewById(R.id.gradientControls);
//        this.gradientControls.connectSlider(this.gradientSlider);
//        this.gradientSlider.connectPreview(this.gradientPreview);
//        fillWithGradientTypes((LinearLayout) this.dialogLayout.findViewById(R.id.gradient_types_holder));
    }

    private void fillWithGradientTypes(LinearLayout holder) {
        LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(dpToPixels(30), dpToPixels(30));
        int dpToPixels = dpToPixels(3);
        lpa.bottomMargin = dpToPixels;
        lpa.topMargin = dpToPixels;
        lpa.rightMargin = dpToPixels;
        lpa.leftMargin = dpToPixels;
        lpa.gravity = 17;
        GradientFill modelGradient = new GradientFill(Color.parseColor("#b3e5fc"), Color.parseColor("#01579b"));
        addAGradientTypeButton(1, holder, lpa, modelGradient);
        addAGradientTypeButton(2, holder, lpa, modelGradient);
        addAGradientTypeButton(3, holder, lpa, modelGradient);
        addAGradientTypeButton(4, holder, lpa, modelGradient);
        addAGradientTypeButton(5, holder, lpa, modelGradient);
        addAGradientTypeButton(6, holder, lpa, modelGradient);
    }

    private void addAGradientTypeButton(final int preset, LinearLayout holder, LinearLayout.LayoutParams lpa, GradientFill modelGradient) {
//        gradientTool_Preview.defaultDirections.modifyGradientWith(modelGradient, preset);
//        colorButton aButton = new colorButton(this.dialogLayout.getContext(), 2, 0, modelGradient, true);
//        aButton.setOnClickListener(new View.OnClickListener() {
//            /* class com.imaginstudio.imagetools.pixellab.GradientMaker.AnonymousClass1 */
//
//            public void onClick(View view) {
//                GradientMaker.this.gradientPreview.modifyDirectionsWithDefault(preset);
//                GradientMaker.this.gradientPreview.invalidate();
//            }
//        });
//        holder.addView(aButton, lpa);
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, this.dialogLayout.getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(-1, -1);
        getWindow().setFlags(1024, 1024);
    }

    public static class GradientFill {
        public static final int GRADIENT_TYPE_LIN_HOR = 0;
        public static final int GRADIENT_TYPE_LIN_VER = 1;
        public static final int GRADIENT_TYPE_RADIAL = 2;
        public static final int GRADIENT_V2_TYPE_LIN = 3;
        public static final int GRADIENT_V2_TYPE_RAD = 4;
        private int endColor;
        private int[] gradV2_colors;
        private int[] gradV2_direction;
        private float[] gradV2_positions;
        private int gradV2_type;
        private int startColor;
        private int type;

        public int[] getGradV2_direction() {
            return this.gradV2_direction;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type2) {
            this.type = type2;
        }

        public int getStartColor() {
            return this.startColor;
        }

        public void setStartColor(int startColor2) {
            this.startColor = startColor2;
        }

        public int getEndColor() {
            return this.endColor;
        }

        public float[] getGradV2_positions() {
            return this.gradV2_positions;
        }

        public int[] getGradV2_colors() {
            return this.gradV2_colors;
        }

        public int getGradV2_type() {
            return this.gradV2_type;
        }

        public void setEndColor(int endColor2) {
            this.endColor = endColor2;
        }

        public void setV2Direction(int type2, int[] relativeDirection) {
            this.gradV2_type = type2;
            this.gradV2_direction = (int[]) relativeDirection.clone();
        }

        public void setV2Colors(int[] colors, float[] positions) {
            this.gradV2_colors = colors;
            this.gradV2_positions = positions;
        }

        public Shader getShader(Rect contentBounds) {
            return getShader(((((float) this.gradV2_direction[0]) / 100.0f) * ((float) contentBounds.width())) + ((float) contentBounds.left), ((((float) this.gradV2_direction[1]) / 100.0f) * ((float) contentBounds.height())) + ((float) contentBounds.top), ((((float) this.gradV2_direction[2]) / 100.0f) * ((float) contentBounds.width())) + ((float) contentBounds.left), ((((float) this.gradV2_direction[3]) / 100.0f) * ((float) contentBounds.height())) + ((float) contentBounds.top));
        }

        public Shader getShader(float X0, float Y0, float X1, float Y1) {
            if (this.gradV2_type == 4) {
                return new RadialGradient(X0, Y0, Math.max((float) Math.sqrt(Math.pow((double) (X0 - X1), 2.0d) + Math.pow((double) (Y0 - Y1), 2.0d)), 0.1f), this.gradV2_colors, this.gradV2_positions, Shader.TileMode.CLAMP);
            }
            if (this.gradV2_type == 3) {
                return new LinearGradient(X0, Y0, X1, Y1, this.gradV2_colors, this.gradV2_positions, Shader.TileMode.CLAMP);
            }
            return null;
        }

        public Shader getShader(PointF p1, PointF p2) {
            return getShader(p1.x, p1.y, p2.x, p2.y);
        }

        public Shader getShader(RectF contentBounds) {
            return getShader(((((float) this.gradV2_direction[0]) / 100.0f) * contentBounds.width()) + contentBounds.left, ((((float) this.gradV2_direction[1]) / 100.0f) * contentBounds.height()) + contentBounds.top, ((((float) this.gradV2_direction[2]) / 100.0f) * contentBounds.width()) + contentBounds.left, ((((float) this.gradV2_direction[3]) / 100.0f) * contentBounds.height()) + contentBounds.top);
        }

        public String convertToStringV2() {
            StringBuilder gradientString = new StringBuilder();
            gradientString.append("v2;");
            gradientString.append(getGradV2_type());
            gradientString.append(";");
            appendArray(getGradV2_direction(), gradientString);
            gradientString.append(";");
            appendArray(getGradV2_colors(), gradientString);
            gradientString.append(";");
            appendArray(getGradV2_positions(), gradientString);
            return gradientString.toString();
        }

        public GradientFill(String gradient) {
            this.gradV2_type = 3;
            this.gradV2_direction = new int[]{50, 0, 50, 100};
            this.gradV2_colors = new int[]{-3355444, -12303292};
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.type = 1;
            this.startColor = -3355444;
            this.endColor = -12303292;
            String[] gradientElements = gradient.split(";");
            try {
                if (gradientElements[0].equals("v2")) {
                    int type2 = Integer.parseInt(gradientElements[1]);
                    int[] directions = stringToInt(gradientElements[2].split("/"));
                    int[] colors = stringToInt(gradientElements[3].split("/"));
                    float[] positions = stringToFloat(gradientElements[4].split("/"));
                    setV2Direction(type2, directions);
                    setV2Colors(colors, positions);
                    return;
                }
                this.type = Integer.valueOf(gradient.split(";")[0]).intValue();
                this.startColor = Integer.valueOf(gradient.split(";")[1]).intValue();
                this.endColor = Integer.valueOf(gradient.split(";")[2]).intValue();
            } catch (IndexOutOfBoundsException e) {
            }
        }

        /* access modifiers changed from: package-private */
        public int[] stringToInt(String[] strs) {
            int[] toReturn = new int[strs.length];
            for (int i = 0; i < strs.length; i++) {
                toReturn[i] = Integer.parseInt(strs[i]);
            }
            return toReturn;
        }

        /* access modifiers changed from: package-private */
        public float[] stringToFloat(String[] strs) {
            float[] toReturn = new float[strs.length];
            for (int i = 0; i < strs.length; i++) {
                toReturn[i] = Float.parseFloat(strs[i]);
            }
            return toReturn;
        }

        private void appendArray(int[] array, StringBuilder strBuilderInstance) {
            for (int i = 0; i < array.length; i++) {
                strBuilderInstance.append(array[i]);
                if (i < array.length - 1) {
                    strBuilderInstance.append("/");
                }
            }
        }

        private void appendArray(float[] array, StringBuilder strBuilderInstance) {
            for (int i = 0; i < array.length; i++) {
                strBuilderInstance.append(array[i]);
                if (i < array.length - 1) {
                    strBuilderInstance.append("/");
                }
            }
        }

        public int getColorAtTopLeft() {
//            Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(bmp);
//            Paint pnt = new Paint();
//            pnt.setShader(getShader(new Rect(0, 0, 2, 2)));
//            canvas.drawRect(0.0f, 0.0f, 2.0f, 2.0f, pnt);
//            int color = ViewCompat.MEASURED_STATE_MASK;
//            try {
//                color = bmp.getPixel(0, 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            bmp.recycle();
//            return color;
            return 0;
        }

        public GradientFill(int V2_type, int[] V2_direction, int[] V2_colors, float[] V2_positions) {
            this.gradV2_type = 3;
            this.gradV2_direction = new int[]{50, 0, 50, 100};
            this.gradV2_colors = new int[]{-3355444, -12303292};
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.type = 1;
            this.startColor = -3355444;
            this.endColor = -12303292;
            this.gradV2_type = V2_type;
            this.gradV2_direction = V2_direction;
            this.gradV2_colors = V2_colors;
            this.gradV2_positions = V2_positions;
        }

        @Deprecated
        public GradientFill(int type2, int startColor2, int endColor2) {
            this.gradV2_type = 3;
            this.gradV2_direction = new int[]{50, 0, 50, 100};
            this.gradV2_colors = new int[]{-3355444, -12303292};
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.type = 1;
            this.startColor = -3355444;
            this.endColor = -12303292;
            this.type = type2;
            this.endColor = endColor2;
            this.startColor = startColor2;
            convertToV2(type2, startColor2, endColor2);
        }

        public GradientFill(int startColor2, int endColor2) {
            this.gradV2_type = 3;
            this.gradV2_direction = new int[]{50, 0, 50, 100};
            this.gradV2_colors = new int[]{-3355444, -12303292};
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.type = 1;
            this.startColor = -3355444;
            this.endColor = -12303292;
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.gradV2_colors = new int[]{startColor2, endColor2};
        }

        public GradientFill() {
            this.gradV2_type = 3;
            this.gradV2_direction = new int[]{50, 0, 50, 100};
            this.gradV2_colors = new int[]{-3355444, -12303292};
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.type = 1;
            this.startColor = -3355444;
            this.endColor = -12303292;
        }

        public GradientFill copy() {
            return new GradientFill(this.gradV2_type, this.gradV2_direction, this.gradV2_colors, this.gradV2_positions);
        }

        private void convertToV2(int typeV1, int initialColor, int finalColor) {
            this.gradV2_positions = new float[]{0.0f, 1.0f};
            this.gradV2_colors = new int[]{initialColor, finalColor};
            this.gradV2_type = typeV1 == 2 ? 4 : 3;
            switch (typeV1) {
                case 0:
                    this.gradV2_direction[0] = 0;
                    this.gradV2_direction[1] = 50;
                    this.gradV2_direction[2] = 100;
                    this.gradV2_direction[3] = 50;
                    return;
                case 1:
                    this.gradV2_direction[0] = 50;
                    this.gradV2_direction[1] = 0;
                    this.gradV2_direction[2] = 50;
                    this.gradV2_direction[3] = 100;
                    return;
                case 2:
                    this.gradV2_direction[0] = 50;
                    this.gradV2_direction[1] = 50;
                    this.gradV2_direction[2] = 100;
                    this.gradV2_direction[3] = 100;
                    return;
                default:
                    return;
            }
        }

        public boolean hasAlpha() {
            for (int color : this.gradV2_colors) {
                if (Color.alpha(color) != 255) {
                    return true;
                }
            }
            return false;
        }

        public static String makeString(int[] letters) {
            StringBuilder builder = new StringBuilder(letters.length);
            for (int i : letters) {
                builder.append(textContainer.AB.charAt(i));
            }
            return builder.toString();
        }
    }
}
