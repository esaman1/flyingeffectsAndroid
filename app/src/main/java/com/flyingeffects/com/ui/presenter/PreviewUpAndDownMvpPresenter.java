package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.ui.interfaces.model.PreviewUpAndDownMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.PreviewUpAndDownMvpView;
import com.flyingeffects.com.ui.model.PreviewUpAndDownMvpModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

public class PreviewUpAndDownMvpPresenter extends BasePresenter implements PreviewUpAndDownMvpCallback {

    private PreviewUpAndDownMvpView previewUpAndDownMvpView;
    private PreviewUpAndDownMvpModel previewUpAndDownMvpModel;

    public PreviewUpAndDownMvpPresenter(Context context, PreviewUpAndDownMvpView mvp_view, List<NewFragmentTemplateItem> allData, int nowSelectPage, String fromTo, String templateId, String toUserID, String searchText, boolean isCanLoadMore, String tc_id) {
        this.previewUpAndDownMvpView = mvp_view;
        previewUpAndDownMvpModel = new PreviewUpAndDownMvpModel(context, this,allData,nowSelectPage,fromTo,templateId,toUserID,searchText,isCanLoadMore,tc_id);
    }

    public void toDressUp(String path,String templateId,String templateTitle){
        previewUpAndDownMvpModel.toDressUp(path,templateId,templateTitle);
    }


    public void requestMessageStatistics(String type,String message_id,String template_id){
        previewUpAndDownMvpModel.requestMessageStatistics(type,message_id,template_id);
    }

    public void ToDressUpSpecial(List<String> paths,int api_type,String templateId,String title,String ToDressUpSpecial){

        previewUpAndDownMvpModel.ToDressUpSpecial(paths,api_type,templateId,title,ToDressUpSpecial);
    }

    public void ToTemplateAddStickerActivity(List<String> strToList1,String templateName,String templateId,int api_type,String templateType){
        previewUpAndDownMvpModel.ToTemplateAddStickerActivity(strToList1,templateName,templateId,api_type,templateType);
    }




    public void downZip(String url,String zipPid){
        previewUpAndDownMvpModel.prepareDownZip(url,zipPid);
    }

    public void showBottomSheetDialog(String path, String imagePath, String id, NewFragmentTemplateItem templateItem, String fromTo){
        previewUpAndDownMvpModel.showBottomSheetDialog(path,imagePath,id,templateItem,fromTo);
    }

    public void GetDressUpPath(List<String> paths){
        previewUpAndDownMvpModel.GetDressUpPath(paths);
    }


    public void showCommentBottomSheetDialog(){
      //  previewUpAndDownMvpModel.showBottomSheetDialogForComment();
    }

    public void requestMoreData(){
        previewUpAndDownMvpModel.requestMoreData();
    }


    public void requestTemplateDetail(String templateId){
        previewUpAndDownMvpModel.requestTemplateDetail(templateId);
    }




    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout){
        previewUpAndDownMvpModel.initSmartRefreshLayout(smartRefreshLayout);
    }

    public void requestAD(){
        previewUpAndDownMvpModel.requestAD();
    }


    public void collectTemplate(String templateId,String title,String template_type){
        previewUpAndDownMvpModel.collectTemplate(templateId, title,template_type);
    }

    public void ZanTemplate(String templateId,String title,String template_type){
        previewUpAndDownMvpModel.zanTemplate(templateId, title,template_type);
    }



    public void DownVideo(String path,String imagePath,String id,boolean isFromAgainChooseBj){
        previewUpAndDownMvpModel.downVideo(path,imagePath,id,false,isFromAgainChooseBj);
    }




    @Override
    public void collectionResult(boolean collectionResult) {
        previewUpAndDownMvpView.collectionResult(collectionResult);
    }

    @Override
    public void zanResult() {
        previewUpAndDownMvpView.zanResult();
    }

    @Override
    public void hasLogin(boolean hasLogin) {
        previewUpAndDownMvpView.hasLogin(hasLogin);
    }

    @Override
    public void onclickCollect() {
        previewUpAndDownMvpView.onclickCollect();
    }

    @Override
    public void getDressUpPathResult(List<String> paths) {
        previewUpAndDownMvpView.getDressUpPathResult(paths);
    }

    @Override
    public void downVideoSuccess(String path, String imagePath) {
        previewUpAndDownMvpView.downVideoSuccess(path,imagePath);
    }

    @Override
    public void getTemplateFileSuccess(String filePath) {
        previewUpAndDownMvpView.getTemplateFileSuccess(filePath);
    }

    @Override
    public void showDownProgress(int progress) {
        previewUpAndDownMvpView.showDownProgress(progress);
    }

    @Override
    public void showNewData(List<NewFragmentTemplateItem> allData, boolean isRefresh) {
        previewUpAndDownMvpView.showNewData(allData,isRefresh);
    }

    @Override
    public void resultAd(List<TTNativeExpressAd> ads) {
        previewUpAndDownMvpView.resultAd(ads);
    }

    @Override
    public void getTemplateLInfo(NewFragmentTemplateItem data) {
        previewUpAndDownMvpView.getTemplateInfo(data);
    }

    @Override
    public void getSpliteMusic(String path) {
        previewUpAndDownMvpModel.getBackgroundMusic(path);
    }

    @Override
    public void returnSpliteMusic(String path, String videoPath) {
        previewUpAndDownMvpView.returnSpliteMusic(path,videoPath);
    }

    @Override
    public void shareSaveToAlbum() {
        previewUpAndDownMvpView.shareSaveToAlbum();
    }


    public void requestUserInfo(){
        previewUpAndDownMvpModel.requestUserInfo();
    }








}