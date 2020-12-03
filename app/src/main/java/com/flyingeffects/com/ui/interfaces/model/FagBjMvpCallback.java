package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface FagBjMvpCallback {

    void setFragmentList(List<FirstLevelTypeEntity> data);

    void PictureAlbum(List<new_fag_template_item> data);



}
