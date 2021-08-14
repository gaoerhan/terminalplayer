package com.example.bjb.myapplication;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;

import com.example.bjb.myapplication.common.SgdsConst;
import com.example.bjb.myapplication.devicecommand.ADVMethod;
import com.example.bjb.myapplication.devicecommand.FactoryCategoryUtils;
import com.example.bjb.myapplication.devicecommand.MyShixinMethod;
import com.example.bjb.myapplication.devicecommand.NewChangshangMethod;
import com.example.bjb.myapplication.devicecommand.ShixinMethod;
import com.example.bjb.myapplication.httputils.Ok;
import com.example.bjb.myapplication.httputils.callback.CallBack;
import com.example.bjb.myapplication.httputils.callback.FileCallBack;
import com.example.bjb.myapplication.httputils.logInterCeptor.LogInterceptor;
import com.example.bjb.myapplication.httputils.persistentcookiejar.PersistentCookieJar;
import com.example.bjb.myapplication.httputils.persistentcookiejar.cache.SetCookieCache;
import com.example.bjb.myapplication.httputils.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.example.bjb.myapplication.utils.FileUtils;
import com.example.bjb.myapplication.utils.HardwareUtils;
import com.example.bjb.myapplication.utils.SDCardFileUtils;
import com.example.bjb.myapplication.utils.SPUtil;
import com.ys.myapi.MyManager;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import startest.ys.com.poweronoff.PowerOnOffManager;

public class MyApplication extends Application {
    public ShixinMethod shixinMethod = new ShixinMethod();
    public MyShixinMethod myShixinMethod = new MyShixinMethod();


    private Context mContext;
    private static MyApplication app = null;

    public static MyApplication getInstance() {
        return app;
    }


    private static MyManager myManager = null;
    private AudioManager am;

    public static MyManager getMyManager() {
        return myManager;
    }


    private static PowerOnOffManager powerOnOffManager;

    public static PowerOnOffManager getPowerOnOffManager(){
        return powerOnOffManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        mContext = getApplicationContext();

        myManager = MyManager.getInstance(this);
        myManager.bindAIDLService(this);

        powerOnOffManager = PowerOnOffManager.getInstance(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());


         am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);


        //网络访问初始化
        Ok.init(this)
                .connectTimeout(10000l, TimeUnit.MILLISECONDS)
                .readTimeout(10000l, TimeUnit.MILLISECONDS)
                .AppInterceptor("eason", new LogInterceptor())
                .CookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MyApplication.this)))
                .build();
    }


//增大音量
    public void raiseVolume(){

        am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }


    //降低音量

    public void lowVolume(){
        am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 关闭电源
     */
    public void setPowerOff() {
        if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.AIDIWEI) {
            ADVMethod.poweroff();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_AW350) {
            myShixinMethod.poweroff();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.NEW_CHANGSHANG) {
            NewChangshangMethod.setShut_down();
        } else {
            shixinMethod.setPowerOff();

        }

    }


    /**
     * 重启
     * Intent intent = new Intent(Intent.ACTION_REBOOT);
     * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * startActivity(intent);
     */
    public void reboot() {
        if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.AIDIWEI) {
            ADVMethod.reboot();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_AW350) {
            myShixinMethod.reboot();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.NEW_CHANGSHANG) {
            NewChangshangMethod.setmReboot();
        } else {

            shixinMethod.reboot();

        }


    }


    /**
     * 打开屏幕
     */
    @SuppressWarnings("deprecation")
    public void setScreenOn() {
        if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.AIDIWEI) {
            ADVMethod.screenOn();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_AW350) {
            myShixinMethod.screenOn();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.NEW_CHANGSHANG) {
            NewChangshangMethod.setWakeup();
        } else {

            shixinMethod.setScreenOn();
        }
    }


    /**
     * 关闭屏幕
     */
    public void setScreenOff() {

        if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.AIDIWEI) {
            ADVMethod.screenOff();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288 || FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_AW350) {
            myShixinMethod.screenOff();
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.NEW_CHANGSHANG) {
            NewChangshangMethod.setSleep();
        } else {

                    shixinMethod.setScreenOff();

        }

    }






    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        myManager.unBindAIDLService(this);
        super.onTerminate();
    }
}
