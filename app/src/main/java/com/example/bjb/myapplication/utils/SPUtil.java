package com.example.bjb.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bjb.myapplication.MyApplication;




/**
 * Created by Administrator on 2016/7/4.
 */
public class SPUtil {
    private final String FILENAME = "sc_post.TP";
    private static SPUtil INSTANCE;
    private SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    /** 保证只有一个实例 */
    private SPUtil() {
    }
    /** 获取实例 ,单例模式 */
    public static SPUtil getInstance() {
        if(INSTANCE == null)INSTANCE = new SPUtil();
        return INSTANCE;
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defval){
        return sharedPreferences.getString(key,defval).trim();
    }
}
