package com.example.bjb.myapplication.devicecommand;

import android.content.Intent;

import com.example.bjb.myapplication.MyApplication;

public class ZhongFuMethod {
    private static String STATICIP = "StaticIp";
    private static String DHCP = "DHCP";
    private static String IPADDR = "192.168.0.123";
    private static String MASK = "255.255.255.0";
    private static String GATEWAY = "192.168.0.1";
    private static String DNS1 = "192.168.0.1";
    private static String DNS2 = "0.0.0.0";

    //获取api版本
    public static String getApiVersion(){
        return MyApplication.getMyManager().getApiVersion();
    }
    //获取当前设备类型
    public static String getAndroidModle(){
        return MyApplication.getMyManager().getAndroidModle();
    }
    //获取Android版本
    public  static String getAndroidVersion(){
        return MyApplication.getMyManager().getAndroidVersion();
    }

    //获取硬件内存大小
    public static String getRunningMemory(){
        return MyApplication.getMyManager().getRunningMemory();
    }
    //获取内部存储容量
    public static String getInternalStorageMemory(){
        return MyApplication.getMyManager().getInternalStorageMemory();
    }
    //获取固件sdk版本
    public static String getFirmwareVersion(){
        return MyApplication.getMyManager().getFirmwareVersion();
    }

    //获取固件内核版本
    public static String getKernelVersion(){
        return MyApplication.getMyManager().getKernelVersion();
    }

    //获取设备固件版本号
    public static String getAndroidDisplay(){
        return MyApplication.getMyManager().getAndroidDisplay();
    }


    //获取固件编译日期

    public static String getFirmwareDate(){
        return MyApplication.getMyManager().getFirmwareDate();
    }

    //------------------------------
    //关机
    public static void shutdown(){
        MyApplication.getMyManager().shutdown();
    }


    //重启
    public static void reboot(){
        MyApplication.getMyManager().reboot();
    }



    //-------------------------------


    //判断导航栏是否隐藏 true为隐藏

    public static boolean getNavBarHideState(){
        return MyApplication.getMyManager().getNavBarHideState();
    }

    //隐藏像是导航栏

    public static void hideNavBar(){
        MyApplication.getMyManager().hideNavBar(MyApplication.getMyManager().getNavBarHideState());
    }

     //判断画出导航栏是否打开

    public static boolean isSlideShowNavBarOpen(){
        return MyApplication.getMyManager().isSlideShowNavBarOpen();
    }

    //打开关闭画出导航栏

    public static void setSlideShowNavBar(){
        MyApplication.getMyManager().setSlideShowNavBar(MyApplication.getMyManager().isSlideShowNavBarOpen());
    }

     //判断滑出通知栏是否打开
    public static boolean isSlideShowNotificationBarOpen(){
        return MyApplication.getMyManager().isSlideShowNotificationBarOpen();
    }

    //打开或关闭画出通知栏

    public static void setSlideShowNotificationBar(){
        MyApplication.getMyManager().setSlideShowNotificationBar(MyApplication.getMyManager().isSlideShowNotificationBarOpen());
    }

    //截屏 ,路径自己定义
    public static void takeScreenshot(String pathName){
        MyApplication.getMyManager().takeScreenshot(pathName);
    }

    //获取屏幕宽高

    public static String getScreenWidthAndHeight(){
        int width = MyApplication.getMyManager().getScreenWidth();
        int height = MyApplication.getMyManager().getScreenHeight();
        return "Width = " + width + " height = " + height;
    }

     //开背光
    public static void turnOnBacklight(){
        MyApplication.getMyManager().turnOnBacklight();
    }


    //关背光
    public static void turnOffBacklight(){
        MyApplication.getMyManager().turnOffBacklight();
    }


    //旋转90度

    public static void rotateScreen_90(){
        MyApplication.getMyManager().rotateScreen(MyApplication.getInstance(),"90");
    }

    //旋转0度
    public static void rotateScreen_0(){
        MyApplication.getMyManager().rotateScreen(MyApplication.getInstance(),"0");
    }

    //进入Recovery模式
    public static void rebootRecovery(){
        MyApplication.getMyManager().rebootRecovery();
    }

    //静默安装

    public static void silentInstallApk(){
        MyApplication.getMyManager().silentInstallApk("/mnt/internal_sd/AdvertisingClient_signed.apk");
    }

    //--------------------

    //获取以太网MAC地址
    public static String getEthMacAddress(){
        return MyApplication.getMyManager().getEthMacAddress();
    }

    //设置以太网MAC地址

    public static void setEthMacAddress(){
        MyApplication.getMyManager().setEthMacAddress("ee4e592090cf");
    }

    //获取以太网IP

    public static String getDhcpIpAddress(){
        if(DHCP.equals(MyApplication.getMyManager().getEthMode())){
            return MyApplication.getMyManager().getDhcpIpAddress();
        }else {
            return MyApplication.getMyManager().getStaticEthIPAddress();
        }
    }

    //设置以太网

    public static void setStaticEthIPAddress(){
        MyApplication.getMyManager().setStaticEthIPAddress(IPADDR,GATEWAY,MASK,DNS1,DNS2);
    }

    //打开以太网
    public static void ethEnabledTrue(){
        MyApplication.getMyManager().ethEnabled(true);
    }

    //关闭以太网
    public static void ethEnabledFalse(){
        MyApplication.getMyManager().ethEnabled(false);
    }

    //设置以太网为动态

    public static void setDhcpIpAddress(){
        MyApplication.getMyManager().setDhcpIpAddress(MyApplication.getInstance());
    }


    //-----------

    //外置SD卡路径

    public static String getSDcardPath(){
        return MyApplication.getMyManager().getSDcardPath();
    }
    //U盘路径
    public static String getUSBStoragePath(){
        return MyApplication.getMyManager().getUSBStoragePath(0);
    }

    //卸载外部存储
    public static void unmountVolume(){
        String USBStorage = "/mnt/usb_storage/USB_DISK1";
        MyApplication.getMyManager().unmountVolume(USBStorage);
    }

    //挂载外部存储
    public static void mountVolume(){
        String USBStorage1 = "/mnt/usb_storage/USB_DISK1";
        MyApplication.getMyManager().mountVolume(USBStorage1);
    }

    //读到EEPROM
    public static String readEEPRom(){
        return MyApplication.getMyManager().readEEPRom();
    }


    //写EEPROM
    public static void writeEEPRom(){
        MyApplication.getMyManager().writeEEPRom("12345678无是谁dmd");
    }

    //设置时间

    public static void setTime(){
        MyApplication.getMyManager().setTime(2017, 3, 16, 17, 44);
    }

     //上网类型

    public static int getCurrentNetType(){
        return MyApplication.getMyManager().getCurrentNetType();
    }

    //屏幕数量

    public static int getScreenNumber(){
        return MyApplication.getMyManager().getScreenNumber();
    }
    //是否有HDMI输出

    public static int getHdmiinStatus(){
        return MyApplication.getMyManager().getHdmiinStatus();
    }



    //定时开关机   注意：该方法同样适用于只设置开机时间，只需将关机时间传0即可，

    public static void setPowerOnOff(int[] powerOnTime, int[] powerOffTime){
        MyApplication.getPowerOnOffManager().setPowerOnOff(powerOnTime,powerOffTime);
    }

    //星期设置开关机时间

    public static void setPowerOnOffWithWeekly(int[] powerOnTime, int[] powerOffTime, int[] weekdays){
        MyApplication.getPowerOnOffManager().setPowerOnOffWithWeekly(powerOnTime,powerOffTime,weekdays);
    }


    //获取当前开机时间

    public static String getPowerOnTime(){
        return MyApplication.getPowerOnOffManager().getPowerOnTime();
    }


    //获取当前关机时间
    public static String getPowerOffTime(){
        return MyApplication.getPowerOnOffManager().getPowerOffTime();
    }



    //获取上次开机时间
    public static String getLastestPowerOnTime(){
        return MyApplication.getPowerOnOffManager().getLastestPowerOnTime();
    }


    //获取上次关机时间

    public static String getLastestPowerOffTime(){
        return MyApplication.getPowerOnOffManager().getLastestPowerOffTime();
    }
    //当前开机模式
    public static String getPowerOnMode(){
        return MyApplication.getPowerOnOffManager().getPowerOnMode();
    }

    //清空时间
    public static void clearPowerOnOffTime(){
        MyApplication.getPowerOnOffManager().clearPowerOnOffTime();
    }


    //定时开关机版本号
    public static String getVersion(){
        return MyApplication.getPowerOnOffManager().getVersion();
    }




    //3128接口-------------------vvvvvvvvvvv

    //关闭滑出导航栏
    public static void closeSlideNavi(){
        Intent intent = new Intent();
        intent.setAction("com.ys.slide.systembar");
        intent.putExtra("barMode","navigationbar");
        intent.putExtra("isSlide",false);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //打开滑出导航栏
    public static void openSlideNavi(){
        Intent intent = new Intent();
        intent.setAction("com.ys.slide.systembar");
        intent.putExtra("barMode","navigationbar");
        intent.putExtra("isSlide",true);
        MyApplication.getInstance().sendBroadcast(intent);
    }


    //.打开滑出通知栏
    public static void openSlideNoti(){
        Intent intent = new Intent();
        intent.setAction("com.ys.slide.systembar");
        intent.putExtra("barMode","notificationbar");
        intent.putExtra("isSlide",true);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //关闭滑出通知栏
    public static void closeSlideNoti(){
        Intent intent = new Intent();
        intent.setAction("com.ys.slide.systembar");
        intent.putExtra("barMode","notificationbar");
        intent.putExtra("isSlide",false);
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //隐藏导航栏
    public static void hideNavi(){
        Intent intent = new Intent();
        intent.setAction("android.action.adtv.hideNavigationBar");
        MyApplication.getInstance().sendBroadcast(intent);
    }

    //显示导航栏
    public static void showNavi(){
        Intent intent = new Intent();
        intent.setAction("android.action.adtv.showNavigationBar");
        MyApplication.getInstance().sendBroadcast(intent);
    }
}
