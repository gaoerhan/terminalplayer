package com.example.bjb.myapplication.socket;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.example.bjb.myapplication.socket.entity.LoginRequest;
import com.example.bjb.myapplication.utils.BytesUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    private Socket socket;

    private Thread connectThread;

    private Timer timer = new Timer();
    ;

    private TimerTask timerTask;

    private OutputStream outputStream;

    private boolean isReConnect = true;

    private Handler handler = new Handler(Looper.getMainLooper());


    private SocketBinder socketBinder = new SocketBinder();
    private String ip;
    private String port;

    private String msgFromServer;

    private OnMessageListener onMessageListener;

    public class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
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

    private LoginRequest loginRequest;

    public void initSocket() {


        if (null == socket && null == connectThread) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)), 2000);
                        if (socket.isConnected()) {
                            toastMsg("socket????????????");
                            loginRequest = new LoginRequest();
                            loginRequest.setUsername("gate");
                            loginRequest.setPassword("123456");
                            loginRequest.setTerminalId("019FB49AC06D1DBF9CE2");
                            loginRequest.setTerminalName("?????????");
                            loginRequest.setType("2");
                            //????????????
                            sendOrder(2, new Gson().toJson(loginRequest) + "\r\n");
                            Log.e("socketservice", "?????????" + new Gson().toJson(loginRequest));
                            //????????????????????????
//                            sendBeatData();
                            //?????????????????????

                            new ClientRecThread(socket).start();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                        if (e instanceof SocketTimeoutException) {
                            toastMsg("??????????????????????????? ");
                            releaseSocket();
                        } else if (e instanceof NoRouteToHostException) {
                            toastMsg("??????????????????????????????");
                            stopSelf();
                        } else if (e instanceof ConnectException) {
                            toastMsg("????????????????????????????????????");
                            stopSelf();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        toastMsg("IP???????????? ");
                    }


                }
            });

            //??????????????????
            connectThread.start();
        }


    }

    private void login() {


    }

    public void sendOrder(final int type, final String order) {
        if (socket != null && socket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        if (outputStream != null) {

                            byte[] bytesContent = order.getBytes();

                            byte[] lengs = BytesUtil.int2Bytes(bytesContent.length);

                            byte[] types = BytesUtil.int2Bytes(type);

                            byte[] result = new byte[types.length + lengs.length + bytesContent.length];
                            System.arraycopy(types, 0, result, 0, types.length);
                            System.arraycopy(lengs, 0, result, types.length, lengs.length);
                            System.arraycopy(bytesContent, 0, result, types.length + lengs.length, bytesContent.length);
                            outputStream.write(result);
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            toastMsg("socket????????????????????????");
        }
    }


    private void sendBeatData() {

        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        outputStream.write(new Gson().toJson(loginRequest).getBytes("UTF-8"));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();

                        toastMsg("????????????,????????????");
                        releaseSocket();
                    }

                }
            };


        }

        timer.schedule(timerTask, 0, 10000);
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


    private void releaseSocket() {

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }


        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }

        if (isReConnect) {
            initSocket();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
    }


    // ??????
    public class ClientRecThread extends Thread {
        private Socket socket;
        private InputStream inputStream;

        public ClientRecThread(Socket socket) {

            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {

                if (socket != null) {
                    try {
                        releaseSocket();
                        inputStream.close();
                    } catch (IOException ioe) {
                        // TODO Auto-generated catch block
                        ioe.printStackTrace();
                    }

                }
                toastMsg("??????????????????????????????");
            }
        }

        @Override
        public void run() {
            String str = null;
            int temp;

            byte[] msg = new byte[1024];
            boolean reading = true;
            try {
                while (reading) {
                    if ((temp = inputStream.read(msg)) != -1) {
                        String receiveData = new String(msg, 0, temp);
                        str += receiveData;
                    }
                    if (onMessageListener != null) {
                        onMessageListener.getMessage(str);
                    }

                }

            } catch (IOException e) {

                reading = false;
                if (socket != null) {
                    try {
                        releaseSocket();
                        inputStream.close();
                    } catch (IOException ioe) {
                        // TODO Auto-generated catch block
                        ioe.printStackTrace();
                    }

                }
                toastMsg("??????????????????????????????");
            }
        }
    }


    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }

    public interface OnMessageListener {
        void getMessage(String msg);
    }
}
