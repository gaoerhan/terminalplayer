package com.example.bjb.myapplication.view.widget;

import android.os.Handler;
import android.widget.TextView;


public class CodeTimer {
    private int time = 15;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView codeTv;
    private VerificationDialog verificationDialog;

    public CodeTimer(TextView codeTv, VerificationDialog verificationDialog) {
        super();
        this.codeTv = codeTv;
        this.verificationDialog = verificationDialog;
        initTimer();
    }

    private void initTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                time--;
                if (time == 0) {
                    timerHandler.removeCallbacks(timerRunnable);
                    codeTv.setEnabled(true);
                    time = 15;
//                    codeTv.setText("重新发送");
                    verificationDialog.onVerificationCallBack();
                } else {
                    codeTv.setText("即将" + time + "s"+"后自动消失");
                    timerHandler.postDelayed(timerRunnable, 1000);
                }
            }
        };
    }

    public void startTimer() {
        codeTv.setEnabled(false);
        codeTv.setText("" + time + "s");
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    public void stopTimer() {
        time = 16;
        codeTv.setEnabled(true);
        codeTv.setText("获取验证码");
        timerHandler.removeCallbacks(timerRunnable);
    }

}
