package com.example.bjb.myapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bjb.myapplication.R;

import com.example.bjb.myapplication.utils.SDCardFileUtils;
import com.upyun.upplayer.widget.UpVideoView;

public class TestActivity extends Activity implements View.OnClickListener {

    private UpVideoView upvideoviewlefttop;
    private String pathlefttop;
    private UpVideoView upvideoviewleftbottom;
    private String pathleftbottom;

    private UpVideoView upvideoviewrighttop;
    private String pathrighttop;

    private UpVideoView upvideoviewrightbottom;
    private String pathrightbottom;

    private Button btn_stop;
    private Button btn_seekto;
    private Button btn_seektoguding;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


//        pathleft = Environment.getExternalStorageDirectory().getPath() + "/ContentPlayer/liandong/test.mp4";
        pathlefttop = Environment.getExternalStorageDirectory().getPath() + "/Movies/lt.mp4";
        pathleftbottom = Environment.getExternalStorageDirectory().getPath() + "/Movies/lb.mp4";

        pathrighttop = Environment.getExternalStorageDirectory().getPath() + "/Movies/rt.mp4";
        pathrightbottom = Environment.getExternalStorageDirectory().getPath() + "/Movies/rb.mp4";

        upvideoviewlefttop = findViewById(R.id.upvideoviewlefttop);
        upvideoviewrighttop = findViewById(R.id.upvideoviewrighttop);
        upvideoviewleftbottom = findViewById(R.id.upvideoviewleftbottom);
        upvideoviewrightbottom = findViewById(R.id.upvideoviewrightbottom);

        btn_stop = findViewById(R.id.btn_stop);
        btn_seekto = findViewById(R.id.btn_seekto);
        btn_seektoguding = findViewById(R.id.btn_seektoguding);
        btn_seekto.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_seektoguding.setOnClickListener(this);

        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {

            upvideoviewlefttop.setVideoPath(pathlefttop);
            upvideoviewrighttop.setVideoPath(pathrighttop);
            upvideoviewleftbottom.setVideoPath(pathleftbottom);
            upvideoviewrightbottom.setVideoPath(pathrightbottom);


        }



    }



    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    protected void onStop() {
        super.onStop();
        upvideoviewlefttop.pause();
        upvideoviewrighttop.pause();
        upvideoviewleftbottom.pause();
        upvideoviewrightbottom.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        upvideoviewlefttop.start();
        upvideoviewrighttop.start();
        upvideoviewleftbottom.start();
        upvideoviewrightbottom.start();
        upvideoviewlefttop.resume();
        upvideoviewrighttop.resume();
        upvideoviewleftbottom.resume();
        upvideoviewrightbottom.resume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_stop:

                upvideoviewlefttop.pause();
                upvideoviewrighttop.pause();
                upvideoviewleftbottom.pause();
                upvideoviewrightbottom.pause();
                break;
            case R.id.btn_seekto:

                int current = upvideoviewlefttop.getCurrentPosition();
                upvideoviewlefttop.seekTo(current + 1000);
                upvideoviewrighttop.seekTo(current + 1000);
                upvideoviewleftbottom.seekTo(current + 1000);
                upvideoviewrightbottom.seekTo(current + 1000);

                break;
            case R.id.btn_seektoguding:

                upvideoviewlefttop.seekTo(0);
                upvideoviewrighttop.seekTo(0);
                upvideoviewleftbottom.seekTo(0);
                upvideoviewrightbottom.seekTo(0);

                break;
            default:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                upvideoviewlefttop.setVideoPath(pathlefttop);
                upvideoviewrighttop.setVideoPath(pathrighttop);
                upvideoviewleftbottom.setVideoPath(pathleftbottom);
                upvideoviewrightbottom.setVideoPath(pathrightbottom);

                upvideoviewlefttop.start();
                upvideoviewrighttop.start();
                upvideoviewleftbottom.start();
                upvideoviewrightbottom.start();

            } else {

            }
        }
    }
}