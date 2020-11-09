package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface FagBjMvpView {
    void setFragmentList(List<TemplateType> data);
    void PictureAlbum(List<new_fag_template_item> data) ;
}
