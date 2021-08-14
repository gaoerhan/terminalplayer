package com.example.bjb.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.bjb.myapplication.R;
import com.example.bjb.myapplication.entity.NewPicture;
import com.example.bjb.myapplication.utils.APPUtils;
import com.example.bjb.myapplication.view.callbacks.ViewerCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Field;
import java.util.List;


/**
 * Created by Administrator on 2018/4/2.
 */

public class NewBannerView extends RelativeLayout implements ViewerCallback {
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private ImageView[] mIndicator;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<String> mPics ;

    private DisplayImageOptions options; // 显示图片的设置
    private ImageLoader imageLoader;
    private String jumpLink;
    private ViewGroup mGroup;

    private OnBannerItemClickListener mOnBannerItemClickListener;
    private Runnable mRunnable ;
    private int mItemCount;
    private int mInterval;

    private volatile boolean isCircle = true;
    private FixedSpeedScroller mScroller;
    public interface OnBannerItemClickListener {
        void onClick(int position);
    }

    public NewBannerView(Context context) {
        this(context, null);
    }

    public NewBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public NewBannerView(Context context, NewPicture picture, ViewGroup group){
        super(context);
        mContext =context;
        init();
        initView(picture.getRawPathList(), picture.getSwitchTime());
        final String packageName = APPUtils.getPackageNameByAppName(mContext,jumpLink);


    }



    private void init() {
        View.inflate(mContext, R.layout.view_bannerview, this);
        // 取到布局中的控件
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_points);
    }



    /**
     * banner item的点击监听
     *
     * @param onBannerItemClickListener
     */
    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
    }

    public int getCurrentItem(){
        return mViewPager.getCurrentItem();
    }

    public void setCurrentItem(int num){
        mViewPager.setCurrentItem(num);
    }

    private void initView(List<String> pics, int interval) {
        mInterval = interval;
        mRunnable = new Runnable() {
            @Override
            public void run() {

                if(mViewPager.getCurrentItem() >= 600*mItemCount-1){
                    mViewPager.setCurrentItem(20*mItemCount);
                }else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
//                Log.e("player","当前条目"+mViewPager.getCurrentItem());
                mHandler.postDelayed(mRunnable, mInterval*975);
            }
        };
        mPics = pics;
        mItemCount = pics.size();
        // 给viewpager设置adapter
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(mPics, mContext);
        mViewPager.setAdapter(bannerPagerAdapter);

        //------vvv控制切换时间
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(mViewPager.getContext(),new AccelerateInterpolator());
            mField.set(mViewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //----AAA
        // 初始化底部点指示器
        initIndicator(mPics, mContext);
        mViewPager.setCurrentItem(20 * mItemCount);

        mScroller.setmDuration(1*1000);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        // 给viewpager设置滑动监听
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switchIndicator(position % mItemCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelRecycle();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startRecycle();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initIndicator(List<String> list, Context context) {
        mIndicator = new ImageView[mItemCount];
        for (int i = 0; i < mIndicator.length; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(6, 0, 6, 0);
            ImageView imageView = new ImageView(context);
            mIndicator[i] = imageView;
            if (i == 0) {
                mIndicator[i].setBackgroundResource(R.drawable.ptt_banner_dian_focus);
            } else {
                mIndicator[i].setBackgroundResource(R.drawable.ptt_banner_dian_white);
            }
            mLinearLayout.addView(imageView, params);
        }
        if (mItemCount == 1|| mItemCount > 6) {
            mLinearLayout.setVisibility(View.GONE);
        } else {
//            mLinearLayout.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    private void switchIndicator(int selectItems) {
        for (int i = 0; i < mIndicator.length; i++) {
            if (i == selectItems) {
                mIndicator[i].setBackgroundResource(R.drawable.ptt_banner_dian_focus);
            } else {
                mIndicator[i].setBackgroundResource(R.drawable.ptt_banner_dian_white);
            }
        }
    }

    private void startRecycle() {
        mHandler.postDelayed(mRunnable, mInterval*975);
    }

    private void cancelRecycle() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startRecycle();
        } else {
            cancelRecycle();
        }
    }


    private class BannerPagerAdapter extends PagerAdapter {
        private List<String> imagesUrl;
        private Context context;

        public BannerPagerAdapter(List<String> imagesUrl, Context context) {
            this.imagesUrl = imagesUrl;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mItemCount == 1 ? 1 : mItemCount*600;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View ret = null;
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //更换网络图片
            if (imageLoader == null) {
                imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            }
            imageLoader.displayImage("file://" + mPics.get(position%mItemCount),imageView,options);
            Log.e("HJ","图片路径:" + mPics.get(position%mItemCount));
            // 联网取图片，根据自己的情况修改
            ret = imageView;
            container.addView(ret);

            return ret;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }



    @Override
    public void viewerOnPause(boolean isFinishing) {

    }

    @Override
    public void viewerOnResume() {

    }

    @Override
    public void viewerOnDestroy() {

        // 不释放会导致内存泄漏 重复创建移除控件的时候会表现出来
        try {
            if (imageLoader != null) {
                imageLoader.clearDiscCache();
                imageLoader.clearMemoryCache();
                imageLoader.destroy();
                imageLoader = null;
            }
            removeCallbacks(mRunnable);
            if (mHandler != null) mHandler.removeCallbacks(mRunnable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class FixedSpeedScroller extends Scroller {
        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int time) {
            mDuration = time;
        }

        public int getmDuration() {
            return mDuration;
        }

    }

    public void setCircle(){
        isCircle = true;
    }

    public void stopCircle(){
        isCircle = false;
    }
}
