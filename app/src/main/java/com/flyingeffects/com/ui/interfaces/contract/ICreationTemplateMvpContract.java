package com.flyingeffects.com.ui.interfaces.contract;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.base.mvpBase.BaseModel;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.base.mvpBase.BaseView;
import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

import java.util.ArrayList;
import java.util.List;

public interface ICreationTemplateMvpContract {

    abstract class CreationTemplateMvpPresenter extends BasePresenter<ICreationTemplateMvpView, ICreationTemplateMvpModel> {

        public abstract void itemClickForStickView(AnimStickerModel stickView);

        public abstract void hasPlayingComplete();

        public abstract void chooseMusicIndex(int index);

        public abstract void deleteFirstSticker();

        public abstract void hideKeyBord();

        public abstract void stickerOnclickCallback(String title);

        public abstract void animIsComplate();

        public abstract void getVideoDuration(long duration);

        public abstract void needPauseVideo();

        public abstract void getVideoCover(String path, String originalPath);

        public abstract void getBgmPath(String path);

        public abstract void showRenderVideoTime(int duration);

        public abstract void changFirstVideoSticker(String path);

        public abstract void isFirstAddSuccess();

        public abstract void showCreateTemplateAnim(boolean isShow);

        public abstract void showMusicBtn(boolean isShow);

        public abstract void showTextDialog(String inputText);

        public abstract void hineTextDialog();

        public abstract void addStickerTimeLine(String id, boolean isText, String text, StickerView stickerView);

        public abstract void updateTimeLineSickerText(String text, String id);

        public abstract void deleteTimeLineSicker(String id);

        public abstract void showTimeLineSickerArrow(String id);

        public abstract void modifyTimeLineSickerPath(String id, String path, StickerView stickerView);

        public abstract void stickerFragmentClose();

        public abstract void showLoadingDialog();

        public abstract void dismissLoadingDialog();

        public abstract void setDialogProgress(String title, int dialogProgress, String content);


        public abstract void chooseFrame(String path);

        public abstract void dismissStickerFrame();

        public abstract void setVideoPath(String path);

        public abstract void dismissTextStickerFrame();

        public abstract void chooseCheckBox(int i);

        public abstract long getDuration();

        public abstract void returnStickerTypeList(ArrayList<StickerTypeEntity> list);

        public abstract void getStickerTypeList();

        public abstract void chooseInitMusic();

        public abstract void startPlayAnim(int position, boolean isClearAllAnim, StickerView targetStickerView, int intoPosition, boolean isFromPreview);

        public abstract void modificationSingleAnimItemIsChecked(int i);

        public abstract List<StickerAnim> getListAllAnim();

        public abstract void chooseNowStickerMaterialMusic();

        public abstract void chooseTemplateMusic(boolean b);

        public abstract void chooseAddChooseBjPath();

        public abstract void closeAllAnim();

        public abstract void stopAllAnim();

        public abstract void deleteAllSticker();

        public abstract void addSticker(String stickerPath, String title);

        public abstract void copyGif(String fileName, String copyName, String title);
    }

    interface ICreationTemplateMvpView extends BaseView {

        void itemClickForStickView(AnimStickerModel stickView);

        void hasPlayingComplete();

        void chooseMusicIndex(int index);

        void deleteFirstSticker();

        void stickerOnclickCallback(String str);

        void showTextDialog(String inputText);

        void hideTextDialog();

        void getVideoDuration(long duration);

        void needPauseVideo();

        void getVideoCover(String path, String originalPath);

        void getBgmPath(String path);

        void hideKeyBord();

        void changFirstVideoSticker(String path);

        void isFirstAddSuccess();

        void showCreateTemplateAnim(boolean isShow);

        void showMusicBtn(boolean isShow);

        void animIsComplate();

        void addStickerTimeLine(String id, boolean isText, String text, StickerView stickerView);

        void updateTimeLineSickerText(String text, String id);

        void deleteTimeLineSicker(String id);

        void showTimeLineSickerArrow(String id);

        void modifyTimeLineSickerPath(String id, String path, StickerView stickerView);

        void stickerFragmentClose();

        void showLoadingDialog();

        void dismissLoadingDialog();

        void setDialogProgress(String title, int dialogProgress, String content);

        void chooseFrame(String path);

        void dismissStickerFrame();

        void dismissTextStickerFrame();

        void clearCheckBox();

        void chooseCheckBox(int i);

        void returnStickerTypeList(ArrayList<StickerTypeEntity> list);

        void closeAllAnim();
    }

    interface ICreationTemplateMvpModel extends BaseModel {
        void setVideoPath(String path);

    }

}
