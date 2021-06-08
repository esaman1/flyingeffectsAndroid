package com.imaginstudio.imagetools.pixellab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.imaginstudio.imagetools.pixellab.TextObject.StickerItemOnitemclick;
import com.imaginstudio.imagetools.pixellab.TextObject.TextComponent;
import com.imaginstudio.imagetools.pixellab.imageinfo.displayInfo;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class textContainer extends RelativeLayout implements TextComponent.OnSelectEventListener {
    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final int abLength = AB.length();
    static SecureRandom rnd = new SecureRandom();
    public int curr_id = -1;
    public int curr_id_shape = -1;
    int lastInserted = -1;
    int lastInserted_shape = -1;
    OnLayersListener layersListener;
    OnSelectionChangedListener mSelectionListener;
    public Map<Integer, TextComponent> texts = new HashMap();
    public boolean willResize;

    displayInfo helperClass;

    public interface OnLayersListener {
        void objectCountChanged();

        void objectZChange();
    }

    public interface OnSelectionChangedListener {
        void objectTouch();

        void onObjectZChanged(String str, int i);

        void onShapeCreate(String str);

        void onShapeDelete(Bundle bundle, int i, String str);

        void onShapeMoveResize(float f, float f2, float f3, float f4, boolean z, String str);

        void onShapeSelectionChanged(boolean z, int i);

        void onTextCreate(String str);

        void onTextDelete(Bundle bundle, int i, String str);

        void onTextDoubleTap();

        void onTextMove(float f, float f2, float f3, boolean z, String str);

        void onTextSelectionChanged(boolean z);
    }

    public enum floatObject {
        left,
        right,
        bottom,
        top,
        center_hor,
        center_ver
    }

    public void resetAll() {
        removeAllViews();
        this.texts.clear();
        this.lastInserted = -1;
        this.curr_id = -1;
        this.lastInserted_shape = -1;
        this.curr_id_shape = -1;
    }

    public void setRenderMode(boolean toggle, float scaleF) {
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            this.texts.get(Integer.valueOf(entry2.getKey().intValue())).setRenderMode(toggle, scaleF);
        }
    }


    public void setSelectionListener(OnSelectionChangedListener selectionListener) {
        this.mSelectionListener = selectionListener;
    }

    @SuppressLint({"NewApi"})
    public textContainer(Context context) {
        super(context);
        setClipChildren(false);
        setLayerType(LAYER_TYPE_NONE, null);
        setOnClickListener(new OnClickListener() {
            /* class com.imaginstudio.imagetools.pixellab.textContainer.AnonymousClass1 */

            @Override
            public void onClick(View arg0) {
                textContainer.this.selectTextId(-1);
                textContainer.this.selectShapeId(-1);
            }
        });
        setDrawingCacheEnabled(true);
    }

    public void editCurrentText(String str) {
        if (this.curr_id != -1 && !this.texts.get(Integer.valueOf(this.curr_id)).returnText().equals(str)) {
            this.texts.get(Integer.valueOf(this.curr_id)).setText(str, true);
        }
    }

    public String addNewText(int initColor, displayInfo helperClass, Drawable drawable, Drawable drawable1, StickerItemOnitemclick StickerItemOnitemclick) {
        this.lastInserted++;
        this.helperClass = helperClass;
        String reference = "上海自来水来自海上";
        TextComponent new_text = new TextComponent(getContext(),
                initColor,
                reference,
                helperClass,
                drawable,
                drawable1
        );
        new_text.setTextSize(160);
        new_text.setText("输入文字", false);
        this.texts.put(Integer.valueOf(this.lastInserted), new_text);
        new_text.assigned_id = this.lastInserted;
        selectShapeId(-1);
        selectTextId(this.lastInserted);
        new_text.setTouchEventListener(this);
        PointF origin = helperClass.getViewOrigin();
        new_text.setX(origin.x);
        new_text.setY(origin.y);
        new_text.setCallback(StickerItemOnitemclick);
        addView(new_text);
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onTextCreate(reference);
        }
        return reference;
    }

//    public String addNewShape(int shapeType, ImageSource imgSource, boolean atOrigin, float x, float y, float width, float height) {
//        this.lastInserted_shape++;
//        String reference = getRandomString(5);
//        ShapeComponent shape = new ShapeComponent(getContext(), shapeType, imgSource, width, height, reference);
//        this.shapes.put(Integer.valueOf(this.lastInserted_shape), shape);
//        shape.assigned_id_shape = this.lastInserted_shape;
//        selectTextId(-1);
//        selectShapeId(this.lastInserted_shape);
//        shape.setTouchEventListener(this);
//        if (atOrigin) {
//            PointF origin = helperClass.getViewOrigin();
//            shape.setX(origin.x);
//            shape.setY(origin.y);
//        } else {
//            shape.setX(x);
//            shape.setY(y);
//        }
//        addView(shape, new LayoutParams(-2, -2));
//        if (this.mSelectionListener != null) {
//            this.mSelectionListener.onShapeCreate(reference);
//        }
//        return reference;
//    }
//
//    public ShapeComponent getCurrentShape() {
//        if (this.curr_id_shape != -1) {
//            return this.shapes.get(Integer.valueOf(this.curr_id_shape));
//        }
//        return null;
//    }
//
//    private ShapeComponent getCurrentOrFirstShape() {
//        if (this.shapes.size() > 0) {
//            return this.curr_id_shape != -1 ? this.shapes.get(Integer.valueOf(this.curr_id_shape)) : this.shapes.get(0);
//        }
//        return null;
//    }

    private TextComponent getCurrentOrFirstText() {
        if (this.texts.size() > 0) {
            return this.curr_id != -1 ? this.texts.get(Integer.valueOf(this.curr_id)) : this.texts.get(0);
        }
        return null;
    }

    public TextComponent getCurrentText() {
        if (this.curr_id != -1) {
            return this.texts.get(Integer.valueOf(this.curr_id));
        }
        return null;
    }

//    public void editCurrShape(int shapeType, ImageSource imgSource, boolean newSize, int width, int height) {
//        if (imgSource.checkValid() && this.curr_id_shape != -1) {
//            int usedWidth = 1;
//            int usedHeight = 1;
//            if (newSize) {
//                usedWidth = width;
//                usedHeight = height;
//            }
//            this.shapes.get(Integer.valueOf(this.curr_id_shape)).resetCropPortion();
//            this.shapes.get(Integer.valueOf(this.curr_id_shape)).editSrc(shapeType, imgSource, newSize, usedWidth, usedHeight);
//        }
//    }

    public void bundleObjects(Bundle bundle) {
        Bundle bundleObjects = new Bundle();
        ArrayList<String> objectsIds = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextComponent) {
                bundleObjects.putBundle("text" + String.valueOf(i), ((TextComponent) getChildAt(i)).saveToBundle());
                objectsIds.add("text" + String.valueOf(i));
            }/* else if (getChildAt(i) instanceof ShapeComponent) {
                bundleObjects.putBundle("shape" + String.valueOf(i), ((ShapeComponent) getChildAt(i)).saveToBundle());
                objectsIds.add("shape" + String.valueOf(i));
            }*/
        }
        if (objectsIds.isEmpty()) {
            bundleObjects.putBoolean(appStateConstants.OBJECTS_SKIP_EMPTY, true);
        }
        bundle.putBundle(appStateConstants.OBJECTS_BUNDLE, bundleObjects);
        bundle.putStringArrayList(appStateConstants.OBJECTS_IDS, objectsIds);
    }

    public void recreateObjects(Bundle objectsBundle, ArrayList<String> objectsIds) {
        if (objectsIds != null) {
            Iterator<String> it = objectsIds.iterator();
            while (it.hasNext()) {
                String objectId = it.next();
                /*if (objectId.contains("shape")) {
                    this.lastInserted_shape++;
                    ShapeComponent shape = new ShapeComponent(getContext(), objectsBundle.getBundle(objectId), false, getRandomString(5));
                    this.shapes.put(Integer.valueOf(this.lastInserted_shape), shape);
                    shape.assigned_id_shape = this.lastInserted_shape;
                    shape.setTouchEventListener(this);
                    addView(shape, objectsIds.indexOf(objectId));
                } else*/
                if (objectId.contains("text")) {
                    this.lastInserted++;
                    TextComponent text = new TextComponent(getContext(), objectsBundle.getBundle(objectId), false, getRandomString(5));
                    this.texts.put(Integer.valueOf(this.lastInserted), text);
                    text.assigned_id = this.lastInserted;
                    text.setTouchEventListener(this);
                    addView(text, objectsIds.indexOf(objectId));
                }
            }
        }
    }

    public void moveCurrTextToFront() {
        TextComponent text = this.texts.get(Integer.valueOf(this.curr_id));
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onObjectZChanged(text.reference, indexOfChild(text));
        }
        text.bringToFront();
        if (this.layersListener != null) {
            this.layersListener.objectZChange();
        }
        invalidate();
    }

    public void moveCurrShapeToFront() {
//        ShapeComponent shape = this.shapes.get(Integer.valueOf(this.curr_id_shape));
//        if (this.mSelectionListener != null) {
//            this.mSelectionListener.onObjectZChanged(shape.reference, indexOfChild(shape));
//        }
//        shape.bringToFront();
//        if (this.layersListener != null) {
//            this.layersListener.objectZChange();
//        }
//        invalidate();
    }

    public void moveCurrTextToBack() {
        if (this.curr_id != -1) {
            TextComponent child = this.texts.get(Integer.valueOf(this.curr_id));
            int index = indexOfChild(child);
            if (this.mSelectionListener != null) {
                this.mSelectionListener.onObjectZChanged(child.reference, indexOfChild(child));
            }
            if (index > 0) {
                detachViewFromParent(index);
                attachViewToParent(child, 0, child.getLayoutParams());
            }
            if (this.layersListener != null) {
                this.layersListener.objectZChange();
            }
            invalidate();
        }
    }

    public void moveCurrShapeToBack() {
        if (this.curr_id_shape != -1) {
           /* ShapeComponent child = this.shapes.get(Integer.valueOf(this.curr_id_shape));
            int index = indexOfChild(child);
            if (this.mSelectionListener != null) {
                this.mSelectionListener.onObjectZChanged(child.reference, indexOfChild(child));
            }
            if (index > 0) {
                detachViewFromParent(index);
                attachViewToParent(child, 0, child.getLayoutParams());
            }
            if (this.layersListener != null) {
                this.layersListener.objectZChange();
            }
            invalidate();*/
        }
    }

    public void setObjectIdInLayout(String reference, View object, int newId, boolean logHistory) {
        if (newId >= 0 && newId < getChildCount()) {
            if (logHistory) {
                int oldId = indexOfChild(object);
                if (this.mSelectionListener != null) {
                    this.mSelectionListener.onObjectZChanged(reference, oldId);
                }
            }
            detachViewFromParent(object);
            attachViewToParent(object, newId, object.getLayoutParams());
            invalidate();
            if (this.layersListener != null) {
                this.layersListener.objectZChange();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!(w == oldw && h == oldh) && this.willResize) {
            /*for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
                this.shapes.get(Integer.valueOf(entry.getKey().intValue())).rearrange(w, h, oldw, oldh);
            }*/
            for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
                this.texts.get(Integer.valueOf(entry2.getKey().intValue())).rearrange(w, h, oldw, oldh);
            }
            this.willResize = false;
        }
    }

    public void copyCurrentShape() {
        /*this.lastInserted_shape++;
        String reference = getRandomString(5);
        ShapeComponent shape = new ShapeComponent(getContext(), this.shapes.get(Integer.valueOf(this.curr_id_shape)).saveToBundle(), true, reference);
        this.shapes.put(Integer.valueOf(this.lastInserted_shape), shape);
        shape.assigned_id_shape = this.lastInserted_shape;
        selectTextId(-1);
        selectShapeId(this.lastInserted_shape);
        shape.setTouchEventListener(this);
        PointF origin = helperClass.getViewOrigin();
        shape.setX(origin.x);
        shape.setY(origin.y);
        addView(shape);
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onShapeCreate(reference);
        }*/
    }

    public void copyCurrentText() {
        this.lastInserted++;
        String reference = getRandomString(5);
        TextComponent text = new TextComponent(getContext(), this.texts.get(Integer.valueOf(this.curr_id)).saveToBundle(), true, reference);
        this.texts.put(Integer.valueOf(this.lastInserted), text);
//        text.showFrame();
        text.assigned_id = this.lastInserted;
        selectShapeId(-1);
        selectTextId(this.lastInserted);
        text.setTouchEventListener(this);
        PointF origin = helperClass.getViewOrigin();
        text.setX(origin.x);
        text.setY(origin.y);
        addView(text);
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onTextCreate(reference);
        }
    }

    private void removeShape(int id) {
        /*if (id != -1) {
            if (this.mSelectionListener != null) {
                ShapeComponent object = this.shapes.get(Integer.valueOf(id));
                this.mSelectionListener.onShapeDelete(object.saveToBundle(), indexOfChild(object), object.reference);
            }
            removeView(this.shapes.get(Integer.valueOf(id)));
            this.shapes.remove(Integer.valueOf(id));
            if (id == this.curr_id_shape) {
                selectShapeId(-1);
            }
        }*/
    }

    private void removeText(int id) {
        if (id != -1) {
            if (this.mSelectionListener != null) {
                TextComponent object = this.texts.get(Integer.valueOf(id));
                this.mSelectionListener.onTextDelete(object.saveToBundle(), indexOfChild(object), object.reference);
            }
            removeView(this.texts.get(Integer.valueOf(id)));
            this.texts.remove(Integer.valueOf(id));
            if (id == this.curr_id) {
                selectTextId(-1);
            }
        }
    }

    private void removeCurrentShape() {
        removeShape(this.curr_id_shape);
    }

    private void removeCurrent() {
        removeText(this.curr_id);
    }

    public String getSelectedReference() {
        if (this.curr_id != -1) {
            return this.texts.get(Integer.valueOf(this.curr_id)).reference;
        }
        /*if (this.curr_id_shape != -1) {
            return this.shapes.get(Integer.valueOf(this.curr_id_shape)).reference;
        }*/
        return "0";
    }

    public void selectByReference(String reference) {
        int text_id_select = -1;
        int shape_id_select = -1;
        Iterator<Map.Entry<Integer, TextComponent>> it = this.texts.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            int i = it.next().getKey().intValue();
            if (this.texts.get(Integer.valueOf(i)).reference.equals(reference)) {
                text_id_select = this.texts.get(Integer.valueOf(i)).assigned_id;
                break;
            }
        }
        /*Iterator<Map.Entry<Integer, ShapeComponent>> it2 = this.shapes.entrySet().iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            int i2 = it2.next().getKey().intValue();
            if (this.shapes.get(Integer.valueOf(i2)).reference.equals(reference)) {
                shape_id_select = this.shapes.get(Integer.valueOf(i2)).assigned_id_shape;
                break;
            }
        }
        selectShapeId(shape_id_select);*/
        selectTextId(text_id_select);
    }

    private void msg(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void removeCurrentObject() {
        if (this.curr_id != -1) {
            removeCurrent();
        } else if (this.curr_id_shape != -1) {
            removeCurrentShape();
        }
    }

    public void selectTextId(int id) {
        if (id == -1) {
            for (Map.Entry<Integer, TextComponent> entry : this.texts.entrySet()) {
                int i = entry.getKey().intValue();
                if (this.texts.get(Integer.valueOf(i)).isSelected()) {
                    this.texts.get(Integer.valueOf(i)).toggleSelect(false);
                }
            }
        } else {
            for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
                int i2 = entry2.getKey().intValue();
                if (i2 != id) {
                    if (this.texts.get(Integer.valueOf(i2)).isSelected()) {
                        this.texts.get(Integer.valueOf(i2)).toggleSelect(false);
                    }
                } else if (!this.texts.get(Integer.valueOf(i2)).isSelected()) {
                    this.texts.get(Integer.valueOf(i2)).toggleSelect(true);
                }
            }
        }
        if (this.texts.containsKey(Integer.valueOf(id))) {
            this.curr_id = id;
        } else {
            this.curr_id = -1;
        }
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onTextSelectionChanged(this.curr_id != -1);
        }
    }

    public void redrawAll() {
       /* for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
            this.shapes.get(Integer.valueOf(entry.getKey().intValue())).postInvalidate();
        }*/
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            this.texts.get(Integer.valueOf(entry2.getKey().intValue())).postInvalidate();
        }
    }

    public void selectShapeId(int id) {
//        boolean z = false;
//        if (id == -1) {
//            for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//                int i = entry.getKey().intValue();
//                if (this.shapes.get(Integer.valueOf(i)).isSelected()) {
//                    this.shapes.get(Integer.valueOf(i)).toggleSelect(false);
//                }
//            }
//        } else {
//            for (Map.Entry<Integer, ShapeComponent> entry2 : this.shapes.entrySet()) {
//                int i2 = entry2.getKey().intValue();
//                if (i2 != id) {
//                    if (this.shapes.get(Integer.valueOf(i2)).isSelected()) {
//                        this.shapes.get(Integer.valueOf(i2)).toggleSelect(false);
//                    }
//                } else if (!this.shapes.get(Integer.valueOf(i2)).isSelected()) {
//                    this.shapes.get(Integer.valueOf(i2)).toggleSelect(true);
//                }
//            }
//        }
//        if (this.shapes.containsKey(Integer.valueOf(id))) {
//            this.curr_id_shape = id;
//        } else {
//            this.curr_id_shape = -1;
//        }
//        if (this.mSelectionListener != null) {
//            int shape_type = this.curr_id_shape == -1 ? 0 : this.shapes.get(Integer.valueOf(this.curr_id_shape)).getShapeType();
//            OnSelectionChangedListener onSelectionChangedListener = this.mSelectionListener;
//            if (this.curr_id_shape != -1) {
//                z = true;
//            }
//            onSelectionChangedListener.onShapeSelectionChanged(z, shape_type);
//        }
    }

    @Override // com.imaginstudio.imagetools.pixellab.TextObject.TextComponent.OnSelectEventListener
    public void onEvent_SelectText(int id_) {
        if (this.mSelectionListener != null) {
            this.mSelectionListener.objectTouch();
        }
        selectShapeId(-1);
        selectTextId(id_);
    }

    @Override // com.imaginstudio.imagetools.pixellab.TextObject.TextComponent.OnSelectEventListener
    public void onEvent_doubleTapText() {
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onTextDoubleTap();
        }
    }

    public void onEvent_SelectShape(int id_) {
        if (this.mSelectionListener != null) {
            this.mSelectionListener.objectTouch();
        }
        selectTextId(-1);
        selectShapeId(id_);
    }

    @Override // com.imaginstudio.imagetools.pixellab.TextObject.TextComponent.OnSelectEventListener
    public void onEvent_MoveMaxText(float oldPosX, float oldPosY, float oldMaxW, boolean moveOnly, String reference) {
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onTextMove(oldPosX, oldPosY, oldMaxW, moveOnly, reference);
        }
    }

    public void onEvent_MoveResizeShape(float oldPosX, float oldPosY, float oldWidth, float oldHeight, boolean moveOnly, String reference) {
        if (this.mSelectionListener != null) {
            this.mSelectionListener.onShapeMoveResize(oldPosX, oldPosY, oldWidth, oldHeight, moveOnly, reference);
        }
    }

    public View getObjectByRef(String reference) {
        View foundText = getTextByRef(reference);
        return foundText/* != null ? foundText : getShapeByRef(reference)*/;
    }

//    public ShapeComponent getShapeByRef(String reference) {
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            int i = entry.getKey().intValue();
//            if (commonFuncs.compareStrings(this.shapes.get(Integer.valueOf(i)).reference, reference)) {
//                return this.shapes.get(Integer.valueOf(i));
//            }
//        }
//        return null;
//    }

    public TextComponent getTextByRef(String reference) {
        for (Map.Entry<Integer, TextComponent> entry : this.texts.entrySet()) {
            int i = entry.getKey().intValue();
            if (commonFuncs.compareStrings(this.texts.get(Integer.valueOf(i)).reference, reference)) {
                return this.texts.get(Integer.valueOf(i));
            }
        }
        return null;
    }

    public void undoCreateText(String reference) {
        for (Map.Entry<Integer, TextComponent> entry : this.texts.entrySet()) {
            int i = entry.getKey().intValue();
            if (this.texts.get(Integer.valueOf(i)).reference.equals(reference)) {
                removeView(this.texts.get(Integer.valueOf(i)));
                this.texts.remove(Integer.valueOf(this.texts.get(Integer.valueOf(i)).assigned_id));
                if (i == this.curr_id) {
                    selectTextId(-1);
                    return;
                }
                return;
            }
        }
    }

    public void undoObjectZChange(String reference, int oldId) {
        View oldObject = getObjectByRef(reference);
        if (oldObject != null && oldId > -1) {
            setObjectIdInLayout(reference, oldObject, oldId, false);
        }
    }

    public void undoCreateShape(String reference) {
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            int i = entry.getKey().intValue();
//            if (this.shapes.get(Integer.valueOf(i)).reference.equals(reference)) {
//                removeView(this.shapes.get(Integer.valueOf(i)));
//                this.shapes.remove(Integer.valueOf(this.shapes.get(Integer.valueOf(i)).assigned_id_shape));
//                if (i == this.curr_id_shape) {
//                    selectShapeId(-1);
//                    return;
//                }
//                return;
//            }
//        }
    }

    public void undoDeleteText(Bundle data, int where, String reference) {
        this.lastInserted++;
        TextComponent text = new TextComponent(getContext(), data, false, reference);
        this.texts.put(Integer.valueOf(this.lastInserted), text);
        text.assigned_id = this.lastInserted;
        text.setTouchEventListener(this);
        selectShapeId(-1);
        selectTextId(this.lastInserted);
        if (where != -1) {
            addView(text, where);
        } else {
            addView(text);
        }
    }

    public void undoDeleteShape(Bundle data, int where, String reference) {
//        this.lastInserted_shape++;
//        ShapeComponent shape = new ShapeComponent(getContext(), data, false, reference);
//        this.shapes.put(Integer.valueOf(this.lastInserted_shape), shape);
//        shape.assigned_id_shape = this.lastInserted_shape;
//        shape.setTouchEventListener(this);
//        selectTextId(-1);
//        selectShapeId(this.lastInserted_shape);
//        if (where != -1) {
//            addView(shape, where);
//        } else {
//            addView(shape);
//        }
    }

    static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(abLength)));
        }
        return sb.toString();
    }

    public void setLayersListener(OnLayersListener listener) {
        this.layersListener = listener;
    }

    public void removeObjectsByReferences(ArrayList<String> references) {
//        if (references != null && references.size() > 0) {
//            Iterator<String> it = references.iterator();
//            while (it.hasNext()) {
//                removeObjectByReference(History.ComponentType.unspecified, it.next());
//            }
//        }
    }

//    public void removeObjectByReference(History.ComponentType type, String reference) {
//        if (type == History.ComponentType.unspecified || type == History.ComponentType.text) {
//            for (Map.Entry<Integer, TextComponent> entry : this.texts.entrySet()) {
//                int i = entry.getKey().intValue();
//                if (this.texts.get(Integer.valueOf(i)).reference.equals(reference)) {
//                    removeText(i);
//                    return;
//                }
//            }
//        }
//        if (type == History.ComponentType.unspecified || type == History.ComponentType.shape) {
//            for (Map.Entry<Integer, ShapeComponent> entry2 : this.shapes.entrySet()) {
//                int i2 = entry2.getKey().intValue();
//                if (this.shapes.get(Integer.valueOf(i2)).reference.equals(reference)) {
//                    removeShape(i2);
//                    return;
//                }
//            }
//        }
//    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if (this.layersListener != null) {
            this.layersListener.objectCountChanged();
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (this.layersListener != null) {
            this.layersListener.objectCountChanged();
        }
    }

    public PointF computeObjectsCenter(ArrayList<String> references) {
        float x = 0.0f;
        float y = 0.0f;
        int count = 0;
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            int i = entry.getKey().intValue();
//            if (references.contains(this.shapes.get(Integer.valueOf(i)).reference)) {
//                x += this.shapes.get(Integer.valueOf(i)).getX() + this.shapes.get(Integer.valueOf(i)).getPivotX();
//                y += this.shapes.get(Integer.valueOf(i)).getY() + this.shapes.get(Integer.valueOf(i)).getPivotY();
//                count++;
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            int i2 = entry2.getKey().intValue();
            if (references.contains(this.texts.get(Integer.valueOf(i2)).reference)) {
                x += this.texts.get(Integer.valueOf(i2)).getX() + this.texts.get(Integer.valueOf(i2)).getPivotX();
                y += this.texts.get(Integer.valueOf(i2)).getY() + this.texts.get(Integer.valueOf(i2)).getPivotY();
                count++;
            }
        }
        return new PointF(x / ((float) count), y / ((float) count));
    }

    /* access modifiers changed from: package-private */
    public PointF getGlobalCenter(View object) {
        return new PointF(object.getX() + object.getPivotX(), object.getY() + object.getPivotY());
    }

    public void rotateObjects(ArrayList<String> references, int angle) {
        PointF center = computeObjectsCenter(references);
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references.contains(object.reference)) {
//                object.rotateShape(object.angle + ((float) angle));
//                PointF currentCenter = getGlobalCenter(object);
//                float theta = (float) (Math.toRadians((double) angle) + Math.atan2((double) (currentCenter.y - center.y), (double) (currentCenter.x - center.x)));
//                float distance = commonFuncs.dist(currentCenter, center);
//                object.setX((float) ((((double) center.x) + (((double) distance) * Math.cos((double) theta))) - ((double) object.getPivotX())));
//                object.setY((float) ((((double) center.y) + (((double) distance) * Math.sin((double) theta))) - ((double) object.getPivotY())));
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references.contains(object2.reference)) {
                object2.rotateText(object2.angle + ((float) angle));
                PointF currentCenter2 = getGlobalCenter(object2);
                float theta2 = (float) (Math.toRadians((double) angle) + Math.atan2((double) (currentCenter2.y - center.y), (double) (currentCenter2.x - center.x)));
                float distance2 = commonFuncs.dist(currentCenter2, center);
                object2.setX((float) ((((double) center.x) + (((double) distance2) * Math.cos((double) theta2))) - ((double) object2.getPivotX())));
                object2.setY((float) ((((double) center.y) + (((double) distance2) * Math.sin((double) theta2))) - ((double) object2.getPivotY())));
            }
        }
    }

    public void hideLockObjects(ArrayList<String> references, boolean hide) {
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references.contains(object.reference)) {
//                if (hide) {
//                    object.setHidden(!object.isHidden());
//                } else {
//                    object.setLocked(!object.isLocked());
//                }
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references.contains(object2.reference)) {
                if (hide) {
                    object2.setHidden(!object2.isHidden());
                } else {
                    object2.setLocked(!object2.isLocked());
                }
            }
        }
    }

    public void positionObjects(ArrayList<String> references, float dx, float dy) {
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references.contains(object.reference)) {
//                object.setX(object.getX() + dx);
//                object.setY(object.getY() + dy);
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references.contains(object2.reference)) {
                object2.setX(object2.getX() + dx);
                object2.setY(object2.getY() + dy);
            }
        }
    }

    public void scaleObjects(ArrayList<String> references, float factor) {
        PointF center = computeObjectsCenter(references);
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references.contains(object.reference)) {
//                PointF currentCenter = getGlobalCenter(object);
//                float newX = (currentCenter.x - center.x) * factor;
//                float newY = (currentCenter.y - center.y) * factor;
//                object.setX((center.x + newX) - object.getPivotX());
//                object.setY((center.y + newY) - object.getPivotY());
//                object.relativeScale(factor);
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references.contains(object2.reference)) {
                object2.relativeScale(factor);
                PointF currentCenter2 = getGlobalCenter(object2);
                float newX2 = (currentCenter2.x - center.x) * factor;
                float newY2 = (currentCenter2.y - center.y) * factor;
                object2.setX((center.x + newX2) - object2.getPivotX());
                object2.setY((center.y + newY2) - object2.getPivotY());
            }
        }
    }

    public void relativePosObjects(ArrayList<String> references, floatObject gravity) {
        RectF objectsBoundingBox = getObjectsBoundingBox(references);
        float dX = 0.0f;
        float dY = 0.0f;
        switch (gravity) {
            case left:
                dX = -1.0f * objectsBoundingBox.left;
                break;
            case right:
                dX = ((float) helperClass.getContainerWidth()) - objectsBoundingBox.right;
                break;
            case bottom:
                dY = ((float) helperClass.getContainerHeight()) - objectsBoundingBox.bottom;
                break;
            case top:
                dY = -1.0f * objectsBoundingBox.top;
                break;
            case center_hor:
                dX = (((float) helperClass.getContainerWidth()) * 0.5f) - objectsBoundingBox.centerX();
                break;
            case center_ver:
                dY = (((float) helperClass.getContainerHeight()) * 0.5f) - objectsBoundingBox.centerY();
                break;
        }
        if (!(dX == 0.0f && dY == 0.0f)) {
//            for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//                ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//                if (references.contains(object.reference)) {
//                    object.setX(object.getX() + dX);
//                    object.setY(object.getY() + dY);
//                }
//            }
            for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
                TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
                if (references.contains(object2.reference)) {
                    object2.setX(object2.getX() + dX);
                    object2.setY(object2.getY() + dY);
                }
            }
        }
    }

    private RectF getObjectsBoundingBox(ArrayList<String> references) {
        float minX = 2.14748365E9f;
        float minY = 2.14748365E9f;
        float maxX = -2.14748365E9f;
        float maxY = -2.14748365E9f;
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references.contains(object.reference)) {
//                if (object.getX() < minX) {
//                    minX = object.getX();
//                }
//                if (object.getY() < minY) {
//                    minY = object.getY();
//                }
//                if (object.getX() + ((float) object.width) > maxX) {
//                    maxX = object.getX() + ((float) object.width);
//                }
//                if (object.getY() + ((float) object.height) > maxY) {
//                    maxY = object.getY() + ((float) object.height);
//                }
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references.contains(object2.reference)) {
                if (object2.getX() < minX) {
                    minX = object2.getX();
                }
                if (object2.getY() < minY) {
                    minY = object2.getY();
                }
                if (object2.getX() + ((float) object2.textWidth) > maxX) {
                    maxX = object2.getX() + ((float) object2.textWidth);
                }
                if (object2.getY() + ((float) object2.textHeight) > maxY) {
                    maxY = object2.getY() + ((float) object2.textHeight);
                }
            }
        }
        return new RectF(minX, minY, maxX, maxY);
    }

    public void mergeObjects(ArrayList<String> references) {
        try {
            Bitmap bmOut = Bitmap.createBitmap((int) (((float) getWidth()) * 2.0f), (int) (((float) getHeight()) * 2.0f), Bitmap.Config.ARGB_8888);
            Canvas tmpCanvas = new Canvas(bmOut);
            tmpCanvas.scale(2.0f, 2.0f);
            setVisibility(View.INVISIBLE);
            setRenderMode(true, 2.0f);
            hideAllBut(references, true);
            draw(tmpCanvas);
            Rect boundingBox = DrawingPanelRenderer.getAutoCropBound(bmOut);
            if (boundingBox.right == -1 || boundingBox.left == -1 || boundingBox.top == -1 || boundingBox.bottom == -1) {
                throw new Exception();
            }
            Bitmap bmOut2 = DrawingPanelRenderer.autoCropBitmap(bmOut, boundingBox);
            File returned = commonFuncs.getCacheFile(commonFuncs.generateName("drawingMerged"));
            commonFuncs.saveFile(bmOut2, returned, 1);
            removeObjectsByReferences(references);
            setRenderMode(false, 1.0f);
            unHideAll();
            setVisibility(View.VISIBLE);
//            addNewShape(ShapeComponent.SHAPE_TYPE_DRAWING, new ImageSource(returned.getPath()), false, ((float) boundingBox.left) / 2.0f, ((float) boundingBox.top) / 2.0f, ((float) boundingBox.width()) / 2.0f, ((float) boundingBox.height()) / 2.0f);
        } catch (Throwable th) {
            Toast.makeText(getContext(), "(int) R.string.error_merge", Toast.LENGTH_SHORT).show();
        }
    }

    /* access modifiers changed from: package-private */
    public void unHideAll() {
        hideAllBut(null, false);
    }

    /* access modifiers changed from: package-private */
    public void hideAllBut(ArrayList<String> references, boolean hide) {
//        for (Map.Entry<Integer, ShapeComponent> entry : this.shapes.entrySet()) {
//            ShapeComponent object = this.shapes.get(Integer.valueOf(entry.getKey().intValue()));
//            if (references == null || !references.contains(object.reference)) {
//                if (hide) {
//                    object.hideTemp();
//                } else {
//                    object.unhideTemp();
//                }
//            }
//        }
        for (Map.Entry<Integer, TextComponent> entry2 : this.texts.entrySet()) {
            TextComponent object2 = this.texts.get(Integer.valueOf(entry2.getKey().intValue()));
            if (references == null || !references.contains(object2.reference)) {
                if (hide) {
                    object2.hideTemp();
                } else {
                    object2.unhideTemp();
                }
            }
        }
    }

    @Deprecated
    public static String getObjectCount(Context context) {
        return context.getPackageName();
    }
}
