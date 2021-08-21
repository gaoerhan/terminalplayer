package com.example.bjb.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.utils.SPUtil;
import com.example.bjb.myapplication.utils.UIUtil;
import com.example.bjb.myapplication.view.widget.VerificationDialog;

public class StartActivity extends Activity implements View.OnClickListener {

    public static final String LIANPINGPLAY = "lianping";
    public static final String CONTENTPLAY = "content";


    /**
     * 请输入ip地址
     */
    private EditText mEdtLoginIp;
    /**
     * 请输入用户名
     */
    private EditText mEdtLoginUsername;
    /**
     * 请输入密码
     */
    private EditText mEdtLoginPassword;
    /**
     * 登录
     */
    private TextView mTvLogin;


    private boolean isLiandong ;

    private VerificationDialog verificationDialog;
    /**
     * 请输入终端名称
     */
    private EditText mEdtLoginTerminalname;
    private Spinner mSpMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        initView();


    }


    private void initView() {

        mEdtLoginIp = (EditText) findViewById(R.id.edt_login_ip);
        mEdtLoginUsername = (EditText) findViewById(R.id.edt_login_username);
        mEdtLoginPassword = (EditText) findViewById(R.id.edt_login_password);
        mEdtLoginTerminalname = (EditText) findViewById(R.id.edt_login_terminalname);

        mTvLogin = (TextView) findViewById(R.id.tv_login);
        mTvLogin.setOnClickListener(this);

        mSpMode = (Spinner) findViewById(R.id.sp_mode);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.mode, R.layout.spinner_layout);
        mSpMode.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSpMode.setDropDownVerticalOffset((int) UIUtil.dip2px(25));
        }
        mSpMode.setSelection(("true".equals(SPUtil.getInstance().getString("contentmode",""))|| TextUtils.isEmpty(SPUtil.getInstance().getString("contentmode","")) )? 0 :1);
        mSpMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpMode.setSelection(position);
                if(position == 0){
                    SPUtil.getInstance().saveString("contentmode","true");
                    isLiandong = false;
                }else {
                    SPUtil.getInstance().saveString("contentmode","false");
                    isLiandong = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        if(TextUtils.isEmpty(SPUtil.getInstance().getString("password",""))){

        }else {
            verificationDialog = new VerificationDialog(this);
            verificationDialog.show();

            verificationDialog.setOnVerificationCallBackLinstener(new VerificationDialog.onVerificationCallBackLinstener() {
                @Override
                public void onVerificationCallBack() {
                    login();
                    verificationDialog.dismiss();
                }
            });
        }


        mEdtLoginTerminalname.setText(SPUtil.getInstance().getString("terminalname", ""));
        mEdtLoginUsername.setText(SPUtil.getInstance().getString("username", ""));
        mEdtLoginPassword.setText(SPUtil.getInstance().getString("password", ""));
        mEdtLoginIp.setText(SPUtil.getInstance().getString("ip", ""));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;

            case R.id.tv_login:

                login();

                break;

        }
    }

    private void login(){
        String terminalName = mEdtLoginTerminalname.getText().toString().trim();
        String username = mEdtLoginUsername.getText().toString().trim();
        String password = mEdtLoginPassword.getText().toString().trim();
        String ip = mEdtLoginIp.getText().toString().trim();

        if (TextUtils.isEmpty(terminalName)) {
            Toast.makeText(StartActivity.this, "请输入终端名称", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(StartActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(StartActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(ip)) {
            Toast.makeText(StartActivity.this, "请输入IP地址", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent;
        if (isLiandong) {
            intent = new Intent(StartActivity.this, NettyActivity.class);
        } else {
            intent = new Intent(StartActivity.this, NettyActivity.class);
        }


        intent.putExtra("terminalname", terminalName);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("ip", ip);

        startActivity(intent);
        finish();
    }
}
