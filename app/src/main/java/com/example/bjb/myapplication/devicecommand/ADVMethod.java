package com.example.bjb.myapplication.devicecommand;

import android.content.Intent;
import android.util.Log;

import com.example.bjb.myapplication.MyApplication;
import com.example.bjb.myapplication.socket.entity.MaterialListBean;


public class ADVMethod {

    //关机
    public static void poweroff(){
        Intent intent = new Intent("android.adw.intent.action.shutdown");
        MyApplication.getInstance().sendBroadcast(intent);

    }
    //重启
    public static void reboot(){
        Intent intent = new Intent("android.adw.intent.action.reboot");
        MyApplication.getInstance().sendBroadcast(intent);

    }

    //休眠
    public static void screenOff(){
        Intent intent = new Intent("android.adw.intent.action.gotosleep");
        MyApplication.getInstance().sendBroadcast(intent);

    }

    //唤醒

    public static void screenOn(){
        Intent intent = new Intent("android.adw.intent.action.wakeup");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //截屏

    public static void screenShot(){
        Intent intent = new Intent("rk.android.screenshot.action");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //隐藏导航栏
    public static void hideStateBar() {
        //隐藏广播
        Intent in = new Intent();
//        in.setAction("elc_hide_systembar");
        in.setAction("com.outform.hidebar");
        MyApplication.getInstance().sendBroadcast(in);

    }
    //显示导航栏
    public static void showStateBar() {
        //显示广播
        Intent in = new Intent();
//        in.setAction("elc_unhide_systembar");
        in.setAction("com.outform.unhidebar");
        MyApplication.getInstance().sendBroadcast(in);

    }


    public static void setADVOnOff(String onDay, String onTime, String offDay, String offTime){
        String[] onDays = onDay.split("-");
        String[] onTimes = onTime.split(":");

        String[] offDays = offDay.split("-");
        String[] offTimes = offTime.split(":");

        Log.e("player","关机日期："+ offDay+ " --- " + "时间： " + offTime);
        Log.e("player","开机日期："+ onDay + " --- " + "时间： " + onTime);
        Intent intent = new Intent("android.56iq.intent.action.setpoweronoff");
        int[] timeonArray1 = {Integer.parseInt(onDays[0]), Integer.parseInt(onDays[1]), Integer.parseInt(onDays[2]), Integer.parseInt(onTimes[0]), Integer.parseInt(onTimes[1])};
        int[] timeoffArray1 = {Integer.parseInt(offDays[0]), Integer.parseInt(offDays[1]), Integer.parseInt(offDays[2]), Integer.parseInt(offTimes[0]), Integer.parseInt(offTimes[1])};

        intent.putExtra("timeon", timeonArray1);
        intent.putExtra("timeoff", timeoffArray1);
        intent.putExtra("enable", true); //定时开关机 //或 intent.putExtra("enable", false); //取消定时开关机 sendBroadcast(intent);
        MyApplication.getInstance().sendBroadcast(intent);
    }


    public static void cancelADVOnOff(){
        Intent intent = new Intent("android.56iq.intent.action.setpoweronoff");
        intent.putExtra("enable", false); //定时开关机 //或 intent.putExtra("enable", false); //取消定时开关机 sendBroadcast(intent);
        MyApplication.getInstance().sendBroadcast(intent);
    }

}
