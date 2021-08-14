package com.example.bjb.myapplication.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bjb.myapplication.R;


/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class VerificationDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout ll;
    private TextView tvCancel, tvEnter, tvCode;

    private CodeTimer codeTimer;
    private String phoneNum;

    private onVerificationCallBackLinstener onVerificationCallBackLinstener;

    private static final String TAG = "VerificationDialog";

    public void setOnVerificationCallBackLinstener(VerificationDialog.onVerificationCallBackLinstener onVerificationCallBackLinstener) {
        this.onVerificationCallBackLinstener = onVerificationCallBackLinstener;
    }

    public VerificationDialog(@NonNull Context context) {
        super(context, R.style.loading_dialog);
        this.context = context;
    }

    public VerificationDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected VerificationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_verification);
        getWindow().setBackgroundDrawableResource(R.drawable.login_shape_verification);
        initView();
    }


    private void initView() {
        ll = (RelativeLayout) findViewById(R.id.ll);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvEnter = (TextView) findViewById(R.id.tv_enter);
        tvCode = (TextView) findViewById(R.id.tv_code);

        tvCancel.setOnClickListener(this);
        tvEnter.setOnClickListener(this);
        tvCode.setOnClickListener(this);

        tvCancel.setBackgroundResource(R.drawable.btn_loginverity_cancel_selector);
        tvEnter.setBackgroundResource(R.drawable.btn_loginverity_confirm_selector);
        codeTimer = new CodeTimer(tvCode,this);
        codeTimer.startTimer();
        ll.setLayoutParams(new LinearLayout.LayoutParams((int) (((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth() * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            onVerificationCallBackLinstener.onVerificationCallBack();
            codeTimer.stopTimer();
        }
        if (v.getId() == R.id.tv_enter) {
            codeTimer.stopTimer();
            dismiss();
        }
        if (v.getId() == R.id.tv_code) {

        }
    }

    public void onVerificationCallBack(){
        onVerificationCallBackLinstener.onVerificationCallBack();
    }

    public interface onVerificationCallBackLinstener {
        void onVerificationCallBack();
    }


}
