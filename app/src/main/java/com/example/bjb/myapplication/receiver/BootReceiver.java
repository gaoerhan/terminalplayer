package com.example.bjb.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {

	static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	static final String MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
	static final String MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.e("appstart","bootreceiver1" + "开机启动了码");
		if (intent.getAction().equals(MEDIA_MOUNTED)) {
				Log.e("appstart","bootreceiver2" + "开机启动了码");

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					context.startForegroundService(new Intent(context, MyService.class));
				} else {
					context.startService(new Intent(context, MyService.class));
				}

		}

	}

}
