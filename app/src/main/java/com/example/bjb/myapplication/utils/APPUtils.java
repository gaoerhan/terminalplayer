package com.example.bjb.myapplication.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class APPUtils {


    //开启第三方应用
    public static void openApp(Context context, String packageName) {
        if(TextUtils.isEmpty(packageName)){
            return;
        }
        Log.e("appstart","三方应用启动了open");
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pi == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String packageName1 = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent1 = new Intent();
            intent1 = pm.getLaunchIntentForPackage(packageName1);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent1);
        }
    }

    //静默安装三方应用
    public static boolean installSlient(final String path) {
                //pm install -r /mnt/sdcard/Android/elevatorcloud.apk
                String cmd = "pm install -r "+ path;
                Process process = null;
                DataOutputStream os = null;
                BufferedReader successResult = null;
                BufferedReader errorResult = null;
                StringBuilder successMsg = null;
                StringBuilder errorMsg = null;
                try {
                    //静默安装需要root权限
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.write(cmd.getBytes());
                    os.writeBytes("\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    //执行命令
                    process.waitFor();
                    //获取返回结果
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String s;
                    while ((s = successResult.readLine()) != null) {
                        successMsg.append(s);
                        Log.e("appp", "安装成功消息：" + successMsg.toString());
                        return true;
                    }
                    while ((s = errorResult.readLine()) != null) {
                        errorMsg.append(s);
                        Log.e("appp", "安装错误消息: " + errorMsg.toString());
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (successResult != null) {
                            successResult.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //显示结果
                Log.e("appp", "成功消息：" + successMsg.toString() + "\n" + "错误消息: " + errorMsg.toString());
                return false;
    }


    //静默卸载三方应用
    public static boolean uninstallSlient(final String packgeName) {

                String cmd = "pm uninstall " + packgeName;
                Process process = null;
                DataOutputStream os = null;
                BufferedReader successResult = null;
                BufferedReader errorResult = null;
                StringBuilder successMsg = null;
                StringBuilder errorMsg = null;
                try {
                    //卸载也需要root权限
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.write(cmd.getBytes());
                    os.writeBytes("\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    //执行命令
                    process.waitFor();
                    //获取返回结果
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String s;
                    while ((s = successResult.readLine()) != null) {
                        successMsg.append(s);
                        if("Failure".equals(successMsg.toString())){
                            return true;
                        }else {
                            return true;
                        }
                    }
                    while ((s = errorResult.readLine()) != null) {
                        errorMsg.append(s);

                        Log.e("appp", "卸载错误消息: " + errorMsg.toString());
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                        if (successResult != null) {
                            successResult.close();
                        }
                        if (errorResult != null) {
                            errorResult.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //显示结果
                Log.e("myvideo", "成功消息：" + successMsg.toString() + "\n" + "错误消息: " + errorMsg.toString());
                return false;

    }



    //获取三方应用版本
    public static void getAppInfo(Context context) {
        //PackageManager.GET_ACTIVITIES
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(0);

        try {
            String packageName = null;
            String path = null;
            for (PackageInfo info : packages) {
                packageName = info.packageName;

                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    path = context.getPackageManager().getApplicationInfo(
                            packageName, 0).sourceDir;
                    StringBuilder sb = new StringBuilder();
                    sb.append("name=")
                            .append(info.applicationInfo.loadLabel(
                                    context.getPackageManager()).toString())
                            .append(";");
                    sb.append("packageName=").append(packageName)
                            .append(";");
                    sb.append("versionCode=").append(info.versionCode)
                            .append(";");
                    sb.append("versionName=").append(info.versionName)
                            .append(";");
                    sb.append("path=").append(path).append(";");
                    File file = new File(path);
                    if (file.exists()) {
                        long len = file.length();
                        sb.append("size=").append(len);
                    }
                    sb.append("\n");
                    Log.e("application", "第三方应用：" + sb.toString());
                } else {
                    // Log.v("app", "系统应用：" + pkg + ", 路径：" + path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //判断应用是否在前台
    public static boolean IsForeground(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);

        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().startsWith(context.getPackageName())) {
                return true;
            }
        }

        return false;

    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//    boolean flag=false;
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了

//        flag = true;
                return true;
            }
        }
        return false;
    }

    public static String isForeground2(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return "";
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
//    boolean flag=false;
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了

//        flag = true;
                return taskInfo.topActivity.getShortClassName();
            }
        }
        return "";
    }
        //通过应用名称获取包名
    public static String getPackageNameByAppName(Context context, String appName) {
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(0);
        String packageName = "";
        try {

            for (PackageInfo info : packages) {
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                    String name = info.applicationInfo.loadLabel(
                            context.getPackageManager()).toString();
                    if (appName.equals(name)) {
                        packageName = info.packageName;
                        Log.e("application", "第三方应用名称" + appName + "包名：" + packageName);
                    }

                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }
}
