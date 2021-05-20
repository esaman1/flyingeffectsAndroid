package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.List;

public interface FagBjMvpCallback {

    void setFragmentList(List<FirstLevelTypeEntity> data);

    void PictureAlbum(List<NewFragmentTemplateItem> data);



}
