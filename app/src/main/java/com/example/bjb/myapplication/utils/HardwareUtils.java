package com.example.bjb.myapplication.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


import com.example.bjb.myapplication.MyApplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 获取硬件信息
 *
 * @author Ethan.li
 */
public class HardwareUtils {

    public static final String SCREENNAME = "Screen1";

    /**
     * 获取Sd卡存储大小
     *
     * @return
     */
    public static long[] getSDCardVolume() {
        long[] sdCardInfo = new long[2];
        File sdcardDir = SDCardFileUtils.getRootFile();
        // String state = Environment.getStorageState(sdcardDir); 需要android4.4
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            long availBlocks = sf.getAvailableBlocks();

            sdCardInfo[0] = (bSize * bCount) / 1024;//总大小
            sdCardInfo[1] = (bSize * availBlocks) / 1024;//可用大小
        }
        return sdCardInfo;
    }

    /**
     * 获取可用的内部记忆大小
     *
     * @return
     */
    public static long getAvailableMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize) / 1024;
    }

    /**
     * 获取cup个数
     *
     * @return
     */
    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {

            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    //获取本地IP
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "";
    }



    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    /**
     * 获取设备ID
     *
     * @return
     */
    public static String getModel() {
        String model = Build.ID;
        if (model != null) {
            return model;
        } else {
            return "";
        }
    }

    /**
     * 机器唯一编码
     *
     * @return
     */
    public static String getMachineCode() {
        String machineCode = getLocalMac();
        if ("".equals(machineCode)) {
            machineCode = UUID.randomUUID().toString();
        }
        machineCode = DsStringUtils.getMd5Value(machineCode);
        if (machineCode.length() > 20) {
            machineCode = machineCode.substring(0, 20);
        }
        return machineCode;
    }
    //根据IP获取本地Mac
    public static String getLocalMac() {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
                    .getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
        }
        return mac_s;
    }

    //获取内存使用率
    public static String getMemoryUsage(){
        long total = getMemoryTotal();
        long free = getMemoryFree();
        double usage = (total - free)*100 / total;
        BigDecimal b = new BigDecimal(usage);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%";
    }

    private static int getMemoryTotal() {
        int total = 0;

        try {
            FileReader localFileReader = new FileReader("/proc/meminfo");
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            String line = localBufferedReader.readLine() ;// 读取memInfo第一行: 系统总内存大小
            String[] elements = line.split(" ");
            localBufferedReader.close();
            total = (int)(Double.parseDouble(elements[8]) / 1000.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    private static long getMemoryFree(){
        long memoryFree;
        ActivityManager activityManager = (ActivityManager) MyApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        memoryFree = (long) (memoryInfo.availMem / 1000.0 / 1000.0);
        return memoryFree;
    }






    //Sd卡占用情况
    public static String getSDCardUsage() {
        double usage = 0;
        long[] sdCardInfo = getSDCardVolume();

        if (sdCardInfo[0] != 0)
            usage = 1 - (double) sdCardInfo[1] / sdCardInfo[0];

        return new DecimalFormat("00%").format(usage);
    }

    //cpu占用
    private static long total = 0;
    private static long idle = 0;

    public static String getCPUUsage() {
        double usage = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            String[] toks = load.split(" ");
            long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
            long currIdle = Long.parseLong(toks[5]);

            usage = (double) (currTotal - total) / (currTotal - total + currIdle - idle);
            total = currTotal;
            idle = currIdle;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DecimalFormat("00%").format(usage);
    }

    public static void killSleep() {
        String ppid = getSleepPid();
        if (ppid.length() > 0) {
            String cmd = String.format("kill -9 %s \n", ppid);
            try {
                runAsRoot(cmd);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String getSleepPid() {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader bufferedReader = null;
        String ppid = "";
        try {
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号 // TODO 判断root成功
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("ps -N sleep \n");
            os.writeBytes("exit $?\n"); // 以上个命令的返回值作为退出码
            os.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            String[] string = sb.toString().split("\n");
            int count = 0;
            if (string.length > 1) {
                String[] str = string[1].split(" ");
                for (String str1 : str) {
                    if (str1.length() > 1) {
                        count++;
                        if (count == 3) {
                            ppid = str1.trim();
                        }
                    }
                }
            }
            process.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    ;
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    ;
                }
            }
            bufferedReader = null;
            if (process != null) {
                try {
                    int exitCode = process.exitValue();
                    process.destroy();
                } catch (Throwable e) {
                    ;
                }
            }
        }
        return ppid;
    }

    private static boolean runAsRoot(String cmd) throws IOException, InterruptedException {
        int exitCode = -1;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号 // TODO 判断root成功
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n"); // 以上个命令的返回值作为退出码
            os.flush();
            process.waitFor();
        } finally {
            try {
//                if (os != null) {
//                    os.close();
//                }
                if (process != null) {
                    exitCode = process.exitValue();
                    process.destroy();
                }
            } catch (Exception e) {
            }
        }
        return (exitCode == 0);
    }

    public static boolean syncSystemTime(Date date) throws ParseException, IOException, InterruptedException {
        Date sysdate = Calendar.getInstance().getTime();
        if (Math.abs((date.getTime() - sysdate.getTime()) / 1000) > 3 * 60) {
            //set sys time
            setSystemTime(date);
            return true;
        }
        return false;
    }

    // yyyy-MM-ddTHH:mm:ssZ
    public static void syncSystemTime(String timestr) throws ParseException, IOException, InterruptedException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse(timestr);
        syncSystemTime(date);
    }

    private static void setSystemTime(Date date) throws ParseException, IOException, InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.US);
        String datetime = sdf.format(date);
        String command = "date -s \"" + datetime + "\"";
        LogUtils.i("ds", command);
        runAsRoot(command);
    }

    public static void cutScreen(String name) throws ParseException, IOException, InterruptedException {
        // screencap -p /sdcard/screen.png
        String command = String.format("screencap -p \"%s\"", name);
        Log.e("ds", command);
        runAsRoot(command);
    }





    public static void writeContentToFile(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //得到外部储存sdcard的状态
    private static String sdcard=Environment.getExternalStorageState();
    //外部储存sdcard存在的情况
    private static String state=Environment.MEDIA_MOUNTED;


    public static String getAvailableSize()
    {
       StatFs statFs=new StatFs(Environment.getExternalStorageDirectory().getPath());
        if(sdcard.equals(state))
        {
            //获得Sdcard上每个block的size
            long blockSize=statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable=statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal=blockSize*blockavailable/1024/1024/1024;
            return blockavailableTotal + "G";
        }else
        {
            return "";
        }
    }
}
