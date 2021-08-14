package com.example.bjb.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.bjb.myapplication.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Locale;


/**
 * desc:SD卡文件操作工具类
 */
public class SDCardFileUtils {

    public static final String PLAYER_ROOT_DIR = "ContentPlayer";

    /**
     * desc：外部文件是否可写
     */
    public static boolean isExternalStorageWriteable() {
        if (Environment.MEDIA_MOUNTED.equals(getExternalStorageState())) {// 可写
            return true;
        }
        // Environment.MEDIA_MOUNTED_READ_ONLY.equals()//只读
        return false;
    }

    /**
     * desc：获得sd卡根目录
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * desc：获取外部存储设备状态
     */
    public static String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    /**
     * desc：获得根目录文件
     */
    public static File getRootFile() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
        String s = spf.getString("install_dir_preference", "");
        File f = new File(s);
        if (!f.exists()) {
            f = Environment.getExternalStorageDirectory();
            spf.edit().putString("install_dir_preference", f.getPath()).commit();
        }
        return f;
    }

    /**
     * desc：获得根目录
     */
    public static String getRootDir() {
        return getRootFile().getPath();
    }

    /**
     * desc：获得Player根目录
     */
    public static String getPlayerRootDir() {
        return getRootFile().getPath() + File.separator + PLAYER_ROOT_DIR;
    }


    //获取视频路径
    public static String getVideoRootDir() {

        File cacheDir = new File(getRootFile().getPath() + File.separator + PLAYER_ROOT_DIR + File.separator + "video");
        if (!cacheDir.exists())
            cacheDir.mkdir();
        return cacheDir.getPath();

    }
    //获取视频路径
    public static String getWebviewRootDir() {

        File cacheDir = new File(getRootFile().getPath() + File.separator + PLAYER_ROOT_DIR + File.separator + "web");
        if (!cacheDir.exists())
            cacheDir.mkdir();
        return cacheDir.getPath();

    }

    //获取图片路径

    public static String getPictureRootDir() {
        File cacheDir = new File(getRootFile().getPath() + File.separator + PLAYER_ROOT_DIR + File.separator + "picture");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getPath();
    }

    public static String getLiandongRootDir() {
        File cacheDir = new File(getRootFile().getPath() + File.separator + PLAYER_ROOT_DIR + File.separator + "liandong");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getPath();
    }

    /**
     * desc：创建目录
     */
    public static File createDir(String dir) {
        File dirFile = new File(getRootDir() + File.separator + dir);
        if (!dirFile.exists())
            dirFile.mkdirs();

        return dirFile;
    }

    /**
     * desc：创建文件
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static File createFile(String dir, String fileName) {
        createDir(dir);
        File newFile = new File(getRootDir() + File.separator + dir + File.separator + fileName);

        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return newFile;
    }

    /**
     * desc：创建文件
     *
     * @param absPath
     * @return
     */
    public static File createFile(String absPath) {
        File newFile = new File(getRootDir() + File.separator + absPath);

        // 删除原始文件
        if (newFile.exists()) {
            final File to = new File(newFile.getAbsolutePath() + System.currentTimeMillis());
            newFile.renameTo(to);
            to.delete();
        }

        // 创建新文件
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFile;
    }

    /**
     * desc：获取文件
     */
    public static File getFile(String dir, String fileName) {
        String path = getRootDir() + File.separator + dir + File.separator + fileName;
        return new File(path);
    }

    /**
     * desc：获取文件
     */
    public static File getFile(String absPath) {
        String path = getRootDir() + File.separator + absPath;
        return new File(path);
    }

//    /**
//     * desc：判断文件是否存在
//     */
//    public static Boolean isFileExists(String dir, String fileName) {
//        return getFile(dir, fileName).exists();
//    }


    public static boolean isFileExists(String dir, String fileName) {
        try {
            File f = new File(dir,fileName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * desc：获取StatFs
     */
    public static StatFs getStatFs() {
        return new StatFs(getRootDir());
    }

    /**
     * desc：获取空闲的存储空间大小
     */
    public static long getAvailaleSize() {
        StatFs stat = getStatFs();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
        // (availableBlocks * blockSize)/1024 KIB单位 || (availableBlocks *
        // blockSize)/1024/1024 MIB单位
    }

    /**
     * desc：计算总空间大小
     */
    public long getAllSize() {
        StatFs stat = getStatFs();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize;
    }

    /**
     * desc：判断是否有指定的磁盘空间
     *
     * @param needSize 需要空闲字节数
     * @return
     */
    public static boolean hasAvailaleSize(long needSize) {
        return getAvailaleSize() > needSize;
    }

    /**
     * desc：关闭RandomAccessFile文件
     *
     * @param accessFile
     */
    public static void closeRandomAccessFile(RandomAccessFile accessFile) {
        if (accessFile != null) {
            try {
                accessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * desc：递归删除指定路径下的所有文件
     *
     * @param dir
     * @param fileName
     */
    public static void deleteAll(String dir, String fileName) {
        File file = getFile(dir, fileName);

        if (file.exists()) {
            deleteAll(file);
        }
    }

    /**
     * desc：递归删除指定路径下的所有文件
     *
     * @param file
     */
    public static void deleteAll(File file) {
        if (file.exists()) {
            if (file.isFile() || file.list().length == 0) {
                final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
                file.renameTo(to);
                to.delete();// 删除该文件夹
            } else {
                File[] files = file.listFiles();

                for (File f : files) {
                    deleteAll(f);// 递归删除每一个文件

                    final File to = new File(f.getAbsolutePath() + System.currentTimeMillis());
                    f.renameTo(to);
                    to.delete();// 删除该文件夹
                }
            }
        }
    }

    /**
     * desc：清空文件内容
     */
    public static void clearHisDataFile(String dir, String targetFilename) {
        File target = getFile(dir, targetFilename);
        if (target.exists()) {
            FileWriter fw = null;

            try {
                fw = new FileWriter(target);
                fw.write("1");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * desc：清空文件内容
     */
    public static void clearFileContent(File file) {
        if (file.exists()) {
            FileWriter fw = null;

            try {
                fw = new FileWriter(file, false);
                fw.write("");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void copyAssetstoSdcard(Context context, String assetsDir,
                                          String destDir) {
        String[] files;
        AssetManager am = context.getResources().getAssets();
        try {
            files = am.list(assetsDir);
        } catch (IOException e) {
            Log.e("dsandroid", String.format("copyAssetstoSdcard: %s failed.", assetsDir));
            e.printStackTrace();
            return;
        }

        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (String file : files) {
            File f = new File(destDir + '/' + file);
            if (!f.exists()) {
                try {
                    InputStream in = am.open(assetsDir + "/" + file);
                    OutputStream out = new FileOutputStream(f);

                    byte[] buf = new byte[10 * 1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();
                } catch (IOException e) {
                    Log.e("dsandroid", String.format("copyAssetstoSdcard: %s failed.", f.getAbsolutePath()));
                    e.printStackTrace();
                }
            }
        }
    }

    public static HashSet<String> getSdcards() {
        HashSet<String> ss = getExternalMounts();
        ss.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        return ss;
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|fuseblk|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }


    /**
     * 文件夹拷贝
     *
     * @param fromFile 文件夹路径  例如：/mnt/sdcard/A/
     * @param toFile   本地的保存路径  例如：/mnt/sdcard/B/
     * @return 成功失败  0成功 -1失败
     */
    public static int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (File file : currentFiles) {
            if (file.isDirectory())//如果当前项为子目录 进行递归
            {
                copy(file.getPath() + "/", toFile + file.getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(file.getPath(), toFile + file.getName());
            }
        }
        return 0;
    }

    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    private static int CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) != -1) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }
}
