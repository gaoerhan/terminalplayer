package com.example.bjb.myapplication.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bjb.myapplication.MyApplication;
import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.common.Constants;
import com.example.bjb.myapplication.common.observer.INotifyListener;
import com.example.bjb.myapplication.common.observer.NotifyListenerManager;
import com.example.bjb.myapplication.common.observer.NotifyObject;
import com.example.bjb.myapplication.devicecommand.ADVMethod;
import com.example.bjb.myapplication.devicecommand.FactoryCategoryUtils;
import com.example.bjb.myapplication.devicecommand.MyShixinMethod;
import com.example.bjb.myapplication.devicecommand.NewChangshangMethod;
import com.example.bjb.myapplication.entity.DefaultPlayBean;
import com.example.bjb.myapplication.entity.NewPicture;
import com.example.bjb.myapplication.entity.NewVideo;
import com.example.bjb.myapplication.httputils.Ok;
import com.example.bjb.myapplication.httputils.callback.CallBack;
import com.example.bjb.myapplication.httputils.callback.FileCallBack;
import com.example.bjb.myapplication.httputils.callback.JsonCallBack;
import com.example.bjb.myapplication.net.NetStateChangeObserver;
import com.example.bjb.myapplication.net.NetStateChangeReceiver;
import com.example.bjb.myapplication.net.NetworkType;
import com.example.bjb.myapplication.socket.NettyService;
import com.example.bjb.myapplication.socket.entity.CommandReceive;
import com.example.bjb.myapplication.socket.entity.HeartbeatResponse;
import com.example.bjb.myapplication.socket.entity.LiandongReceive;
import com.example.bjb.myapplication.socket.entity.LoginResponse;
import com.example.bjb.myapplication.socket.entity.MaterialListBean;
import com.example.bjb.myapplication.socket.handler.MyProtocolBean;
import com.example.bjb.myapplication.utils.FileUtils;
import com.example.bjb.myapplication.utils.HardwareUtils;
import com.example.bjb.myapplication.utils.IPUtils;
import com.example.bjb.myapplication.utils.SDCardFileUtils;
import com.example.bjb.myapplication.utils.SPUtil;
import com.example.bjb.myapplication.view.MyImagview;
import com.example.bjb.myapplication.view.MyMediaView;
import com.example.bjb.myapplication.view.NewBannerView;
import com.example.bjb.myapplication.view.NewVideoPlayer;
import com.example.bjb.myapplication.view.WebShower;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;
import com.example.bjb.myapplication.view.widget.SimpleDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyActivity extends Activity implements View.OnClickListener, INotifyListener, NetStateChangeObserver {

    public static final String TAG = "nettyactivity";

    private String currentLPVideoName;

    private ServiceConnection serviceConnection;
    private NettyService nettyService;
    private boolean isAccepting = true;

    public MyShixinMethod myShixinMethod = new MyShixinMethod();
    private Handler handler = new Handler(Looper.getMainLooper());

    private Timer heartTimer;
    private TimerTask heartTimerTask;
    public static boolean isHearting;

    public static List<MaterialListBean> materialListBeans = new ArrayList<>();

    byte[] buffer = new byte[2024];
    private String materialIds;
    private static String mySelfInnerIp;
    private String sourceType;

    private volatile boolean isFirstPlayDefault = true;

    private RelativeLayout rootLayout;
    private RelativeLayout rl_content_main;
    static ArrayList<WeakReference<RelativeLayout>> oldLayouts = new ArrayList<WeakReference<RelativeLayout>>();

    // 需控制的控件列表
    private ArrayList<ViewerCallback> handleViewers = new ArrayList<>();

    private NewVideoPlayer newVideoPlayer;
    private MyMediaView myMediaView;
    private NewBannerView bannerView;
    private WebShower webShower;

    private List<String> liandongIps = new ArrayList<String>();
    private List<String> playPaths = new ArrayList<>();

    private Timer updateProgressTimer;
    private NettyActivity.UpdateProgressTask updateProgressTask;
    private String[] playIds;
    private String myMainIp;
    private boolean isChecking;

    private ExecutorService fixedThreadPool;
    MulticastSocket mSocket;
    private Timer timer = new Timer();

    private SimpleDialog simpleDialog;

    private SimpleDialog backDialog;

    private boolean isLianping;

    private int reloginCount = 0;

    private Handler mHandler = new Handler(new WeakReference<Handler.Callback>((Message msg) -> {
        switch (msg.what) {
            case 1:
                String progress = (String) msg.obj;
                if (myMediaView != null && progress != null) {
                    if (myMediaView.isPlaying()) {
                        int current = myMediaView.getCurrentPosition();
//                            if(Math.abs(Integer.parseInt(progress) - current) > 30){
                        myMediaView.seekTo(Integer.parseInt(progress));
//                            }
//                            myMediaView.seekTo(99333);
                    }
                }
                break;
            default:

                break;
        }
        return false;
    }).get());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetStateChangeReceiver.registerReceiver(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_netty);

        rl_content_main = findViewById(R.id.rl_content_main);
        fixedThreadPool = Executors.newFixedThreadPool(4);

        isLianping = Boolean.parseBoolean(SPUtil.getInstance().getString("isLianping", "false"));
        //测试使用，正常界面输入存储
        final Intent intent = getIntent();
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
                if (isChecking) {
                    checkPlayList(materialIds, sourceType);
                }
            }
        };

        heartTimer.schedule(heartTimerTask, 5000, 60 * 1000);
        simpleDialog = new SimpleDialog(NettyActivity.this, "请重新登录", true);
        simpleDialog.setCanceledOnTouchOutside(false);
        simpleDialog.setCancelable(false);
        simpleDialog.setOnSimpleConfirmListener(new SimpleDialog.OnSimpleConfirmListener() {
            @Override
            public void setSimpleConfirm() {
                Intent intent1 = new Intent(NettyActivity.this, StartActivity.class);
                startActivity(intent1);
                finish();
            }
        });


        mySelfInnerIp = IPUtils.getLocalIpAddress();
        Log.e(TAG, "获取内网ip:" + mySelfInnerIp);
        this.binderSocketService();

        playPic();
        registerListener();

    }

    private Runnable reloginTask = new Runnable() {
        @Override
        public void run() {
            nettyService.initNetty();
        }
    };

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
                        Log.e(TAG, "数据：" + msg.toString());
                        //登录响应
                        if (type == 2) {
                            final LoginResponse loginResponse = new Gson().fromJson(msg.getContent(), LoginResponse.class);
                            int code = loginResponse.getCode();
                            Log.e("nettyactivity", " 登陆响应：" + loginResponse.toString());
                            if (code == 1) {
                                SPUtil.getInstance().saveString("token", loginResponse.getToken());
                                isHearting = true;
                                handler.removeCallbacks(reloginTask);
                                reloginCount = 0;
                                toastMsg("登录成功");
                            } else {
                                isHearting = false;
                                toastMsg("登录错误信息" + loginResponse.getMsg());
                                if (reloginCount < 4) {
                                    handler.postDelayed(reloginTask, 2000);
                                    reloginCount++;
                                }

                            }

                            //指令请求
                        } else if (type == 5) {
                            CommandReceive commandReceive = new Gson().fromJson(msg.getContent(), CommandReceive.class);
                            Log.e("nettyactivity", " 指令请求：" + commandReceive.toString());
                            List<CommandReceive.DeviceInstructionListBean> deviceInstructionListBeans = commandReceive.getDeviceInstructionList();
                            if (deviceInstructionListBeans.size() == 0) {
                                return;
                            }
                            for (int i = 0; i < deviceInstructionListBeans.size(); i++) {
                                String instructionCode = deviceInstructionListBeans.get(i).getInstructionCode();

                                //关机
                                if ("A006-02".equals(instructionCode)) {
//                                    MyApplication.getInstance().setPowerOff();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //重启
                                } else if ("A006-03".equals(instructionCode)) {
//                                    MyApplication.getInstance().reboot();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //截屏
                                } else if ("A006-06".equals(instructionCode)) {
                                    screenShot("A006-06", deviceInstructionListBeans.get(i).getInstructionId());
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //打开屏幕
                                } else if ("A006-07".equals(instructionCode)) {
                                    MyApplication.getInstance().setScreenOn();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //关闭屏幕
                                } else if ("A006-08".equals(instructionCode)) {
                                    MyApplication.getInstance().setScreenOff();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                } else if ("A006-09".equals(instructionCode)) {
                                    try {
                                        getMaterialList(deviceInstructionListBeans.get(i).getInstructionId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //音量加
                                } else if ("A006-16".equals(instructionCode)) {
                                    MyApplication.getInstance().raiseVolume();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //音量减
                                } else if ("A006-17".equals(instructionCode)) {
                                    MyApplication.getInstance().lowVolume();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //播放
                                } else if ("A006-04".equals(instructionCode)) {


                                    if (myMediaView != null)
                                        myMediaView.start();

                                    if (newVideoPlayer != null)
                                        newVideoPlayer.startPlay();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //暂停播放
                                } else if ("A006-05".equals(instructionCode)) {
                                    if (myMediaView != null)
                                        myMediaView.pause();

                                    if (newVideoPlayer != null)
                                        newVideoPlayer.pausePlay();
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //开始轮播
                                } else if ("A006-12".equals(instructionCode)) {
                                    if (!isFront) movetoFront();
                                    if (newVideoPlayer != null)
                                        newVideoPlayer.setCircleplay();
//                                    if (isLianping) {
//
//                                        //通知其他人
//                                        String message = "remove:" + mySelfInnerIp;
//                                        sendBroad(message, myMainIp);
//                                    }
                                    isLianping = false;
                                    SPUtil.getInstance().saveString("isLianping", "false");
                                    materialIds = deviceInstructionListBeans.get(i).getMaterialIds();
                                    Log.e(TAG, "轮播素材：" + materialIds);
                                    sourceType = deviceInstructionListBeans.get(i).getType();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            checkPlayList(materialIds, sourceType);
                                        }
                                    });
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //停止轮播
                                } else if ("A006-13".equals(instructionCode)) {
                                    if (newVideoPlayer != null)
                                        newVideoPlayer.stopCircleplay();
                                    //图片停止轮播
                                    if (bannerView != null) {
                                        bannerView.stopCircle();
                                    }

                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //视频进度调节
                                } else if ("A006-14".equals(instructionCode)) {
                                    String progress = deviceInstructionListBeans.get(i).getValue();

                                    if (!TextUtils.isEmpty(progress)) {

                                        int pg = Integer.parseInt(progress);

                                        if (newVideoPlayer != null) {
                                            int total = newVideoPlayer.getTotalProgress();
                                            newVideoPlayer.seekTo((total * pg) / 100);
                                        }

                                        if (myMediaView != null) {
                                            int total = myMediaView.getDuration();
                                            myMediaView.seekTo((total * pg) / 100);
                                        }


                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
                                    //关闭播放
                                } else if ("A006-15".equals(instructionCode)) {
                                    if (myMediaView != null)
                                        myMediaView.pause();

                                    if (newVideoPlayer != null)
                                        newVideoPlayer.pausePlay();

                                    if (bannerView != null) {
                                        bannerView.stopCircle();
                                    }
                                    moveToBack();

                                } else if ("A006-19".equals(instructionCode)) {

                                    if (newVideoPlayer != null) {
                                        if (!newVideoPlayer.isPlaying()) {
                                            newVideoPlayer.startPlay();
                                        }
                                    }

                                    if (myMediaView != null) {
                                        if (!myMediaView.isPlaying()) {
                                            myMediaView.start();
                                        }
                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");

                                    //更新同步组指令 A006-19播放/A006-20暂停/A006-22停止 / type = 9素材下发
                                } else if ("A006-20".equals(instructionCode)) {

                                    if (newVideoPlayer != null) {
                                        if (newVideoPlayer.isPlaying()) {
                                            newVideoPlayer.pausePlay();
                                        }
                                    }
                                    if (myMediaView != null) {
                                        if (myMediaView.isPlaying()) {
                                            myMediaView.pause();
                                        }
                                    }
                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");


                                    //更新同步组指令 A006-19播放/A006-20暂停/A006-22停止 / type = 9素材下发
                                } else if ("A006-21".equals(instructionCode)) {
                                    materialIds = deviceInstructionListBeans.get(i).getValue();
                                    Log.e(TAG, "删除素材：" + materialIds);
                                    deleteMetiarial(deviceInstructionListBeans.get(i).getInstructionId(), materialIds);
                                } else if("A006-22".equals(instructionCode)){
                                    MyApplication.getInstance().mute();
                                } else if("A006-22".equals(instructionCode)){
                                    MyApplication.getInstance().unMute();
                                } else if ("A006-25".equals(instructionCode)) {

//                                    if (myMediaView != null) {
//                                        if (myMediaView.isPlaying()) {
//                                            myMediaView.pause();
//                                        }
//                                    }
//
//                                    String original = SPUtil.getInstance().getString("liandongVideo", "");
//                                    if (!TextUtils.isEmpty(original)) {
//                                        File file1 = new File(SDCardFileUtils.getLiandongRootDir() + File.separator + original);
//                                        if (file1 != null && file1.exists()) {
//                                            file1.delete();
//                                        }
//                                        SPUtil.getInstance().saveString("liandongVideo", "");
//                                    }
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            playPic();
//                                        }
//                                    });
//                                    nettyService.sendCommandResponse(deviceInstructionListBeans.get(i).getInstructionId(), "1");
//                                    //更新同步组指令 A006-19播放/A006-20暂停/A006-22停止 / type = 9素材下发
                                }

                            }

                            //指令响应
                        } else if (type == 6) {

                            //播放器心跳请求
                        } else if (type == 7) {

                            //播放器心跳响应
                        } else if (type == 8) {
                            HeartbeatResponse heartbeatResponse = new Gson().fromJson(msg.getContent(), HeartbeatResponse.class);
                            String code = heartbeatResponse.getCode();
                            Log.e("nettyactivity", " 心跳响应：" + heartbeatResponse.toString());
                            if ("2".equals(code)) {

                            } else {
                                toastMsg(heartbeatResponse.getMsg());
                            }
                            //轮播联播任务
                        } else if (type == 9) {
                            final LiandongReceive liandongReceive = new Gson().fromJson(msg.getContent(), LiandongReceive.class);
                            liandongIps = liandongReceive.getDevices();
                            isLianping = true;
                            SPUtil.getInstance().saveString("isLianping", "true");
                            if (SDCardFileUtils.isFileExists(SDCardFileUtils.getLiandongRootDir(), liandongReceive.getName())) {
                                currentLPVideoName = liandongReceive.getName();
                                SPUtil.getInstance().saveString("liandongVideo", currentLPVideoName);
                                final String path = SDCardFileUtils.getLiandongRootDir() + File.separator + liandongReceive.getName();
                                //播放
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        nettyService.sendLPCommandResponse(liandongReceive.getScreenDeviceId(), 3);
                                        playLianVideo(path);
                                    }
                                });
//                                confirm(liandongIps);
                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        playPic();
                                    }
                                });
                                //下载
                                String ip = SPUtil.getInstance().getString("ip", "");
                                String[] IP = ip.split(":");
                                //下载图片素材
                                final String downurl = "http://" + IP[0] + ":9000" + "/exhibit-browser" + liandongReceive.getPath();
                                Log.e(TAG, "下载素材" + downurl);
                                fixedThreadPool.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        liandongDownload(downurl, SDCardFileUtils.getLiandongRootDir(),
                                                "downloading" + liandongReceive.getName(), liandongReceive.getScreenDeviceId());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void sendMsg(final String msg) {

                        toastMsg(msg);
                        //弹窗重新登录
                        isHearting = false;
                        if (reloginCount < 4) {
                            handler.postDelayed(reloginTask, 2000);
                            reloginCount++;
                        }

                    }
                });
                nettyService.initNetty();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        Intent intent = new Intent(this, NettyService.class);

        String ip = SPUtil.getInstance().getString("ip", "");
        if (!TextUtils.isEmpty(ip)) {
            if (ip.contains(":")) {
                String[] srings = ip.split(":");
                if (!TextUtils.isEmpty(srings[1])) {
                    intent.putExtra("ip", srings[0]);
                    intent.putExtra("port", srings[1]);
                    this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                } else {
                    toastMsg("端口号为空");
                }
            } else {
                toastMsg("端口号为空");
            }
        } else {
            toastMsg("ip地址为空");
        }

    }


    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
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

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                break;
        }

    }


    @Override
    public void notifyUpdate(final NotifyObject obj) {
        switch (obj.what) {
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NettyActivity.this, obj.str, Toast.LENGTH_SHORT).show();
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
        // 清除原页面的媒体播放
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }

        handleViewers.clear();

        //布局
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

        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_content_main.addView(layout);
        rl_content_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }

    }


    private void playVideoItem(List<String> paths) {
        // 清除原页面的媒体播放
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }
        newVideoPlayer = null;
        myMediaView = null;
        bannerView = null;
        webShower = null;
        handleViewers.clear();

        for (int i = 0; i < paths.size(); i++) {
            Log.e(TAG, "视频路径" + paths.get(i));
        }
        //布局
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //-----------------可添加多个控件
        NewVideo newVideo = new NewVideo();
        newVideo.setSilence((byte) 0);
        newVideo.setPanel((byte) 1);

        newVideo.setRawPathList(paths);

        newVideo.setWidth(widthPixels);
        newVideo.setHeight(heightPixels);

        newVideoPlayer = new NewVideoPlayer(this, newVideo, layout, 1);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(newVideoPlayer, lp1);
        handleViewers.add(newVideoPlayer);


        // ------------------

        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }
        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_content_main.addView(layout);
        rl_content_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));

        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }
    }


    private void playLianVideo(String path) {
        // 清除原页面的媒体播放
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }
        newVideoPlayer = null;
        myMediaView = null;
        bannerView = null;
        webShower = null;
        handleViewers.clear();


        //布局
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        myMediaView = new MyMediaView(this, path, widthPixels, heightPixels, 0, 0);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(myMediaView, lp1);
        handleViewers.add(myMediaView);


        // ------------------

        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }
        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_content_main.addView(layout);
        rl_content_main.removeView(oldLayout);
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
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        for (int i = 0; i < paths.size(); i++) {
            Log.e(TAG, "图片路径" + paths.get(i));
        }
        newVideoPlayer = null;
        myMediaView = null;
        bannerView = null;
        webShower = null;
        handleViewers.clear();

        //布局
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        NewPicture picture = new NewPicture();
        picture.setWidth(widthPixels);
        picture.setHeight(heightPixels);
        picture.setSwitchTime(5);

        picture.setRawPathList(paths);

        bannerView = new NewBannerView(this, picture, layout);

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

        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_content_main.addView(layout);
        rl_content_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }

    }


    private void playWebItem(String url) {
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }

        newVideoPlayer = null;
        myMediaView = null;
        bannerView = null;
        webShower = null;
        handleViewers.clear();

        //布局
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webShower = new WebShower(NettyActivity.this, url);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0, 0, 0, 0);

        layout.addView(webShower, lp1);
        handleViewers.add(webShower);


        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        rl_content_main.addView(layout);
        rl_content_main.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }
    }

    @Override
    protected void onPause() {
        NetStateChangeReceiver.unRegisterObserver(this);
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(isFinishing());
        }
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        super.onPause();
    }


    @Override
    protected void onResume() {
        NetStateChangeReceiver.registerObserver(this);
        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }
        isFront = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        isFront = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backDialog != null && backDialog.isShowing()) backDialog.dismiss();
        NetStateChangeReceiver.unRegisterReceiver(this);
        handler.removeCallbacks(reloginTask);
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
        if (mSocket != null) {
            mSocket.close();
        }
        unRegisterListener();
        for (ViewerCallback v : handleViewers) {
            v.viewerOnDestroy();
        }
        if (bannerView != null) bannerView = null;
        if (newVideoPlayer != null) newVideoPlayer = null;
        if (myMediaView != null) myMediaView = null;
        unbindService(serviceConnection);
        Intent intent = new Intent(getApplicationContext(), NettyService.class);
        stopService(intent);
    }


    private void checkPlayList(String ids, String type) {
        Log.e(TAG, "播放素材id：" + ids + " 类型" + type);
        if (!TextUtils.isEmpty(ids)) {
            playIds = ids.split(",");
            playPaths.clear();
            int count = playIds.length;
            if ("0".equals(type)) {
                //判断素材是否存在，存在加入播放列表
                for (int i = 0; i < playIds.length; i++) {
                    for (int j = 0; j < materialListBeans.size(); j++) {
                        try {
                            Log.e(TAG, "播放素材id:" + playIds[i] + "总的素材id" + materialListBeans.get(j).getId());
                            if (playIds[i].equals(materialListBeans.get(j).getId() + "")) {

                                if (SDCardFileUtils.isFileExists(SDCardFileUtils.getPictureRootDir(NettyActivity.this), materialListBeans.get(j).getName())) {
                                    playPaths.add(SDCardFileUtils.getPictureRootDir(NettyActivity.this) + File.separator + materialListBeans.get(j).getName());
                                } else {
                                    //去下载或者默认不管

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (playPaths.size() > 0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playImageItem(playPaths);
                        }
                    });
                }
                if (count > playPaths.size()) {
                    isChecking = true;
                } else {
                    isChecking = false;
                }
            } else if ("1".equals(type)) {

                for (int i = 0; i < playIds.length; i++) {

                    for (int j = 0; j < materialListBeans.size(); j++) {
                        if (playIds[i].equals(materialListBeans.get(j).getId() + "")) {

                            if (SDCardFileUtils.isFileExists(SDCardFileUtils.getVideoRootDir(NettyActivity.this), materialListBeans.get(j).getName())) {
                                playPaths.add(SDCardFileUtils.getVideoRootDir(NettyActivity.this) + File.separator + materialListBeans.get(j).getName());
                            } else {
                                //去下载或者默认不管

                            }
                        }
                    }
                }

                if (playPaths.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playVideoItem(playPaths);
                        }
                    });
                }
                if (count > playPaths.size()) {
                    isChecking = true;
                } else {
                    isChecking = false;
                }
            } else {
                for (int j = 0; j < materialListBeans.size(); j++) {
                    try {
                        Log.e(TAG, "播放素材id:" + playIds[0] + "总的素材id" + materialListBeans.get(j).getId());
                        if (playIds[0].equals(materialListBeans.get(j).getId() + "")) {
                            if (SDCardFileUtils.isFileExists(SDCardFileUtils.getWebviewRootDir(NettyActivity.this), materialListBeans.get(j).getName())) {
                                //读文件播放
                                File file = new File(SDCardFileUtils.getWebviewRootDir(NettyActivity.this), materialListBeans.get(j).getName());
                                String url = FileUtils.readTxtFile(file);
                                Log.e(TAG, "播放网页地址为:" + url);
                                playWebItem(url);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }


    //获取素材列表
    private void getMaterialList(final int instructionId) {
        Log.e(TAG, "IP地址：" + "http://" + SPUtil.getInstance().getString("ip", "") + "/exhibit-browser/pubPlayerController/materialList/");
        String ip = SPUtil.getInstance().getString("ip", "");
        String[] IP = ip.split(":");

        Ok.post().url("http://" + IP[0] + ":9000/exhibit-browser/public/pubPlayerController/materialList/")
                .param("machine_code", HardwareUtils.getMachineCode())
                .header("token", SPUtil.getInstance().getString("token", ""))
                .build()
                .call(new JsonCallBack<String>() {
                    @Override
                    public void fail(final Exception e) {
                        Log.e(TAG, "获取素材列表报错：" + e.toString());
                    }

                    @Override
                    public void success(final String json) {


                        Log.e("nettyactivity", "获取素材列表回调" + json);
                        if (TextUtils.isEmpty(json)) {
                            return;
                        } else {
                            Gson gson = new Gson();
                            materialListBeans = gson.fromJson(json, new TypeToken<List<MaterialListBean>>() {
                            }.getType());

                            for (int i = 0; i < materialListBeans.size(); i++) {

                                //0表示图片素材     1 表示视频素材  2 网页素材

                                final MaterialListBean materialListBean = materialListBeans.get(i);
                                int type = materialListBean.getType();
                                if (type == 0) {

                                    if (SDCardFileUtils.isFileExists(SDCardFileUtils.getPictureRootDir(NettyActivity.this), materialListBean.getName())) {
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "100%", "5");
                                    } else {

                                        String ip = SPUtil.getInstance().getString("ip", "");
                                        String[] IP = ip.split(":");
                                        //下载图片素材
                                        final String downurl = "http://" + IP[0] + ":9000" + "/exhibit-browser" + materialListBean.getPath();
                                        Log.e(TAG, "下载素材" + downurl);
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "0%", "4");
                                        fixedThreadPool.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                download(downurl, SDCardFileUtils.getPictureRootDir(NettyActivity.this), "downloading" + materialListBean.getName(), materialListBean.getId(), instructionId);
                                            }
                                        });


                                    }


                                } else if (type == 1) {

                                    if (SDCardFileUtils.isFileExists(SDCardFileUtils.getVideoRootDir(NettyActivity.this), materialListBean.getName())) {
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "100%", "5");
                                    } else {

                                        String ip = SPUtil.getInstance().getString("ip", "");
                                        String[] IP = ip.split(":");
                                        //下载视频素材
                                        final String downurl = "http://" + IP[0] + ":9000" + Constants.SERVER_IP + materialListBean.getPath();
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "0%", "4");
                                        fixedThreadPool.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                String threadName = Thread.currentThread().getName();
                                                download(downurl, SDCardFileUtils.getVideoRootDir(NettyActivity.this), "downloading" + materialListBean.getName(), materialListBean.getId(), instructionId);
                                            }
                                        });
                                    }

                                } else if (type == 2) {

                                    if (SDCardFileUtils.isFileExists(SDCardFileUtils.getVideoRootDir(NettyActivity.this), materialListBean.getName())) {
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "100%", "5");
                                    } else {

                                        String ip = SPUtil.getInstance().getString("ip", "");
                                        String[] IP = ip.split(":");
                                        //下载视频素材
                                        final String downurl = "http://" + IP[0] + ":9000" + Constants.SERVER_IP + materialListBean.getPath();
                                        nettyService.sendDowloadResponse(instructionId, (long) materialListBean.getId(), "0%", "4");
                                        fixedThreadPool.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                String threadName = Thread.currentThread().getName();
                                                download(downurl, SDCardFileUtils.getWebviewRootDir(NettyActivity.this), "downloading" + materialListBean.getName(), materialListBean.getId(), instructionId);
                                            }
                                        });
                                    }

                                }

                            }


                            //本地有的而素材列表没有的要遍历删除

                            File picfile = new File(SDCardFileUtils.getPictureRootDir(NettyActivity.this));
                            File[] picFiles = picfile.listFiles();
                            if (picFiles != null) {
                                for (int i = 0; i < picFiles.length; i++) {
                                    // 判断是否为文件夹
                                    if (picFiles[i].isFile()) {
                                        String filename = picFiles[i].getName();
                                        int count = 0;
                                        for (int j = 0; j < materialListBeans.size(); j++) {

                                            if (materialListBeans.get(j).getName().equals(filename)) {
                                                count++;
                                            }
                                        }
                                        if (count == 0) {
                                            picFiles[i].delete();
                                        }

                                    }
                                }
                            }


                            File videofile = new File(SDCardFileUtils.getVideoRootDir(NettyActivity.this));
                            File[] videofiles = videofile.listFiles();

                            if (videofiles != null) {
                                for (int i = 0; i < videofiles.length; i++) {
                                    // 判断是否为文件夹
                                    if (videofiles[i].isFile()) {
                                        String filename = videofiles[i].getName();
                                        int count = 0;
                                        for (int j = 0; j < materialListBeans.size(); j++) {

                                            if (materialListBeans.get(j).getName().equals(filename)) {
                                                count++;
                                            }
                                        }
                                        if (count == 0) {
                                            videofiles[i].delete();
                                        }
                                    }
                                }
                            }
                        }

                        if (isFirstPlayDefault) {
                            getDefaultPlayList();
                            isFirstPlayDefault = false;
                        }

                    }
                });
    }


    private void download(String url, final String localpath, final String filename, final int metarialId, final int instructionId) {

        Ok.download().url(url)
                .build()
                .tag(MyApplication.getInstance().getApplicationContext())
                .call(new FileCallBack(localpath, filename) {
                    @Override
                    public void progress(int progress) {

                        Log.e("TestDownload", "下载到进度：" + progress);
                    }

                    @Override
                    public void success(File file) {

                        String newname = filename.substring(11);
                        file.renameTo(new File(localpath, newname));
                        nettyService.sendDowloadResponse(instructionId, (long) metarialId, "100%", "5");
                        Log.e("TestDownload", "下载成功：");
                    }

                    @Override
                    public void fail(Exception e) {
                        Log.e("TestDownload", "下载失败了：" + e.toString());
                        nettyService.sendDowloadResponse(instructionId, (long) metarialId, "0%", "4");
                        final String failReason = e.toString();
                    }
                });
    }

    private void liandongDownload(String url, final String localpath, final String filename, final int screenDeviceId) {
        nettyService.sendLPCommandResponse(screenDeviceId, 1);
        Ok.download().url(url)
                .build()
                .tag(MyApplication.getInstance().getApplicationContext())
                .call(new FileCallBack(localpath, filename) {
                    @Override
                    public void progress(int progress) {
                        Log.e("TestDownload", "下载到进度：" + progress);
                    }

                    @Override
                    public void success(File file) {
                        Log.e("TestDownload", "下载成功：");
                        String newname = filename.substring(11);
                        file.renameTo(new File(localpath, newname));
                        nettyService.sendLPCommandResponse(screenDeviceId, 2);
                        if (isLianping) {
                            currentLPVideoName = newname;
                            SPUtil.getInstance().saveString("liandongVideo", currentLPVideoName);
                            String path = SDCardFileUtils.getLiandongRootDir() + File.separator + newname;
                            nettyService.sendLPCommandResponse(screenDeviceId, 3);
                            playLianVideo(path);
//                            confirm(liandongIps);
                        }

                    }

                    @Override
                    public void fail(Exception e) {
                        Log.e("TestDownload", "下载失败了：" + e.toString());
                        final String failReason = e.toString();

                    }
                });

    }

    //截屏
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
                    // 上传
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

                    // 上传
                    upload(uploadType, instructId, pathName);
                }
            }, 4000);

        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3188) {
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);

            // 截屏文件名 文件格式实际是png!!
            myShixinMethod.screenShot();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameY(fname);

                    // 上传
                    upload(uploadType, instructId, pathName);

                }
            }, 4000);

        } else if (FactoryCategoryUtils.getFactoryCategory() == FactoryCategoryUtils.SHIXIN_RK3288) {

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileUtils.deleteDir(dir);

            // 截屏文件名 文件格式实际是png!!
            myShixinMethod.screenShot();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String fname = "screenshot.jpg";
                    String pathName = FileUtils.getFilePathNameY(fname);
                    // 上传

                    upload(uploadType, instructId, pathName);

                }
            }, 4000);
        }


    }

    private void getDefaultPlayList() {
        String ip = SPUtil.getInstance().getString("ip", "");
        String[] IP = ip.split(":");

        Ok.get().url("http://" + IP[0] + ":9000/exhibit-browser/public/terminalMaterial/listByMac/" + HardwareUtils.getLocalMac())
                .build()
                .call(new JsonCallBack<DefaultPlayBean>() {
                    @Override
                    public void fail(Exception e) {
                        Log.e("nettyactivity", "获取默认播放器列表失败");
                    }

                    @Override
                    public void success(DefaultPlayBean defaultPlayBean) {
                        Log.e(TAG, "获取默认播放列表" + defaultPlayBean.toString());
                        DefaultPlayBean.BodyBean bodyBean = defaultPlayBean.getBody();
                        if (bodyBean == null) return;
                        String materialIds = bodyBean.getMaterialIds();
                        String sourceType = bodyBean.getType();
                        checkPlayList(materialIds, sourceType);

                    }
                });
    }


    private void upload(String uploadType, final int instructId, String pathName) {

        String ip = SPUtil.getInstance().getString("ip", "");
        String[] IP = ip.split(":");

        Ok.post().url("http://" + IP[0] + ":9000/exhibit-browser/public/uploadSingle")
                .param("uploadType", uploadType)
                .param("instructId", instructId)
                .file("file", new File(pathName))
                .header("token", SPUtil.getInstance().getString("token", ""))
                .build()
                .call(new CallBack() {
                    @Override
                    public void fail(Exception e) {
                        Log.e("upload", "报错:" + e.toString());
                        nettyService.sendCommandResponse(instructId, "2");
                    }

                    @Override
                    public void success(String response) {
                        Log.e("upload", "成功:" + response.toString());
                        nettyService.sendCommandResponse(instructId, "1");
                    }
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backDialog = new SimpleDialog(NettyActivity.this, "是否退出登录", true);
            backDialog.setCancelable(false);
            backDialog.setCanceledOnTouchOutside(false);
            backDialog.setOnSimpleConfirmListener(new SimpleDialog.OnSimpleConfirmListener() {
                @Override
                public void setSimpleConfirm() {
                    Intent intent2 = new Intent(NettyActivity.this, StartActivity.class);
                    startActivity(intent2);
                    finish();
                }
            });
            backDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void confirm(final List<String> liandongIps) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < liandongIps.size(); i++) {
                    try {
                        if (IPUtils.ping(liandongIps.get(i))) {
                            myMainIp = liandongIps.get(i);
                            Log.e("player", "找到了主机，主机ip是：" + myMainIp);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(myMainIp)) {

                    boolean isGroupChild = false;

                    for (int i = 0; i < liandongIps.size(); i++) {
                        if (mySelfInnerIp.equals(liandongIps.get(i))) {
                            isGroupChild = true;
                        }
                        Log.e("ipip", "我的ip" + mySelfInnerIp + "子ip" + liandongIps.get(i));
                    }

                    if (myMainIp.equals(mySelfInnerIp)) {

                        send(myMainIp);
                        receive(myMainIp);

                    } else {

                        if (isGroupChild) {
                            cancelSend();
                            receive(myMainIp);
                        } else {
                            cancelSend();
                            if (mSocket != null) {
                                if (mSocket.isClosed()) {
                                    mSocket = null;
                                } else {
                                    mSocket.close();
                                    mSocket = null;
                                }
                            }
                        }


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

    private void send(String ip) {
        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer = null;
        }
        if (updateProgressTask != null) {
            updateProgressTask.cancel();
            updateProgressTask = null;
        }

        updateProgressTimer = new Timer();
        updateProgressTask = new NettyActivity.UpdateProgressTask(ip);
        updateProgressTimer.schedule(updateProgressTask, 5 * 1000L, 60 * 1000);
    }

    private void receive(final String mainIp) {
        new Thread() {
            @Override
            public void run() {

                if (TextUtils.isEmpty(mainIp)) {
                    return;
                }

                if (mSocket != null) {
                    if (mSocket.isClosed()) {
                        mSocket = null;
                    } else {
                        mSocket.close();
                        mSocket = null;
                    }
                }
                String[] strs = mainIp.split("\\.");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                Log.e("player", "接受消息" + " mainIp:" + mainIp + "strs:" + strs.length);
                while (isAccepting) {
                    try {
                        if (mSocket == null) {
                            try {
                                mSocket = new MulticastSocket(30001);
                                InetAddress group = InetAddress.getByName("239.0.0." + strs[3]);//设定多播IP
                                mSocket.joinGroup(group);//加入多播组，发送方和接受方处于同一组时，接收方可抓取多播报文信息
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }
                        }
                        mSocket.receive(packet);
                        String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        Log.e("playertongbu", "接受到的消息：" + s);

                        if (s.contains("progress")) {
                            String[] strings = s.split(":");

                            if (!currentLPVideoName.equals(strings[1])) {
                                return;
                            }
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            msg.obj = strings[2];

                            if (isLianping) {
                                mHandler.sendMessage(msg);
                            }

                        }
//                        else if (s.contains("remove")) {
//                            String[] strings = s.split(";");
//                            if (strings[1].equals(mySelfInnerIp)) {
//                                cancelSend();
//                                if (mSocket != null) {
//                                    if (mSocket.isClosed()) {
//                                        mSocket = null;
//                                    } else {
//                                        mSocket.close();
//                                        mSocket = null;
//                                    }
//                                }
//
//                            } else {
//                                liandongIps.remove(strings[1]);
//                                confirm(liandongIps);
//                            }
//
//                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onNetDisconnected() {

    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        reloginCount = 2;
        handler.postDelayed(reloginTask, 1000);
        Log.e(TAG,"重连中");
    }


    class UpdateProgressTask extends TimerTask {
        String mMainIp;

        public UpdateProgressTask(String mainIp) {
            mMainIp = mainIp;
        }

        @Override
        public void run() {
            try {
                if (TextUtils.isEmpty(mMainIp)) {
                    return;
                }

                String[] strings = mMainIp.split("\\.");

                if (myMediaView == null) {
                    return;
                }

                String message = "progress:" + currentLPVideoName + ":" + myMediaView.getCurrentPosition() + "";

                MulticastSocket sendSocket = new MulticastSocket(30001);//生成套接字并绑定30001端口
                InetAddress group = InetAddress.getByName("239.0.0." + strings[3]);//设定多播IP

                sendSocket.joinGroup(group);//加入多播组，发送方和接受方处于同一组时，接收方可抓取多播报文信息
                sendSocket.setTimeToLive(4);//设定TTL
                //设定UDP报文（内容，内容长度，多播组，端口）
                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, group, 30001);
                sendSocket.send(packet);//发送报文
                sendSocket.close();//关闭套接字

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendBroad(final String message, final String mainIp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(mainIp)) {
                        return;
                    }
                    String[] strings = mainIp.split("\\.");

                    MulticastSocket sendSocket = new MulticastSocket(30001);//生成套接字并绑定30001端口
                    InetAddress group = InetAddress.getByName("239.0.0." + strings[3]);//设定多播IP

                    sendSocket.joinGroup(group);//加入多播组，发送方和接受方处于同一组时，接收方可抓取多播报文信息
                    sendSocket.setTimeToLive(4);//设定TTL
                    //设定UDP报文（内容，内容长度，多播组，端口）
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, group, 30001);
                    sendSocket.send(packet);//发送报文
                    sendSocket.close();//关闭套接字

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void removeElement(List<String> list, String target) {
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String item = iter.next();
            if (item.equals(target)) {
                iter.remove();
            }
        }

    }


    private void deleteMetiarial(int instructionId, String materialIds) {
        if (!TextUtils.isEmpty(materialIds)) {
            String[] deleteIds = materialIds.split(",");
            for (int i = 0; i < deleteIds.length; i++) {
                for (int j = 0; j < materialListBeans.size(); j++) {
                    try {
                        Log.e(TAG, "删除素材id:" + deleteIds[i] + "总的素材id" + materialListBeans.get(j).getId());
                        if (deleteIds[i].equals(materialListBeans.get(j).getId() + "")) {

                            if (SDCardFileUtils.isFileExists(SDCardFileUtils.getPictureRootDir(NettyActivity.this), materialListBeans.get(j).getName())) {
                                File f = new File(SDCardFileUtils.getPictureRootDir(NettyActivity.this), materialListBeans.get(j).getName());
                                f.delete();
                            }
                            nettyService.sendDowloadResponse(instructionId, Long.parseLong(deleteIds[i]), "", "9");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    private boolean isFront;

    private void movetoFront() {

        if (!isRunningForeground(this)) {
            //获取ActivityManager
            ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if (rti.topActivity.getPackageName().equals(getPackageName())) {
                    mAm.moveTaskToFront(rti.id, 0);
                    return;
                }
            }
            //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
            Intent resultIntent = new Intent(NettyActivity.this, NettyActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(resultIntent);
        }

    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveToBack() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
