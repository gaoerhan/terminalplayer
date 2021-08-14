package com.example.bjb.myapplication;

import android.content.Context;
import android.util.Log;


import com.example.bjb.myapplication.common.SgdsConst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;


/**
 * 程序崩溃对应的handler
 * 
 * @author zhuweiwei
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private boolean testing = true; //调试模式 只记录crash.log 使用系统默认崩溃函数处理未捕获的异常

	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
		
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);

		/* 关闭调试模式 调试时请注释掉此行 */
		testing = false;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e("dsandroid", "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 使用Toast来显示异常信息
		/*new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将重启.", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();*/

		if (testing) {
			printException(ex);
			saveCrashLog(ex);
			return true;
		} else {
			saveCrashLog(ex);
			return false;
		}
	}

	private void printException(Throwable ex) {
		Throwable cause = ex;
		while (cause != null) {
			cause.printStackTrace();
			cause = cause.getCause();
		}
	}

	private void saveCrashLog(Throwable ex) {
		Throwable cause = ex;
		StringBuilder sb = new StringBuilder();
		while (cause != null) {
			cause.printStackTrace();
			sb.append(getCauseMessage(cause));
			cause = cause.getCause();
		}

		File f = new File(SgdsConst.CrashLogFile());
		try {
			FileOutputStream outStream = new FileOutputStream(f);
			outStream.write(sb.toString().getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getCauseMessage(Throwable cause) {
		StringBuilder sb = new StringBuilder();
		sb.append(cause.toString() + "\r\n");
		StackTraceElement[] causes = cause.getStackTrace();
		for (StackTraceElement stack : causes) {
			sb.append("\tat " + stack.toString() + "\r\n");
		}
		return sb.toString();
	}

}
