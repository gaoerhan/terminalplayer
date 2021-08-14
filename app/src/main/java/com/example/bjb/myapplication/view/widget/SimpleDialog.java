package com.example.bjb.myapplication.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.bjb.myapplication.R;


public class SimpleDialog extends Dialog implements View.OnClickListener {

    private TextView tv_simple_cancel;
    private TextView tv_simple_confirm;
    private TextView tv_dialog_content;
    private OnSimpleConfirmListener onSimpleConfirmListener;

    public void setOnSimpleConfirmListener(OnSimpleConfirmListener onSimpleConfirmListener) {
        this.onSimpleConfirmListener = onSimpleConfirmListener;
    }

    public SimpleDialog(Context context, String content, boolean hasButton) {
        super(context);
        //加载布局文件

        //R.layout.dialog_content   xml文件
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_content, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(view);
        getWindow().setBackgroundDrawableResource(R.drawable.multipick_layout_shape);
        final WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = 400;
        params.height = 200;
        this.getWindow().setAttributes(params);

        tv_simple_confirm = view.findViewById(R.id.tv_simple_confirm);
        tv_simple_cancel = view.findViewById(R.id.tv_simple_cancel);
        tv_dialog_content = view.findViewById(R.id.tv_dialog_content);
        tv_dialog_content.setText(content);

        if(hasButton){
            tv_simple_confirm.setVisibility(View.VISIBLE);
            tv_simple_cancel.setVisibility(View.VISIBLE);
        }else {
            tv_simple_confirm.setVisibility(View.GONE);
            tv_simple_cancel.setVisibility(View.GONE);
        }
        tv_simple_confirm.setOnClickListener(this);
        tv_simple_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_simple_confirm:
                onSimpleConfirmListener.setSimpleConfirm();
                this.dismiss();
                break;
            case R.id.tv_simple_cancel:
                this.dismiss();
                break;
                default:
                    break;

        }
    }


    public interface OnSimpleConfirmListener{
        void setSimpleConfirm();
    }
}
