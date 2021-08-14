package com.example.bjb.myapplication.receiver;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.bjb.myapplication.activity.StartActivity;


public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(){
            @Override
            public void run() {

                try {
                    Thread.sleep(8000);//休眠3秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    Intent newIntent = new Intent(getApplicationContext(), StartActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失�?
                    getApplicationContext().startActivity(newIntent);
                Log.e("appstart","myservice" + "开机启动了码");



            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(11,new Notification());
            //这个id不要和应用内的其他同志id一样，不行就写 int.maxValue()
         }

    }
}
