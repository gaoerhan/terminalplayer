package com.example.bjb.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.bjb.myapplication.utils.LogUtils;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyImagview extends ImageView implements ViewerCallback {

    private int path;

    private ImageLoader imageLoader;
    private DisplayImageOptions options; // 显示图片的设置
    private Context context;
    public MyImagview(Context context, int path) {
        super(context);
        this.path = path;

        this.context = context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
//        imageLoader.displayImage("file://"+ path, this, options);
        imageLoader.displayImage("drawable://" + path,this,options);
    }


    @Override
    public void viewerOnPause(boolean isFinishing) {

    }

    @Override
    public void viewerOnResume() {

    }

    @Override
    public void viewerOnDestroy() {
        try {
            if (imageLoader != null) {
                imageLoader.clearDiscCache();
                imageLoader.clearMemoryCache();
                imageLoader.destroy();
                imageLoader = null;
            }

            Drawable drawable = this.getDrawable();
            if (drawable != null) {
                BitmapDrawable bm = (BitmapDrawable) drawable;
                if (bm != null && bm.getBitmap() != null && !bm.getBitmap().isRecycled()) {
                    bm.getBitmap().recycle();
                }
                drawable = null;
            }
            this.setImageBitmap(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

