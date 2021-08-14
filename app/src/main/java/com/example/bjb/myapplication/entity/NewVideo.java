package com.example.bjb.myapplication.entity;

import java.util.List;

public class NewVideo  {

    private List<String> rawPathList;
    private byte silence; //1 静音 0 不静音
    private String playerType; // "media player" "flash player" "html5 player"
    private byte panel;
    private int playTime;
    private boolean isBoxPlay;

    private int left;
    private int top;
    private int width;
    private int height;
    private int index; //层叠级别



    public List<String> getRawPathList() {
        return rawPathList;
    }

    public void setRawPathList(List<String> rawPathList) {
        this.rawPathList = rawPathList;
    }

    public byte getSilence() {
        return silence;
    }

    public void setSilence(byte silence) {
        this.silence = silence;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public byte getPanel() {
        return panel;
    }

    public void setPanel(byte panel) {
        this.panel = panel;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public boolean isBoxPlay() {
        return isBoxPlay;
    }

    public void setBoxPlay(boolean boxPlay) {
        isBoxPlay = boxPlay;
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
        return "NewVideo{" +
                "rawPathList=" + rawPathList +
                ", silence=" + silence +
                ", playerType='" + playerType + '\'' +
                ", panel=" + panel +
                ", playTime=" + playTime +
                ", isBoxPlay=" + isBoxPlay +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                ", index=" + index +
                '}';
    }
}
