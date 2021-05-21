package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.entity.TransplationPos;

import java.util.ArrayList;

public interface LayerAnimCallback {

    void translationalXY(ArrayList<TransplationPos> listForTranslaptionPosition);

    void rotate(ArrayList<Float> angle);

    void scale(ArrayList<Float> scale);




}
