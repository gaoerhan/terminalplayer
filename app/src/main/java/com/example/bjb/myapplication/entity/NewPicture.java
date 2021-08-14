package com.example.bjb.myapplication.entity;

import java.util.List;

public class NewPicture {


    private int switchTime; //切换时间 s
    private List<String> rawPathList;


    private int left;
    private int top;
    private int width;
    private int height;
    private int index; //层叠级别



    public int getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(int switchTime) {
        this.switchTime = switchTime;
    }



    public List<String> getRawPathList() {
        return rawPathList;
    }

    public void setRawPathList(List<String> rawPathList) {
        this.rawPathList = rawPathList;
    }



    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



    @Override
    public String toString() {
        return "NewPicture{" +
                "switchTime=" + switchTime +
                ", rawPathList=" + rawPathList +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", index=" + index +
                '}';
    }
}
