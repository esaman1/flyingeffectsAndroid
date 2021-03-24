package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;

import java.util.List;

public interface FagBjMvpView {
    void setFragmentList(List<FirstLevelTypeEntity> data);
    void PictureAlbum(List<NewFragmentTemplateItem> data) ;
}
