package com.example.bjb.myapplication.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.bjb.myapplication.activity.NettyActivity;
import com.example.bjb.myapplication.socket.entity.CommandResponse;
import com.example.bjb.myapplication.socket.entity.HeartbeatRequest;
import com.example.bjb.myapplication.socket.entity.LPCommandResponse;
import com.example.bjb.myapplication.socket.handler.IODisposeHandler;
import com.example.bjb.myapplication.socket.handler.MyEncoder;
import com.example.bjb.myapplication.socket.handler.MyProtocolBean;
import com.example.bjb.myapplication.socket.handler.MyProtocolDecoder;
import com.example.bjb.myapplication.utils.HardwareUtils;
import com.example.bjb.myapplication.utils.SPUtil;
import com.google.gson.Gson;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


public class NettyService extends Service {


    private static final String TAG = "NettyService";



    private Handler handler = new Handler(Looper.getMainLooper());


    private NetttBinder socketBinder = new NetttBinder();
    private String ip;
    private String port;

    private String msgFromServer;

    private OnMessageListener onMessageListener;

    public class NetttBinder extends Binder {
        public NettyService getService() {
            return NettyService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ip = intent.getStringExtra("ip");
        port = intent.getStringExtra("port");
        return socketBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public void initNetty() {

        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel)  {
                        final ChannelPipeline pipeline = nioSocketChannel.pipeline();

                        //??????????????????$???????????????
//                        pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$".getBytes())));
                        //??????????????????????????????
//                        pipeline.addLast(new FixedLengthFrameDecoder(10));
                        //???????????????????????????
//                        pipeline.addLast(new LineBasedFrameDecoder(1024));


                        pipeline.addLast(new MyProtocolDecoder(Integer.MAX_VALUE, 4, 4, 0, 0, true));

                        pipeline.addLast(new MyEncoder());

                        IODisposeHandler ioDisposeHandler = new IODisposeHandler();
                        ioDisposeHandler.setOnNettyMessageListener(new IODisposeHandler.OnNettyMessageListener() {
                            @Override
                            public void getMessage(MyProtocolBean msg) {
                                try {
                                    if (onMessageListener != null) {
                                         onMessageListener.getMessage(msg);
                                        Log.e(TAG,"??????" + msg.toString());
                                    }
                                }catch (Exception e){
                                    Log.e(TAG,"??????" + e.toString());
                                }

                            }

                            @Override
                            public void getException() {
                                workerGroup.shutdownGracefully();
                                pipeline.close();
                                NettyActivity.isHearting = false;
                                if (onMessageListener != null) {
                                    onMessageListener.sendMsg("????????????" );
                                }
                                Log.e(TAG,"??????111");
//                                initNetty();

                            }
                        });

                        pipeline.addLast(ioDisposeHandler);
                    }


                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        NettyActivity.isHearting = false;
                        try {
                            IODisposeHandler.channel = null;
                            ctx.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"?????????" + e.toString());
                        } finally {
                        NettyActivity.isHearting =false;
                            if (onMessageListener != null) {
                                onMessageListener.sendMsg("??????????????????" );
                            }

//                            initNetty();
                        }

                    }
                });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChannelFuture future = null;
                    future = bootstrap.connect(ip, Integer.parseInt(port)).sync();

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }finally {

                        NettyActivity.isHearting = false;
                        if (onMessageListener != null) {
                            onMessageListener.sendMsg("????????????" + ip);
                        }
                        Log.e(TAG,"?????????" + e.toString());
                        //???????????????????????????
//                        initNetty();

                    }


                }
            }
        }).start();


    }



    //??????????????????
    public void sendCommandResponse(int instructionId ,String result) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setCommand(6);
        commandResponse.setInstructionId(instructionId);
        commandResponse.setResult(result);
        String content = new Gson().toJson(commandResponse);
        IODisposeHandler.channel.writeAndFlush(new MyProtocolBean(6, content.getBytes().length, content));
    }
    //?????????????????? result 4 0%    5 100%
    public void sendDowloadResponse(int instructionId,long materialId ,String downloadProgress,String result) {
        CommandResponse commandResponse = new CommandResponse();
        commandResponse.setCommand(6);
        commandResponse.setInstructionId(instructionId);
        commandResponse.setMaterialId(materialId);
        commandResponse.setDownloadProgress(downloadProgress);
        commandResponse.setResult(result);
        String content = new Gson().toJson(commandResponse);
        IODisposeHandler.channel.writeAndFlush(new MyProtocolBean(6, content.getBytes().length, content));
    }

    //????????????????????????
    public void sendLPCommandResponse( int screenDeviceId,int screenDeviceStatus) {
        LPCommandResponse commandResponse = new LPCommandResponse();
        commandResponse.setCommand(6);
        commandResponse.setScreenDeviceId(screenDeviceId);
        commandResponse.setScreenDeviceStatus(screenDeviceStatus);
        String content = new Gson().toJson(commandResponse);
        IODisposeHandler.channel.writeAndFlush(new MyProtocolBean(6, content.getBytes().length, content));
    }


    public void sendHeart() {
        HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
        heartbeatRequest.setCommand("7");
        heartbeatRequest.setCpu(HardwareUtils.getCPUUsage());
        heartbeatRequest.setDisk(HardwareUtils.getAvailableSize()+"");
        heartbeatRequest.setIp(HardwareUtils.getLocalIpAddress());
        heartbeatRequest.setMac(HardwareUtils.getLocalMac());
        heartbeatRequest.setOnline("1");

        heartbeatRequest.setName(SPUtil.getInstance().getString("terminalname",""));

        heartbeatRequest.setToken(SPUtil.getInstance().getString("token",""));
        heartbeatRequest.setMachine_code(HardwareUtils.getMachineCode());
        heartbeatRequest.setRam(HardwareUtils.getMemoryUsage());
        String content = new Gson().toJson(heartbeatRequest);
        IODisposeHandler.channel.writeAndFlush(new MyProtocolBean(7, content.getBytes().length, content));
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





    @Override
    public void onDestroy() {
        super.onDestroy();
        if(IODisposeHandler.channel != null){
            IODisposeHandler.channel.close();
            IODisposeHandler.channel = null;
        }

    }


    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    public interface OnMessageListener {
        void getMessage(MyProtocolBean msg);

        void sendMsg(String msg);
    }
}
