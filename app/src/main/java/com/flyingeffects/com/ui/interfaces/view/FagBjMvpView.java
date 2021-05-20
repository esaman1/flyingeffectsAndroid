package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.List;

public interface FagBjMvpView {
    void setFragmentList(List<FirstLevelTypeEntity> data);
    void PictureAlbum(List<NewFragmentTemplateItem> data) ;
}
