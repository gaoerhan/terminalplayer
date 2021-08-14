package com.example.bjb.myapplication.utils;


import android.content.res.Resources;

/**
 * Created by Administrator on 2016/7/4.
 */
public class UIUtil {
    	public static int px2dip(int pxValue)
	{
		final float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	public static float dip2px(float dipValue)
	{
		final float scale = Resources.getSystem().getDisplayMetrics().density;
		return  (dipValue * scale + 0.5f);
	}

}
