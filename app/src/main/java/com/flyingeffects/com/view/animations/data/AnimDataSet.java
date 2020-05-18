package com.flyingeffects.com.view.animations.data;


import com.flyingeffects.com.view.animations.AnimComposer;
import com.flyingeffects.com.view.animations.beans.AnimElement;
import com.flyingeffects.com.view.animations.beans.AnimEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//剪映动画
public class AnimDataSet {
    //第二版剪映动画
    public static List<AnimComposer> generateV2DataSet(){
        List<AnimComposer> allAnims=new ArrayList<>();
        AnimComposer composer;
        AnimElement element;

        //无动画
        composer=new AnimComposer("无动画",-1);
        allAnims.add(composer);

        //抖入放大
        composer=new AnimComposer("哆嗦放大",10);
        element=new AnimElement(1,20,10,-10,new float[]{0.33f,0.00f,0.67f,1.00f}, AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(20,30,-10,-1,new float[]{0.33f,0.00f,0.68f,0.76f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,40,-1,2,new float[]{0.35f,0.80f,0.69f,1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(40,50,2,0,new float[]{0.40f,0.12f,0.75f,1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,50,1.5f,1f,new float[]{0.33f,0.00f,0.36f,0.92f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(50,60,1f,1f,new float[]{0.33f,0.00f,0.83f,1.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(60,90,1f,2f,new float[]{0.17f,0.00f,0.99f,0.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(1,10,0,750,new float[]{0.19f,0.07f,0.64f,1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(10,20,750,490,new float[]{0.27f,0.01f,0.88f,0.99f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(20,30,490,600,new float[]{0.13f, 0.00f, 0.61f, 0.86f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,50,600,540,new float[]{0.36f, 0.00f, 0.79f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //降落旋转
        composer=new AnimComposer("向下旋转",2);
        element=new AnimElement(1,60,0,540,new float[]{0.01f, 0.92f, 0.54f, 0.93f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,90,0,-90,new float[]{0.38f, 0.03f, 0.87f, 0.01f},AnimEnum.Rotate);
        composer.add(element);
        allAnims.add(composer);

        //轻微抖动
        composer=new AnimComposer("轻微哆嗦",17);
        element=new AnimElement(1,15,10,-5,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,-5,4,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,4,-2,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,-2,1,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,75,1,-0.5f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(75,90,-0.5f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,500,550,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,550,500,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,500,500,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,550,550,530,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,530,530,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,550,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(1,15,600,500,new float[]{0.33f, 0.00f, 0.57f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,500,550,new float[]{0.37f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,550,500,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,500,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,550,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,520,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //上下抖动
        composer=new AnimComposer("上下哆嗦",18);
        element=new AnimElement(1,90,1.5f,1f,new float[]{0.16f, 1.00f, 0.67f, 1.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(1,15,1000,420,new float[]{0.33f, 0.00f, 0.62f, 0.97f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,420,570,new float[]{0.49f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,570,506,new float[]{0.17f, 0.00f, 0.64f, 0.94f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,506,550,new float[]{0.37f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,550,530,new float[]{0.17f, 0.00f, 0.65f, 0.98f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,530,540,new float[]{0.25f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);

        //缩放
        composer=new AnimComposer("单纯缩放",7);
        element=new AnimElement(1,45,2f,1f,new float[]{0.01f, 1.00f, 0.67f, 1.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(45,90,1f,2f,new float[]{0.33f, 0.00f, 0.99f, 0.00f},AnimEnum.Zoom);
        composer.add(element);
        allAnims.add(composer);

        //缩小旋转
        composer=new AnimComposer("缩放旋转",4);
        element=new AnimElement(1,30,1.5f,1f,new float[]{0.01f, 0.99f, 0.67f, 1.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(30,90,0,90,new float[]{0.54f, 0.01f, 0.99f, 0.09f},AnimEnum.Rotate);
        composer.add(element);
        allAnims.add(composer);

        //下降向左
        composer=new AnimComposer("向下向左",9);
        element=new AnimElement(1,30,0,540,new float[]{0.01f, 0.93f, 0.40f, 0.97f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,90,540,0,new float[]{0.29f, 0.00f, 0.99f, 0.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向下甩入
        composer=new AnimComposer("向下哆嗦",12);
        element=new AnimElement(1,15,15,-10,new float[]{0.24f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,-10,5,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,5,-2,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,-2,1,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,70,1,-0.5f,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(70,80,-0.5f,0.25f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(80,90,0.25f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,0,600,new float[]{0.02f, 0.17f, 0.64f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,600,536,new float[]{0.32f, 0.02f, 0.76f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,536,570,new float[]{0.27f, 0.01f, 0.79f, 0.99f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,570,530,new float[]{0.16f, 0.00f, 0.86f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,70,530,550,new float[]{0.20f, 0.00f, 0.87f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(70,80,550,535,new float[]{0.16f, 0.00f, 0.89f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(80,90,535,540,new float[]{0.17f, 0.00f, 0.96f, 0.83f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);

        //向右上甩入
        composer=new AnimComposer("右上哆嗦",15);
        element=new AnimElement(1,15,15,-8,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,-8,4,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,4,-2,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,-2,1,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,75,1,-0.5f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(75,90,-0.5f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,1000,448,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,448,564,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,564,506,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,506,558,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,558,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,540,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(1,15,0,596,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,596,496,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,496,544,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,544,528,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,528,542,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,542,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向右甩入
        composer=new AnimComposer("向右哆嗦",11);
        element=new AnimElement(1,15,-15,10,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,10,-5,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,-5,2,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,2,-1,new float[]{0.33f, 0.00f, 0.67f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,70,-1,0.5f,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(70,80,0.5f,-0.25f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(80,90,-0.25f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,-300,658,new float[]{0.29f, 0.09f, 0.60f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,658,494,new float[]{0.31f, 0.03f, 0.64f, 0.96f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,494,560,new float[]{0.24f, 0.00f, 0.38f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,560,520,new float[]{0.29f, 0.00f, 0.70f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,70,520,550,new float[]{0.00f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(70,80,550,530,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(80,90,530,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向右下甩入
        composer=new AnimComposer("右下哆嗦",13);
        element=new AnimElement(1,15,-15,8,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,8,-4,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,-4,2,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,2,-1,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,75,-1,0.5f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(75,90,0.5f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,0,552,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,552,488,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,488,560,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,560,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,520,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,550,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(1,15,0,592,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,592,488,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,488,560,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,560,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,520,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,550,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向左上甩入
        composer=new AnimComposer("左上哆嗦",14);
        element=new AnimElement(1,15,15,-8,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,-8,4,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,4,-2,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,-2,1,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,75,1,-0.5f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(75,90,-0.5f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,1000,336,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,336,568,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,568,510,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,510,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,550,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,520,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(1,15,1000,380,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,380,576,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,576,522,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,522,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,550,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,520,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向左下降
        composer=new AnimComposer("向左向下",8);
        element=new AnimElement(1,30,1080,540,new float[]{0.01f, 0.90f, 0.63f, 0.98f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,90,540,1080,new float[]{0.30f, 0.00f, 0.99f, 0.06f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);

        //向左下甩入
        composer=new AnimComposer("左下哆嗦",16);
        element=new AnimElement(1,15,15,-8,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(15,30,-8,4,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,45,4,-2,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(45,60,-2,1,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(60,75,1,0.5f,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(75,90,0.5f,0,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(1,15,0,588,new float[]{0.33f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,588,496,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,496,518,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,518,554,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,554,550,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,550,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(1,15,1000,432,new float[]{0.33f, 0.00f, 0.57f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,432,580,new float[]{0.37f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,580,556,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,556,524,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,524,520,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,520,540,new float[]{0.17f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //旋转降落
        composer=new AnimComposer("旋转向下",1);
        element=new AnimElement(1,30,-90,0,new float[]{0.14f,1.00f,0.67f,1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,90,540,1080,new float[]{0.22f,0.00f,0.99f,0.12f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);

        //旋转缩小
        composer=new AnimComposer("旋转缩放",3);
        element=new AnimElement(1,30,90,0,new float[]{0.01f,1.00f,0.67f,1.00f},AnimEnum.Rotate);
        composer.add(element);
        element=new AnimElement(30,90,1f,0.5f,new float[]{0.33f,0.00f,0.99f,0.00f},AnimEnum.Zoom);
        composer.add(element);
        allAnims.add(composer);

        //左右抖动
        composer=new AnimComposer("左右哆嗦",19);
        element=new AnimElement(1,90,1.5f,1.0f,new float[]{0.16f,1.00f,0.67f,1.00f},AnimEnum.Zoom);
        composer.add(element);
        element=new AnimElement(1,15,1000,412,new float[]{0.33f, 0.00f, 0.43f, 0.98f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(15,30,412,565,new float[]{0.45f, 0.02f, 0.68f, 0.99f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(30,45,565,518,new float[]{0.33f, 0.03f, 0.71f, 1.00f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(45,60,518,550,new float[]{0.28f, 0.00f, 0.59f, 0.98f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(60,75,550,535,new float[]{0.33f, 0.09f, 0.72f, 0.97f},AnimEnum.X);
        composer.add(element);
        element=new AnimElement(75,90,535,540,new float[]{0.35f, 0.00f, 0.83f, 1.00f},AnimEnum.X);
        composer.add(element);
        allAnims.add(composer);

        //向上幻影
        composer=new AnimComposer("向上幻影",20);
        composer.setMaxDurationMs(500);
        element=new AnimElement(1,90,6f,1f,new float[]{0.16f, 1.00f, 0.67f, 1.00f},AnimEnum.ZoomY);
        composer.add(element);
        element=new AnimElement(1,15,1000,490,new float[]{0.33f, 0.00f, 0.62f, 0.96f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,490,582,new float[]{0.49f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,582,520,new float[]{0.17f, 0.00f, 0.64f, 0.94f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,520,550,new float[]{0.37f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,550,530,new float[]{0.17f, 0.00f, 0.65f, 0.98f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,530,540,new float[]{0.25f, 0.00f, 0.83f, 1.00f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);
        //向下幻影
        composer=new AnimComposer("向下幻影",21);
        composer.setMaxDurationMs(500);
        element=new AnimElement(1,90,6f,1f,new float[]{0.17f, 0.08f, 0.83f, 0.92f},AnimEnum.ZoomY);
        composer.add(element);
        element=new AnimElement(1,15,0,600,new float[]{0.02f, 0.13f, 0.64f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(15,30,600,528,new float[]{0.32f, 0.09f, 0.76f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(30,45,528,558,new float[]{0.38f, 0.00f, 0.86f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(45,60,558,538,new float[]{0.20f, 0.02f, 0.87f, 0.99f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(60,75,538,543,new float[]{0.16f, 0.00f, 0.89f, 1.00f},AnimEnum.Y);
        composer.add(element);
        element=new AnimElement(75,90,543,540,new float[]{0.17f, 0.01f, 0.91f, 0.98f},AnimEnum.Y);
        composer.add(element);
        allAnims.add(composer);

        //普通放大（老特效）
        composer=new AnimComposer("普通放大",6);
        element=new AnimElement(1,90,1f,2f,new float[]{0.38f, 0.71f, 0.67f, 1.00f},AnimEnum.Zoom);
        composer.add(element);
        allAnims.add(composer);

        //普通缩小(老特效)
        composer=new AnimComposer("普通缩小",5);
        element=new AnimElement(1,90,1f,0.5f,new float[]{0.48f, 0.07f, 0.90f, -0.02f},AnimEnum.Zoom);
        composer.add(element);
        allAnims.add(composer);

        //根据ID从小到大排序
        Collections.sort(allAnims, (o1, o2) -> o1.getId()-o2.getId());
        return allAnims;
    }
}
