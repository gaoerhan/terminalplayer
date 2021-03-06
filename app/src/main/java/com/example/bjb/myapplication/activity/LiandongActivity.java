package com.example.bjb.myapplication.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bjb.myapplication.MyApplication;
import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.common.observer.INotifyListener;
import com.example.bjb.myapplication.common.observer.NotifyListenerManager;
import com.example.bjb.myapplication.common.observer.NotifyObject;
import com.example.bjb.myapplication.devicecommand.ADVMethod;
import com.example.bjb.myapplication.devicecommand.FactoryCategoryUtils;
import com.example.bjb.myapplication.devicecommand.MyShixinMethod;
import com.example.bjb.myapplication.devicecommand.NewChangshangMethod;
import com.example.bjb.myapplication.entity.NewPicture;
import com.example.bjb.myapplication.entity.NewVideo;
import com.example.bjb.myapplication.httputils.Ok;
import com.example.bjb.myapplication.httputils.callback.CallBack;
import com.example.bjb.myapplication.httputils.callback.FileCallBack;
import com.example.bjb.myapplication.socket.NettyService;
import com.example.bjb.myapplication.socket.SocketService;
import com.example.bjb.myapplication.socket.entity.CommandReceive;
import com.example.bjb.myapplication.socket.entity.HeartbeatResponse;
import com.example.bjb.myapplication.socket.entity.LiandongReceive;
import com.example.bjb.myapplication.socket.entity.LoginResponse;
import com.example.bjb.myapplication.socket.handler.MyProtocolBean;
import com.example.bjb.myapplication.utils.FileUtils;
import com.example.bjb.myapplication.utils.IPUtils;
import com.example.bjb.myapplication.utils.SDCardFileUtils;
import com.example.bjb.myapplication.utils.SPUtil;
import com.example.bjb.myapplication.view.MyImagview;
import com.example.bjb.myapplication.view.NewBannerView;
import com.example.bjb.myapplication.view.NewVideoPlayer;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;
import com.example.bjb.myapplication.view.widget.SimpleDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiandongActivity extends Activity implements INotifyListener {

    public static final String TAG = "LiandongActivity";
    private ServiceConnection serviceConnection;

    private NettyService nettyService;

    private RelativeLayout rootLayout;
    private RelativeLayout rl_liandong_main;

    public MyShixinMethod myShixinMethod = new MyShixinMethod();

    static ArrayList<WeakReference<RelativeLayout>> oldLayouts = new ArrayList<WeakReference<RelativeLayout>>();
    // ????????????????????????
    private ArrayList<ViewerCallback> handleViewers = new ArrayList<>();


    private List<NewVideoPlayer> videoPlayers = new ArrayList<NewVideoPlayer>();

    private Handler handler = new Handler(Looper.getMainLooper());

    private Timer heartTimer;
    private TimerTask heartTimerTask;
    private List<String> playPaths = new ArrayList<>();
    public static boolean isHearting;

    private Timer timer = new Timer();
    private boolean isChecking;

    private ExecutorService fixedThreadPool;

    private List<String> liandongIps = new ArrayList<String>();


    private Timer updateProgressTimer;
    private UpdateProgressTask updateProgressTask;


    private boolean isAccepting = true;
    byte[] buffer = new byte[2024];
    MulticastSocket mSocket;

    private String myMainIp;
    private static String mySelfInnerIp;

    private SimpleDialog simpleDialog;

    private SimpleDialog backDialog;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String progress = (String) msg.obj;


                    if(videoPlayers.size() > 0 && progress != null){
                        videoPlayers.get(0).seekTo(Integer.parseInt(progress));

                    }

                    break;
                default:

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liandong);

        rl_liandong_main = findViewById(R.id.rl_liandong_main);


        fixedThreadPool = Executors.newFixedThreadPool(4);

        simpleDialog = new SimpleDialog(LiandongActivity.this,"???????????????",true);
        simpleDialog.setCanceledOnTouchOutside(false);
        simpleDialog.setCancelable(false);
        simpleDialog.setOnSimpleConfirmListener(new SimpleDialog.OnSimpleConfirmListener() {
            @Override
            public void setSimpleConfirm() {
                Intent intent1 = new Intent(LiandongActivity.this,StartActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        backDialog = new SimpleDialog(LiandongActivity.this,"??????????????????",true);
        backDialog.setCancelable(false);
        backDialog.setCanceledOnTouchOutside(false);
        backDialog.setOnSimpleConfirmListener(new SimpleDialog.OnSimpleConfirmListener() {
            @Override
            public void setSimpleConfirm() {
                Intent intent2 = new Intent(LiandongActivity.this,StartActivity.class);
                startActivity(intent2);
                finish();
            }
        });

        Intent intent = getIntent();

        SPUtil.getInstance().saveString("username", intent.getStringExtra("username"));
        SPUtil.getInstance().saveString("password", intent.getStringExtra("password"));
        SPUtil.getInstance().saveString("terminalname", intent.getStringExtra("terminalname"));
//        SPUtil.getInstance().saveString("ip", "172.16.30.231:8000");
        SPUtil.getInstance().saveString("ip", intent.getStringExtra("ip"));
        heartTimer = new Timer();

        heartTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isHearting) {
                    nettyService.sendHeart();
                }

            }
        };


        heartTimer.schedule(heartTimerTask, 5000, 60 * 1000);

        this.binderSocketService();

        playPic();

    }


    private void binderSocketService() {


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NettyService.NetttBinder binder = (NettyService.NetttBinder) iBinder;
                nettyService = binder.getService();
                nettyService.setOnMessageListener(new NettyService.OnMessageListener() {
                    @Override
                    public void getMessage(final MyProtocolBean msg) {


                        int type = msg.getType();
                        Log.e(TAG, "?????????" + msg.toString());
                        //????????????
                        if (type == 2) {

                            final LoginResponse loginResponse = new Gson().fromJson(msg.getContent(), LoginResponse.class);
                            int code = loginResponse.getCode();
                            Log.e("nettyactivity", " ???????????????" + loginResponse.toString());
                            if (code == 1) {
                                SPUtil.getInstance().saveString("token", loginResponse.getToken());
                                isHearting = true;
                                toastMsg("????????????");

                            } else {
                                isHearting = false;
                                toastMsg("??????????????????" + loginResponse.getMsg());
                                simpleDialog.show();
                            }
                            //????????????
                        } else if (type == 5) {
                            CommandReceive commandReceive = new Gson().fromJson(msg.getContent(), CommandReceive.class);
                            Log.e("nettyactivity", " ???????????????" + commandReceive.toString());
                            List<CommandReceive.DeviceInstructionListBean> deviceInstructionListBeans = commandReceive.getDeviceInstructionList();

                            if (deviceInstructionListBeans.size() == 0) {
                                return;
                            }
                            for (int i = 0; i < deviceInstructionListBeans.size(); i++) {
                                String instructionCode = deviceInstructionListBeans.get(i).getInstructionCode();

                                //??????
                                if ("A006-02".equals(instructionCode)) {
                                    MyApplication.getInstance().setPowerOff();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");

                                    //??????
                                } else if ("A006-03".equals(instructionCode)) {
                                    MyApplication.getInstance().reboot();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //??????
                                } else if ("A006-06".equals(instructionCode)) {
                                    screenShot("A006-06", deviceInstructionListBeans.get(i).getInstructionId());
                                    //????????????
                                } else if ("A006-07".equals(instructionCode)) {
                                    MyApplication.getInstance().setScreenOn();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //????????????
                                } else if ("A006-08".equals(instructionCode)) {
                                    MyApplication.getInstance().setScreenOff();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");

                                } else if ("A006-09".equals(instructionCode)) {

//                                    try {
//                                        getMaterialList();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
                                    //?????????
                                } else if ("A006-16".equals(instructionCode)) {
                                    MyApplication.getInstance().raiseVolume();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //?????????
                                } else if ("A006-17".equals(instructionCode)) {
                                    MyApplication.getInstance().lowVolume();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //??????
                                } else if ("A006-04".equals(instructionCode)) {

                                    if (videoPlayers.size() > 0) {
                                        videoPlayers.get(0).startPlay();
                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //????????????
                                } else if ("A006-05".equals(instructionCode)) {
                                    if (videoPlayers.size() > 0) {
                                        videoPlayers.get(0).pausePlay();
                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //????????????
                                } else if ("A006-12".equals(instructionCode)) {

                                    //????????????
                                } else if ("A006-13".equals(instructionCode)) {

                                    //??????????????????
                                } else if ("A006-14".equals(instructionCode)) {

                                    String progress = deviceInstructionListBeans.get(i).getValue();

                                    if (!TextUtils.isEmpty(progress)) {

                                        int pg = Integer.parseInt(progress);

                                        if (videoPlayers.size() > 0) {
                                            int total = videoPlayers.get(0).getTotalProgress();
                                            videoPlayers.get(0).setPrpgress((total * pg) / 100);
                                        }

                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //????????????
                                } else if ("A006-15".equals(instructionCode)) {

                                }
                            }

                            //????????????
                        } else if (type == 6) {

                            //?????????????????????
                        } else if (type == 7) {

                            //?????????????????????
                        } else if (type == 8) {

                            HeartbeatResponse heartbeatResponse = new Gson().fromJson(msg.getContent(), HeartbeatResponse.class);
                            String code = heartbeatResponse.getCode();
                            Log.e("nettyactivity", " ???????????????" + heartbeatResponse.toString());
                            if ("2".equals(code)) {

                            } else {
                                toastMsg(heartbeatResponse.getMsg());
                            }

                            //??????????????????
                        } else if (type == 9) {
                            final LiandongReceive liandongReceive = new Gson().fromJson(msg.getContent(), LiandongReceive.class);
                            liandongIps = liandongReceive.getDevices();

                            mySelfInnerIp = IPUtils.getLocalIpAddress();
                            Log.e(TAG, "????????????ip:" + mySelfInnerIp);

                            playPaths.clear();
                            if (SDCardFileUtils.isFileExists(SDCardFileUtils.getLiandongRootDir(), liandongReceive.getName())) {

                                playPaths.add(SDCardFileUtils.getLiandongRootDir() + File.separator + liandongReceive.getName());
                                //??????
                                playVideoItem(playPaths);
                                confirm();
                                //?????????????????????????????????

                            } else {

                                //???????????????????????????

                                //??????
                                final String downurl = "http://" + "172.16.202.32:9000" + "/exhibit-browser" + liandongReceive.getPath();
                                Log.e(TAG, "????????????" + downurl);
                                fixedThreadPool.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        download(downurl, SDCardFileUtils.getLiandongRootDir(), "downloading" + liandongReceive.getName());
                                    }
                                });

                            }

                        }
                    }

                    @Override
                    public void sendMsg(String msg) {
                        simpleDialog.show();
                        toastMsg(msg);
                    }
                });

                nettyService.initNetty();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        Intent intent = new Intent(this, NettyService.class);
        String ip = SPUtil.getInstance().getString("ip","");
        if(!TextUtils.isEmpty(ip)){
            String[] srings = ip.split(":");
            intent.putExtra("ip", srings[0]);
            intent.putExtra("port", srings[1]);
            this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }else {
            toastMsg("ip????????????");
        }

    }

    /*??????Toast???????????????????????????   ???????????????????????????????????????toast*/
    private void toastMsg(final String msg) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void registerListener() {
        NotifyListenerManager.getInstance().registerListener(this);
    }

    public void unRegisterListener() {
        NotifyListenerManager.getInstance().unRegisterListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerListener();
    }
    @Override
    public void notifyUpdate(final NotifyObject obj) {
        switch (obj.what) {
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LiandongActivity.this, obj.str, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                break;
            default:
                break;
        }
    }


    private void playPic() {
        // ??????????????????????????????
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }

        handleViewers.clear();

        //??????
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        MyImagview myImagview = new MyImagview(this, R.drawable.main_land);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(myImagview, lp1);
        handleViewers.add(myImagview);

        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        // ????????????
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_liandong_main.addView(layout);
        rl_liandong_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }

    }

    private void playVideoItem(List<String> paths) {
        // ??????????????????????????????
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }
        videoPlayers.clear();
        handleViewers.clear();
        ;      DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        for (int i = 0; i < paths.size(); i++) {
            Log.e(TAG, "????????????" + paths.get(i));
        }

        //??????
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        //-----------------?????????????????????
        NewVideo newVideo = new NewVideo();
        newVideo.setSilence((byte) 0);
        newVideo.setPanel((byte) 1);

        newVideo.setRawPathList(paths);

        newVideo.setWidth(widthPixels);
        newVideo.setHeight(heightPixels);

        NewVideoPlayer videoPlayer = new NewVideoPlayer(this, newVideo, layout, 1);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(videoPlayer, lp1);
        handleViewers.add(videoPlayer);
        videoPlayers.add(videoPlayer);

        // ------------------


        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        // ????????????
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_liandong_main.addView(layout);
        rl_liandong_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }
    }


    private void playImageItem(List<String> paths) {

        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }


        for (int i = 0; i < paths.size(); i++) {

            Log.e(TAG, "????????????" + paths.get(i));
        }
        handleViewers.clear();

        //??????
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        NewPicture picture = new NewPicture();
        picture.setWidth(1920);
        picture.setHeight(1080);
        picture.setSwitchTime(5);

        picture.setRawPathList(paths);


        final NewBannerView bannerView = new NewBannerView(this, picture, layout);


        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(bannerView, lp1);
        handleViewers.add(bannerView);


        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        // ????????????
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_liandong_main.addView(layout);
        rl_liandong_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }

    }


    @Override
    protected void onPause() {
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(isFinishing());
        }
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        super.onPause();
    }


    @Override
    protected void onResume() {
        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isHearting = false;
        isAccepting = false;
        fixedThreadPool.shutdown();
        if (heartTimer != null) {
            heartTimer.cancel();
            heartTimer.purge();
            heartTimer = null;
        }
        playPaths.clear();
        if (heartTimerTask != null) {
            heartTimerTask.cancel();
            heartTimerTask = null;
        }

        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer = null;
        }
        if (updateProgressTask != null) {
            updateProgressTask.cancel();
            updateProgressTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

       if(mSocket != null){
           mSocket.close();
       }
        unRegisterListener();
        for (ViewerCallback v : handleViewers) {
            v.viewerOnDestroy();
        }
        unbindService(serviceConnection);
        Intent intent = new Intent(getApplicationContext(), NettyService.class);
        stopService(intent);
    }


    private void download(String url, final String localpath, final String filename) {

        Ok.download().url(url)
                .build()
                .tag(MyApplication.getInstance().getApplicationContext())
                .call(new FileCallBack(localpath, filename) {
                    @Override
                    public void progress(int progress) {

                        Log.e("TestDownload", "??????????????????" + progress);
                    }

                    @Override
                    public void success(File file) {
                        Log.e("TestDownload", "???????????????");
                        String newname = filename.substring(11);
                        file.renameTo(new File(localpath,newname));


                        playPaths.add(SDCardFileUtils.getLiandongRootDir() + File.separator + newname);
                        playVideoItem(playPaths);
                        confirm();
                    }

                    @Override
                    public void fail(Exception e) {
                        Log.e("TestDownload", "??????????????????" + e.toString());
                        final String failReason = e.toString();


                    }
                });

    }


    //??????
    public void screenShot(final String uploadType, final int instructId) {

        if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.NEW_CHANGSHANG) {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);
            NewChangshangMethod.screenshot();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameA(fname);
                    // ??????
                    upload(uploadType, instructId, pathName);

                }
            }, 4000);
        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.AIDIWEI) {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);
            ADVMethod.screenShot();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameA(fname);

                    // ??????
                    upload(uploadType, instructId, pathName);
                }
            }, 4000);

        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188) {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);

            // ??????????????? ?????????????????????png!!
            myShixinMethod.screenShot();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameY(fname);

                    // ??????
                    upload(uploadType, instructId, pathName);

                }
            }, 4000);

        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288) {

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);

            // ??????????????? ?????????????????????png!!
            myShixinMethod.screenShot();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameY(fname);
                    // ??????

                    upload(uploadType, instructId, pathName);

                }
            }, 4000);
        }


    }


    private void upload(String uploadType, final int instructId, String pathName) {
        Ok.post().url("http://" + SPUtil.getInstance().getString("ip", "") + "/exhibit-browser/uploadSingle")
                .param("uploadType", uploadType)
                .param("instructId", instructId)
                .file("file", new File(pathName))
                .header("token", SPUtil.getInstance().getString("token", ""))
                .build()
                .call(new CallBack() {
                    @Override
                    public void fail(Exception e) {
                        Log.e("upload", "??????:" + e.toString());
                        nettyService.sendCommandResponse(instructId, "2");
                    }

                    @Override
                    public void success(String response) {
                        Log.e("upload", "??????:" + response.toString());
                        nettyService.sendCommandResponse(instructId, "1");
                    }
                });
    }





    private void send() {
        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer.purge();
            updateProgressTimer = null;
        }
        if (updateProgressTask != null) {
            updateProgressTask.cancel();
            updateProgressTask = null;
        }

        updateProgressTimer = new Timer();
        updateProgressTask = new UpdateProgressTask();
        updateProgressTimer.schedule(updateProgressTask, 5 * 1000L, 60 * 1000);
    }

    private void receive() {
        new Thread() {
            @Override
            public void run() {

                if(mSocket != null){
                    if(mSocket.isClosed()){
                        mSocket = null;
                    }else {
                        mSocket.close();
                        mSocket = null;
                    }
                }

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                Log.e("player", "????????????");
                while (isAccepting) {
                    try {
                        if (mSocket == null) {
                            try {
                                mSocket = new MulticastSocket(30001);
                                InetAddress group=InetAddress.getByName("239.0.0.1");//????????????IP
                                mSocket.joinGroup(group);//????????????????????????????????????????????????????????????????????????????????????????????????
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }
                        }
                        mSocket.receive(packet);
                        String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        Log.e("playertongbu", "?????????????????????" + s);

                        //??????????????????????????????????????????????????????????????????


                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = s;
                        mHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    class UpdateProgressTask extends TimerTask {
        @Override
        public void run() {
            try {

                if(videoPlayers.size() == 0){
                    return;
                }

                String message = videoPlayers.get(0).getProgress() + "";

                MulticastSocket sendSocket  = new MulticastSocket(30001);//????????????????????????30001??????
                InetAddress group=InetAddress.getByName("239.0.0.1");//????????????IP

                sendSocket.joinGroup(group);//????????????????????????????????????????????????????????????????????????????????????????????????
                sendSocket.setTimeToLive(4);//??????TTL
                //??????UDP??????????????????????????????????????????????????????
                DatagramPacket packet = new DatagramPacket(message.getBytes(),message.getBytes().length,group,30001);
                sendSocket.send(packet);//????????????
                sendSocket.close();//???????????????

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void confirm(){
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < liandongIps.size(); i++) {
                    try {
                        if (IPUtils.ping(liandongIps.get(i))) {
                            myMainIp = liandongIps.get(i);
                            Log.e("player", "????????????????????????ip??????" + myMainIp);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(myMainIp)) {

                    if(myMainIp.equals(mySelfInnerIp)){

                        send();
                        receive();
                    }else {

                        cancelSend();
                        receive();
                    }

                }
            }
        }.start();
    }

    private void cancelSend() {
        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer.purge();
            updateProgressTimer = null;
        }
        if (updateProgressTask != null) {
            updateProgressTask.cancel();
            updateProgressTask = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backDialog.show();
        }


        return super.onKeyDown(keyCode, event);
    }

}
