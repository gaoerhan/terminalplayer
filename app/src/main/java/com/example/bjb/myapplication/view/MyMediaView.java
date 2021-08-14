package com.example.bjb.myapplication.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.example.bjb.myapplication.view.callbacks.ViewerCallback;
import com.upyun.upplayer.widget.UpVideoView;



public class MyMediaView extends UpVideoView implements ViewerCallback,ViewTreeObserver.OnGlobalLayoutListener{

    private String path;
    private boolean isActivate = true;
    private int w, h,left,top;
    private Context mContext;
    public MyMediaView(Context context) {
        super(context);
    }

    private boolean mOnce = false;
    public MyMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyMediaView(Context context, String path, int w, int h, int left, int top) {
        super(context);
        this.mContext = context;
        this.path = path;
        this.w = w;
        this.h = h;
        this.left = left;
        this.top = top;

        this.setVideoPath(path);
        this.start();
    }


    @Override
    public void viewerOnPause(boolean isFinishing) {
        this.pause();
    }

    @Override
    public void viewerOnResume() {
        // 重新开始播放器
        this.resume();
//        this.start();
    }

    @Override
    public void viewerOnDestroy() {
        this.release(true);
    }


    public void toggle(View view) {
        if (this.isPlaying()) {
            //暂停播放
            this.pause();
        } else {
            //开始播放
            this.start();
        }
    }

    public void refresh(String  newPath) {
        this.setVideoPath(newPath);
        this.start();
    }


//    public void pause(){
//        this.pause();
//    }


    public void seekto(int progress){
        this.seekTo(progress);
    }


//    public void resume(){
//        this.resume();
//    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到图片，并获取宽与高
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    w, h);
            Log.e("player","宽变了吗:"+ w + "高呢"+ h);
            lp1.setMargins(left, top, 0, 0);
            this.setLayoutParams(lp1);
            mOnce = true;
        }
    }
}
