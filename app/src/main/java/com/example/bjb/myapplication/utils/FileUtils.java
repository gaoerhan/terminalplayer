package com.example.bjb.myapplication.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    /**
     * 遍历文件夹下的文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file) {
        List<File> list = new ArrayList<File>();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return null;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f);
                }
            }
        }
        return list;
    }

    /**
     * 适用中银板子
     * 遍历文件夹下获取第一个文件的名字并修改
     *
     * @param newFileName 新文件名字
     *
     *             String fname = String.format("%s/%s.%d.snapshot.jpg", SgdsConst.ScreenshotDir(), guid, System.currentTimeMillis());
     */
    public static String getFilePathNameY(String newFileName) {
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/Screenshots/");
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return "";
        } else {

            for(int i = 0;i<fileArray.length;i++){
                if (fileArray[i].isFile()) {
                    if(i == 0){
                        String oldFileName = fileArray[i].getName();
                        File oldFile = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/Screenshots/"+ oldFileName);
                        File newFile = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/Screenshots/" + newFileName);
                        oldFile.renameTo(newFile);

                        return Environment.getExternalStorageDirectory().getPath()+"/Pictures/Screenshots/" + newFileName;
                    }
                } else {
                }
            }
        }
        return "";

    }



    public static String getFilePathNameA(String newFileName) {
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Screenshots/");
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return "";
        } else {

            for(int i = 0;i<fileArray.length;i++){
                if (fileArray[i].isFile()) {
                    if(i == 0){
                        String oldFileName = fileArray[i].getName();
                        Log.e("adv","图片名字:"+oldFileName);
                        File oldFile = new File(Environment.getExternalStorageDirectory().getPath()+"/Screenshots/"+ oldFileName);
                        File newFile = new File(Environment.getExternalStorageDirectory().getPath()+"/Screenshots/" + newFileName);
                        oldFile.renameTo(newFile);

                        return Environment.getExternalStorageDirectory().getPath()+"/Screenshots/" + newFileName;
                    }
                } else {
                }
            }
        }
        return "";

    }
    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }

    public static void deleteDir(File f) {
        if(!f.exists()){
            f.mkdirs();
        }
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory())
                    deleteDir(file);
                file.delete();
            }
        }
    }


    public static String readTxtFile(File file) {
        String content = ""; //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

}
