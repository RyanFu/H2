package com.prettygirl.avgallery.components;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.prettygirl.app.utils.Utils;
import com.prettygirl.avgallery.GalleryDetailActivity;
import com.prettygirl.avgallery1.R;

public class ScrollerView extends HorizontalScrollView implements Runnable, ImageLoadingListener {

    private LinearLayout container;
    private int scrollDirection = LEFT_TO_RIGHT;

    private static int LEFT_TO_RIGHT = 0;
    private static int RIGHT_TO_LEFT = 1;

    private boolean mAutoPlay = false;

    private Activity mActivity;

    @SuppressWarnings("deprecation")
    public ScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        container = new LinearLayout(context);
        addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        setHorizontalScrollBarEnabled(false);
    }

    public void bindActivity(Activity cActivity) {
        mActivity = cActivity;
    }

    public void setImages(final ArrayList<String> urls, boolean autoPlay) {
        container.removeAllViews();
        int margin = (int) Utils.dip2px(getContext(), 3);
        String url = null;
        for (int index = 0, size = urls.size(); index < size && index < 10; index++) {
            url = urls.get(index);
            ImageView v = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            v.setScaleType(ScaleType.CENTER_INSIDE);
            v.setAdjustViewBounds(true);
            v.setTag("" + index);
            v.setOnClickListener(new MyOnclickListener(urls));
            container.addView(v, lp);
            ImageLoader.getInstance().displayImage(url, v, this);
        }
        if (urls.size() >= 10) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.more_txt, container);
            v.findViewById(R.id.moretxt).setOnClickListener(new MyOnclickListener(urls));
            v.findViewById(R.id.moretxt).setTag("9");
        }
        setAutoPlay(autoPlay);
    }

    public void setAutoPlay(boolean auto) {
        mAutoPlay = auto;
        if (auto) {
            postDelayed(this, 1000);
        }
    }

    @Override
    public void run() {
        if (scrollDirection == LEFT_TO_RIGHT) {
            if (getWidth() + getScrollX() >= container.getWidth()) {
                scrollDirection = RIGHT_TO_LEFT;
            } else {
                scrollBy(1, 0);
            }
        } else {
            if (getScrollX() <= 0) {
                scrollDirection = LEFT_TO_RIGHT;
            } else {
                scrollBy(-1, 0);
            }
        }
        if (mAutoPlay) {
            postDelayed(this, 15);
        }
    }

    class MyOnclickListener implements OnClickListener {
        private ArrayList<String> urls;

        MyOnclickListener(ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj == null || !(obj instanceof String)) {
                return;
            }
            int index = 0;
            try {
                index = Integer.valueOf((String) obj);
            } catch (Exception e) {
            }
            Intent intent = new Intent();
            intent.setClass(mActivity, GalleryDetailActivity.class);
            intent.putExtra(GalleryDetailActivity.EXT_IMAGE_INDEX, index);
            intent.putExtra(GalleryDetailActivity.EXT_IMAGE_LIST, urls);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        // NG
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        // NG
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        // NG
    }
}
