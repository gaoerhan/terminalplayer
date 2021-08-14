package com.example.bjb.myapplication.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.example.bjb.myapplication.common.SgdsConst;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * * Android开发调试日志工具类[支持保存到SD卡]<br>
 * * <br>
 * *
 * * 需要一些权限: <br>
 * * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <br>
 * * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" /><br>
 *
 * @author: xiaojian
 * @date: 15-8-27
 * @time: 下午2:57
 * @version: V1.0
 */
@SuppressLint("SimpleDateFormat")
public class LogUtils {

    public static boolean isDebugModel = true;// 是否输出日志
    public static boolean isSaveDebugInfo = true;// 是否保存调试日志
    public static boolean isSaveCrashInfo = true;// 是否保存报错日志
    public static void v(final String tag, final String msg) {
        if (isDebugModel) {
            Log.v(tag, "--> " + msg);
        }
    }

    public static void d(final String tag, final String msg) {
        if (isDebugModel) {
            Log.d(tag, "--> " + msg);
        }
    }

    public static void i(final String tag, final String msg) {
        if (isDebugModel) {
            Log.i(tag, "--> " + msg);
        }
        if (isSaveDebugInfo) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " --> " + msg + "\n");
                }
            }).start();
        }
    }

    public static void w(final String tag, final String msg) {
        if (isDebugModel) {
            Log.w(tag, "--> " + msg);
        }
    }

    /**
     * 调试日志，便于开发跟踪。
     *
     * @param tag
     * @param msg
     */
    public static void e(final String tag, final String msg) {
        if (isDebugModel) {
            Log.e(tag, "--> " + msg);
        }

        if (isSaveDebugInfo) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " --> " + msg + "\n");
                }
            }).start();
        }
    }


    /**
     * try catch 时使用，上线产品可上传反馈。
     *
     * @param tag
     * @param tr
     */
    public static void e(final String tag, final Throwable tr) {
        if (isDebugModel) {
            Log.e(tag, time() + tag + " [CRASH] --> " + getStackTraceString(tr) + "\n");
        }
        if (isSaveCrashInfo) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write(time() + tag + " [CRASH] --> " + getStackTraceString(tr) + "\n");
                }
            }).start();
        }
    }


    /**
     * 获取捕捉到的异常的字符串
     *
     * @param tr
     * @return
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }


    /**
     * 标识每条日志产生的时间
     *
     * @return
     */
    private static String time() {
        return String.format("[%s]", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
    }


    /**
     * 以年月日作为日志文件名称
     *
     * @return
     */
    private static String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }


    /**
     * 保存到日志文件
     *
     * @param content
     */
    public static synchronized void write(String content) {
        try {
            FileWriter writer = new FileWriter(getFile(), true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取日志文件路径
     *
     * @return
     */
    public static String getFile() {
        File sdDir = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            sdDir = Environment.getExternalStorageDirectory();

        File cacheDir = new File(SgdsConst.LogsRootPath());
        if (!cacheDir.exists())
            cacheDir.mkdir();

        File filePath = new File(cacheDir + File.separator + date() + ".txt");

        return filePath.toString();
    }

    /**
     * 按时间删除目录下的文件 删除3天前的日志文件
     *
     * @param dir      将要删除的文件目录
     * @param filetype 要删除的文件类型
     * @return boolean
     */
    public static boolean deleteFiles(File dir, final String filetype) {
        if (dir.isDirectory()) {
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(filetype)) {
                        String time_filename = filename.substring(0, filename.lastIndexOf("."));
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                            Date date_filename = sdf.parse(time_filename);
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, -2);
                            Date yesterday = cal.getTime();
                            if (date_filename.getTime() - yesterday.getTime() < 0) {
                                return true;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return false;
                }
            };
            File[] files = dir.listFiles(filenameFilter);
            if (files.length <= 0) return true;
            for (File file : files) {
                file.delete();
            }
            if (files.length <= 0) return true;
        }
        return false;
    }
}
