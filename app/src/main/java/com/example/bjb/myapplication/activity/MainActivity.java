package com.example.bjb.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.entity.CommonPageElement;
import com.example.bjb.myapplication.entity.Video;
import com.example.bjb.myapplication.view.VideoPlayer;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;

import org.dom4j.Element;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    private RelativeLayout rootLayout;


    static ArrayList<WeakReference<RelativeLayout>> oldLayouts = new ArrayList<WeakReference<RelativeLayout>>();
    // 需控制的控件列表
    private ArrayList<ViewerCallback> handleViewers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainLayout = new RelativeLayout(this);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }



    private void playItem(){
        // 清除原页面的媒体播放
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(true);
            v.viewerOnDestroy();
        }


        handleViewers.clear();


        //布局
        final RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        VideoPlayer videoPlayer = new VideoPlayer(this, null, layout, 1);


        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp1.setMargins(0,0, 0, 0);

        layout.addView(videoPlayer, lp1);

        handleViewers.add(videoPlayer);


        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        // 替换布局
        RelativeLayout oldLayout = this.rootLayout;
        this.rootLayout = layout;
        mainLayout.addView(layout);
        setContentView(mainLayout);
        mainLayout.removeView(oldLayout);
        oldLayouts.add(new WeakReference<RelativeLayout>(oldLayout));


        for (WeakReference<RelativeLayout> ref : oldLayouts) {
            if (ref.get() != null) {
                Log.e("ds", "--- oldLayout:" + ref.get());
            }
        }
    }


    @Override
    protected void onPause() {
        for (ViewerCallback v : handleViewers) {
            v.viewerOnPause(isFinishing());
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        super.onPause();
    }

    @Override
    protected void onResume() {


        for (ViewerCallback v : handleViewers) {
            v.viewerOnResume();
        }

        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ViewerCallback v : handleViewers) {
            v.viewerOnDestroy();
        }
    }
}
