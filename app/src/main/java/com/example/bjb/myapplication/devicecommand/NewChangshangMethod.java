package com.example.bjb.myapplication.devicecommand;

import android.content.Intent;

import com.example.bjb.myapplication.MyApplication;

public class NewChangshangMethod {
    //截图
    public static void screenshot(){
        Intent intent = new Intent("rk.android.screenshot.action");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //显示导航栏
    public static void showStateBar_new(){
        Intent intent = new Intent("com.android.systembar.show");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //隐藏导航栏
    public static void hideStateBar_new(){
        Intent intent = new Intent("com.android.systembar.hide");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //关机
    public static void setShut_down(){
        Intent intent = new Intent("shutdown.zysd.now");
        MyApplication.getInstance().sendBroadcast(intent);
    }

//重启

    public static void setmReboot(){
        Intent intent = new Intent("reboot.zysd.now");
        MyApplication.getInstance().sendBroadcast(intent);

    }


    //唤醒
    public static void setWakeup(){
        Intent intent=new Intent("zysd.alarm.lvds.poweron");
        MyApplication.getInstance().sendBroadcast(intent);

    }

    public static void setSleep(){

        Intent intent=new Intent("zysd.alarm.lvds.poweroff");
        MyApplication.getInstance().sendBroadcast(intent);

    }


    public static void setmDingshikai(String onDay, String onTime){
        Intent intent = new Intent("zysd.alarm.poweron.time");
        //开机时间为 2017 年 10 月 18 日 17 时 05 分i
        intent.putExtra("poweronday",onDay);//开机日期"2018-06-08"
        intent.putExtra("powerontime",onTime);//开机时间"16:20"
        MyApplication.getInstance().sendBroadcast(intent);

    }

    public static void setGuanji(String offDay, String offTime){
        Intent intent = new Intent("zysd.alarm.poweroff.time");
        intent.putExtra("poweroffday",offDay);//关机日期
        intent.putExtra("powerofftime",offTime);//关机时间
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //取消定时开关机接口
    public static void cancelTimePowerOnOff(){
        Intent intent = new Intent("zysd.alarm.poweroff.cancel ");
        MyApplication.getInstance().sendBroadcast(intent);
    }
}
