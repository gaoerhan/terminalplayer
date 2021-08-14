package com.example.bjb.myapplication.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressLint("DefaultLocale")
public class DsStringUtils {
	
	public static String toGb2312Encode(String s) {
		String r = null;
		if(s != null) {
			try {
				r = URLEncoder.encode(s, "gb2312");
			} catch(Exception e) {
				e.printStackTrace();
				//do nothing
			}
		}
		return r;
	}
	
	public static String toUtf8Encode(String s) {
		String r = null;
		if(s != null) {
			try {
				r = URLEncoder.encode(s, "utf-8");
			} catch(Exception e) {
				e.printStackTrace();
				//do nothing
			}
		}
		return r;
	}
	
	public static String toUtf8Decode(String s) {
		String r = null;
		if (s != null) {
			try {
				r = URLDecoder.decode(s, "utf-8");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return r;
	}
	
	public static String getMd5Value(String toMd5) {
		if (TextUtils.isEmpty(toMd5)) {
			return null;
	    }
		return toString(md5Private(toMd5.getBytes())).toUpperCase();
	}
	
	/**
	 * 进行md5运算
	 * @param cs
	 * @return
	 */
	private static byte[] md5Private(byte[] cs) {
		byte[] rs = null;
	      try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			rs = md.digest(cs);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return rs;
	}
	
	public static String toString(byte[] a) {
        if (a == null)
            return "null";
        if (a.length == 0)
            return "";
 
        StringBuilder buf = new StringBuilder();
 
        for (int i = 0; i < a.length; i++) {
        	if (a[i] < 0)
        		buf.append(Integer.toHexString(a[i]&0xff));
        	else if (a[i] < 16) {
        		buf.append('0');
        		buf.append(Integer.toHexString(a[i]));
        	} else {
        		buf.append(Integer.toHexString(a[i]));
        	}
        }
 
        return buf.toString();
    }

	// 替换字符串中的数字为中文数字 (只对三位以上的纯数字进行替换)
	final static String cns[] = new String[]{"零","一","二","三","四","五","六","七","八","九"};
	public static String convertToChineseNumberSingle(String text) {
		if (text == null) {
			return null;
		}
		// 只对三位以上的纯数字进行替换操作
		if (isNumeric(text) && text.length() > 3) {
			for (int i = 0; i < 10; i++) {
				text = text.replace(String.valueOf(i), cns[i]);
			}
		}
		return text;
	}

	// 判断是否纯数字
	public static boolean isNumeric(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		boolean num = true;
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				num = false;
				break;
			}
		}
		return num;
	}
}
