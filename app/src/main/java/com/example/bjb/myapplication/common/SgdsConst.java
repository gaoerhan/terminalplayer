package com.example.bjb.myapplication.common;

import com.example.bjb.myapplication.utils.SDCardFileUtils;

import java.io.File;


public class SgdsConst {

    private static String CHN_ROOT_PATHNAME = "chn";
    private static String ITM_ROOT_PATHNAME = "itm";
    private static String RAW_ROOT_PATHNAME = "raw";
    private static String CMD_ROOT_PATHNAME = "cmd";
    private static String SDE_ROOT_PATHNAME = "sde";
    private static String RECORD_ROOT_PATHNAME = "record";
    private static String SCREENSHOT_PATHNAME = "local/screenshots";

    private static String JWPLAYER_PATHNAME = "jwplayer/player.swf";
    private static String FLASHPLAYER_CACHEDIR = "flashplayer";

    private static String DEFAULT_DISPLAY_PATHNAME = "default/d0";
    private static String DEFAULT_DISPLAY_FILENAME = "default/d0/main.png";

    private static String CRASH_LOG_FILENAME = "crash.log";
    private static String DEBUG_LOG_FILENAME = "debug.log";

    private static int DEVICE_TYPE = EnumConst.DeviceType.SHIXIN.getValue();

    public static int getDeviceType() {
        return DEVICE_TYPE;
    }

    public static String ChnRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + CHN_ROOT_PATHNAME;
    }

    public static String ItmRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + ITM_ROOT_PATHNAME;
    }

    public static String RawRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + RAW_ROOT_PATHNAME;
    }

    public static String getRecordPath() {
        File cacheDir = new File(SDCardFileUtils.getPlayerRootDir() + File.separator + RECORD_ROOT_PATHNAME);
        if (!cacheDir.exists())
            cacheDir.mkdir();
        return cacheDir.getPath();
    }

    public static String CmdRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + CMD_ROOT_PATHNAME;
    }

    public static String SdeRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + SDE_ROOT_PATHNAME;
    }

    public static String JwplayerPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + JWPLAYER_PATHNAME;
    }

    public static String FlashplayerCacheDir() {
        return FLASHPLAYER_CACHEDIR;
    }

    public static String DefaultDisplayDir() {
        return DEFAULT_DISPLAY_PATHNAME;
    }

    public static String DefaultDisplayDirSd() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + DEFAULT_DISPLAY_PATHNAME;
    }

    public static String DefaultDisplayFile() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + DEFAULT_DISPLAY_FILENAME;
    }

    public static String CrashLogFile() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + CRASH_LOG_FILENAME;
    }

    public static String NewApkFile(String name) {
        return SDCardFileUtils.getPlayerRootDir() + "/" + name;
    }

    public static String ScreenshotDir() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + SCREENSHOT_PATHNAME;
    }

    public static String DebugLogFile() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + DEBUG_LOG_FILENAME;
    }

    public static String LogsRootPath() {
        return SDCardFileUtils.getPlayerRootDir() + "/" + "logs";
    }
}
