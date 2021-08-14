package com.example.bjb.myapplication.devicecommand;

import android.content.Intent;

import com.example.bjb.myapplication.MyApplication;

import java.io.IOException;

public class MyShixinMethod {
    //关机
    public void poweroff(){

        MyApplication.getInstance().sendBroadcast(new Intent().setAction("android.intent.action.shutdown" ));
    }
    //重启
    public void reboot(){
        MyApplication.getInstance().sendBroadcast(new Intent().setAction("android.intent.action.reboot" ));
    }

    //休眠
    public void screenOff(){
        Intent intent = new Intent("android.action.adtv.sleep" );
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //唤醒

    public void screenOn(){
        Intent intent = new Intent("android.action.adtv.wakeup" );
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //截屏
    Process su;
    public void screenShot(){
        try {
            su = Runtime.getRuntime().exec("/system/xbin/su");
            String cmd = "screencap /sdcard/Pictures/Screenshots/picture.png"  + "\n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public  void cancelSXOnOff(){
        Intent intent = new Intent("android.intent.action.gz.setpoweronoff");
        intent.putExtra("enable", false); //定时开关机 //或 intent.putExtra("enable", false); //取消定时开关机 sendBroadcast(intent);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    public  void setSXOnOff(String onDay, String onTime, String offDay, String offTime){
        String[] onDays = onDay.split("-");
        String[] onTimes = onTime.split(":");

        String[] offDays = offDay.split("-");
        String[] offTimes = offTime.split(":");

        Intent intent = new Intent("android.intent.action.gz.setpoweronoff");
        int[] timeonArray1 = {Integer.parseInt(onDays[0]), Integer.parseInt(onDays[1]), Integer.parseInt(onDays[2]), Integer.parseInt(onTimes[0]), Integer.parseInt(onTimes[1])};
        int[] timeoffArray1 = {Integer.parseInt(offDays[0]), Integer.parseInt(offDays[1]), Integer.parseInt(offDays[2]), Integer.parseInt(offTimes[0]), Integer.parseInt(offTimes[1])};

//        int[] timeoffArray1 = {2018, 8, 23, 11, 33};
//        int[] timeonArray1 = {2018, 8, 23, 11, 40};
        intent.putExtra("timeon", timeonArray1);
        intent.putExtra("timeoff", timeoffArray1);
        intent.putExtra("enable", true); //定时开关机 //或 intent.putExtra("enable", false); //取消定时开关机 sendBroadcast(intent);
        MyApplication.getInstance().sendBroadcast(intent);
    }
}
