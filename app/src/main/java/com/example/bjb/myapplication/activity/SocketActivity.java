package com.example.bjb.myapplication.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.common.observer.INotifyListener;
import com.example.bjb.myapplication.common.observer.NotifyListenerManager;
import com.example.bjb.myapplication.common.observer.NotifyObject;
import com.example.bjb.myapplication.socket.SocketService;

public class SocketActivity extends Activity implements View.OnClickListener, INotifyListener {

    private ServiceConnection serviceConnection;

    private SocketService socketService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);



        this.binderSocketService();
    }

    private void binderSocketService() {


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
                socketService.setOnMessageListener(new SocketService.OnMessageListener() {
                    @Override
                    public void getMessage(final String msg) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("socketservice","返回数据：" + msg);
                                Toast.makeText(SocketActivity.this,"数据：" + msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                socketService.initSocket();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        Intent intent = new Intent(this,SocketService.class);
//        intent.putExtra("ip","172.16.30.231");
//        intent.putExtra("port","8000");
//        intent.putExtra("ip","172.16.203.242");
//        intent.putExtra("port","10086");


        intent.putExtra("ip","172.16.202.32");
        intent.putExtra("port","8000");
        this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
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
    protected void onDestroy() {
        super.onDestroy();

        unRegisterListener();
        unbindService(serviceConnection);
        Intent intent = new Intent(getApplicationContext(),SocketService.class);
        stopService(intent);
    }


    @Override
    public void notifyUpdate(final NotifyObject obj) {
        switch (obj.what) {
            case 1:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SocketActivity.this,obj.str,Toast.LENGTH_SHORT).show();

                    }
                });

                break;
            case 2:


                break;

            default:

                break;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
