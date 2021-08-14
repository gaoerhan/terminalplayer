package com.example.bjb.myapplication.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.MediaController;


import com.example.bjb.myapplication.entity.NewVideo;
import com.example.bjb.myapplication.utils.LogUtils;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;

import java.util.ArrayList;


/**
 * 视频播放控件   LYVideoView
 * 使用内置的MediaPlayer播放,仅android默认支持的格式
 * 详见: https://developer.android.com/guide/appendix/media-formats.html
 */
public class NewVideoPlayer extends NoDialogVideoView implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, ViewerCallback {
    private int isonepage = 0;
    private ArrayList<String> playlist;
    private boolean silence;
    private boolean panel;
    private int index;
    private boolean lopping;
    private int width;
    private int height;
    private ViewGroup mGroup;

    private MediaPlayer mediaPlayer;

    private OnVideoPlayerSeektoCompleteListener onVideoPlayerSeektoCompleteListener;

    private boolean isCircleplay = true;

    public void setOnVideoPlayerSeektoCompleteListener(OnVideoPlayerSeektoCompleteListener onVideoPlayerSeektoCompleteListener) {
        this.onVideoPlayerSeektoCompleteListener = onVideoPlayerSeektoCompleteListener;
    }

    public NewVideoPlayer(final Context context, NewVideo video, ViewGroup group) {
        super(context);
        silence = video.getSilence() == 1;
        panel = video.getPanel() == 1;
        playlist = new ArrayList<String>(video.getRawPathList());
        width = video.getWidth();
        height = video.getHeight();
        Log.e("video","下发视频的width:  " + width + "----" + "高height:  " + height);

        if (panel) {
            this.setMediaController(new MediaController(context));
        }
        this.setOnPreparedListener(this);
        this.setOnCompletionListener(this);
        this.setOnErrorListener(this);
        this.setBackgroundResource(android.R.color.transparent);

        play();
    }

    public NewVideoPlayer(Context context, NewVideo ele, ViewGroup group, int isonepage) {
        this(context, ele, group);
        this.isonepage = isonepage;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 只有这样才能伸展到设定的大小
        setMeasuredDimension(width, height);
    }

    public void play() {
        Log.i("HJ", "playlist.size " + playlist.size());
        if (playlist.size() == 0) return;
        if (playlist.size() == 1) {
            this.lopping = true;//此板卡出现重复播放的时候出现卡顿

        } else {
            this.lopping = false;
        }
        try {
            LogUtils.i("HJ", "video path: " + playlist.get(this.index));
            this.setVideoPath(playlist.get(this.index));
            this.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void playNext() {
        try {
            LogUtils.i("HJ", "video path: " + playlist.get(this.index));
            this.setVideoPath(playlist.get(this.index));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        try {
            Log.i("HJ", "onCompletion");
            mediaPlayer.release();
            mediaPlayer = null;
            if (this.mediaPlayer != null) this.mediaPlayer.release();
            this.mediaPlayer = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(isCircleplay){
            index++;
        }else {

        }

        if (index >= playlist.size()) {
            //TODO 此时所有视频播放完成
                index = 0;
                playNext();//此时播放的是默认垫片 无法切换节目

        } else {
            playNext();
        }
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        Log.i("HJ", "onPrepared");
        try {
            if (this.mediaPlayer != null) this.mediaPlayer.release();
            this.mediaPlayer = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.mediaPlayer = mediaPlayer;
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if(onVideoPlayerSeektoCompleteListener!=null){
                    onVideoPlayerSeektoCompleteListener.onSeektoComplete(mp);
                }

            }
        });

        if (isonepage == 0) {

        }
        if (playlist.size() == 1) {

        }
        if (silence) {
            mediaPlayer.setVolume(0, 0);
        }
        mediaPlayer.setLooping(this.lopping);
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        String s = String.format("播放错误_VideoPlayer_onError:%d, %d", i, i2);
        try {
            mediaPlayer.release();
            mediaPlayer = null;
            if (this.mediaPlayer != null) this.mediaPlayer.release();
            this.mediaPlayer = null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void viewerOnPause(boolean isFinishing) {
        try {
            if (isFinishing) {
                this.stopPlayback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void viewerOnResume() {
    }

    @Override
    public void viewerOnDestroy() {
        try {
            if (mediaPlayer != null) mediaPlayer.release();
            mediaPlayer = null;
            this.stopPlayback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewerOnDestroy();
    }




    //获取当前进度
    public int getProgress(){
        return mediaPlayer.getCurrentPosition();
    }

    //设置进度
    public void setPrpgress(int progress){
        mediaPlayer.seekTo(progress);
    }

    //获取总进度
    public int getTotalProgress(){
        return mediaPlayer.getDuration();
    }

    //重新播放
    public void  startPlay(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    //暂停播放
    public void pausePlay(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }


    public interface OnVideoPlayerSeektoCompleteListener{
        void onSeektoComplete(MediaPlayer player);
    }


    //列表尺寸
    public int getPlayListSize(){
        return playlist.size();
    }


    //播放某一个视频
    public void playMyNext(int newIndex){
        try {
            Log.i("HJ", "onCompletion");
            mediaPlayer.release();
            mediaPlayer = null;
            if (this.mediaPlayer != null) this.mediaPlayer.release();
            this.mediaPlayer = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.index = newIndex;
        playNext();
    }


    //获取当前视频索引
    public int getCurrentVideoIndex(){
        return index;
    }


    public void setCircleplay(){
        isCircleplay = true;
    }

    public void stopCircleplay(){
        isCircleplay = false;
    }
}
