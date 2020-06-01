package com.flyingeffects.com.view.animations.CustomMove;

import com.flyingeffects.com.enity.TransplationPos;

import java.util.ArrayList;

public interface LayerAnimCallback {

    void translationalXY(ArrayList<TransplationPos> listForTranslaptionPosition);

    void rotate(ArrayList<Float> angle);

    void scale(ArrayList<Float> scale);




}
