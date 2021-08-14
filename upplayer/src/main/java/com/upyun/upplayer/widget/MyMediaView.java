package com.upyun.upplayer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.upyun.upplayer.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class MyMediaView extends UpVideoView implements ViewTreeObserver.OnGlobalLayoutListener{

    UpVideoView upVideoView;
    private String path;
    private boolean isActivate = true;
    private int w, h;
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

    public MyMediaView(Context context, final String path, int w, int h) {
        super(context);
        this.mContext = context;
        this.path = path;
        this.w = w;
        this.h = h;

        this.setVideoPath(path);
        this.start();


    }





    public void toggle(View view) {

        if (upVideoView.isPlaying()) {

            //暂停播放
            upVideoView.pause();

        } else {

            //开始播放
            upVideoView.start();
        }
    }

    public void refresh(String newPath) {
        upVideoView.setVideoPath(newPath);
        upVideoView.start();

    }

    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到图片，并获取宽与高
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(
                    w, h);
            lp1.setMargins(0, 0, 0, 0);
            this.setLayoutParams(lp1);
        }
    }
}
