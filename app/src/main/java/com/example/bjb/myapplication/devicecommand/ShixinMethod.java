package com.example.bjb.myapplication.devicecommand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.view.View;

import com.example.bjb.myapplication.MyApplication;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ShixinMethod {

    public void setScreenOn() {
        Intent i = new Intent("android.action.adtv.wakeup");
        MyApplication.getInstance().sendBroadcast(i);
    }

    public void setScreenOff() {
        Intent i = new Intent("android.action.adtv.sleep");
        MyApplication.getInstance().sendBroadcast(i);
    }

    public void reboot() {
        Intent i = new Intent("android.intent.action.reboot");
        MyApplication.getInstance().sendBroadcast(i);
    }

    public void openWatchDog() {
        Intent intent = new Intent("android.intent.action.watchdog");
        intent.putExtra("val", 1);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    public void closeWatchDog() {
        Intent intent = new Intent("android.intent.action.watchdog");
        intent.putExtra("val",0 );//关闭watchdog，系统开始喂狗
        MyApplication.getInstance().sendBroadcast(intent);
    }
    public void feedWatchDog() {
        Intent intent = new Intent("android.intent.action.watchdog");
        intent.putExtra("val",2 );//关闭watchdog，系统开始喂狗
        MyApplication.getInstance().sendBroadcast(intent);
    }

    public void setPowerOff() {
        Intent i = new Intent("android.intent.action.shutdown");
        MyApplication.getInstance().sendBroadcast(i);
    }

    public boolean setSoundOn() {
        // setStreamMute取消静音有问题
        // 第一 mute了多少次就需要unmute多少次
        // 第二 unmute后音量可能为0 需要自己设置音量
        AudioManager am = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        int vol = 0;
        int i;
        for (i = 0; i < 500; i++) {
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
            am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            am.setStreamMute(AudioManager.STREAM_ALARM, false);
            am.setStreamMute(AudioManager.STREAM_DTMF, false);
            am.setStreamMute(AudioManager.STREAM_RING, false);
            vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (vol == 0) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {}
            } else {
                break;
            }
        }
        if (i < 500) {
            return true;
        } else {
            return false;
        }
    }

    public boolean setSoundOff() {
        // 可以考虑静音前判断下当前是否已经是静音状态
        AudioManager am = (AudioManager)MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
        am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        am.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        am.setStreamMute(AudioManager.STREAM_ALARM, true);
        am.setStreamMute(AudioManager.STREAM_DTMF, true);
        am.setStreamMute(AudioManager.STREAM_RING, true);
        return true;
    }

    public void restart() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected void showBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
