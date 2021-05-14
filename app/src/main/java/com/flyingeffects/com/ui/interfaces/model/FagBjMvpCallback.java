package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;

import java.util.List;

public interface FagBjMvpCallback {

    void setFragmentList(List<FirstLevelTypeEntity> data);

    void PictureAlbum(List<NewFragmentTemplateItem> data);



}
