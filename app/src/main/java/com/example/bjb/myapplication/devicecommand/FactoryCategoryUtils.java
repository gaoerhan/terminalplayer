package com.example.bjb.myapplication.devicecommand;

import android.util.Log;




public class FactoryCategoryUtils {
    public static final int OTHER = 0;

    public static final int AIDIWEI = 4;
    public static final int SHIXIN_RK3188 = 5;
    public static final int SHIXIN_RK3288 = 6;
    public static final int SHIXIN_AW350 = 7;
    public static final int NEW_CHANGSHANG = 8;
    private static String getDeviceno(){
        return android.os.Build.MODEL;
    }

    public static int getFactoryCategory(){
        String deviceNo = getDeviceno();
        Log.e("player","设备型号:" + deviceNo);
        if(deviceNo.equalsIgnoreCase("rk30sdk") || deviceNo.contains("rk3399")){
            return AIDIWEI;
        }else if(deviceNo.equalsIgnoreCase("rk3188")){
            Log.e("player","版本号:" + ZhongFuMethod.getAndroidDisplay());
            if(ZhongFuMethod.getAndroidDisplay().contains("eng.root")){
                return AIDIWEI;
            }else {
                return SHIXIN_RK3188;
            }
        } else if(deviceNo.equalsIgnoreCase("rk3288")){
            Log.e("player","版本号:" + ZhongFuMethod.getAndroidDisplay());
            if(ZhongFuMethod.getAndroidDisplay().contains("server")){
                return NEW_CHANGSHANG;
            }else if(ZhongFuMethod.getAndroidDisplay().contains("root")){
                return AIDIWEI;
            }else {
                return SHIXIN_RK3288;
            }
        }else if(deviceNo.equalsIgnoreCase("softwinerevb")){
            return SHIXIN_AW350;
        }
        return OTHER;

    }

}
