package com.prettygirl.app.components;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pretty.girl.app.R;
import com.prettygirl.app.Application;
import com.prettygirl.app.utils.GirlLoader;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.MiscUtil;

public class MAdView extends RelativeLayout implements AdListener, OnClickListener {

    private AdView mAdView;
    private ScrollerView mScrollerView;

    public MAdView(Context context) {
        super(context);
        init();
    }

    public MAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MAdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (Application.DEFAULT_IS_OFFLINE == true) {
            View.inflate(getContext(), R.layout.e_adslider, this);
            mScrollerView = (ScrollerView) findViewById(R.id.scrollerView);
        } else {
            View.inflate(getContext(), R.layout.e_admob, this);
            mAdView = (AdView) findViewById(R.id.ad);
            mAdView.setAdListener(this);
        }
    }

    public void setScrollAdVisibility(int visibility) {
        if (mScrollerView != null) {
            mScrollerView.setVisibility(visibility);
        }
    }

    private void showScrollerImages(ArrayList<String> urls) {
        if (mScrollerView == null) {
            return;
        }
        mScrollerView.setVisibility(View.VISIBLE);
        mScrollerView.setImages(urls);
        setVisibility(View.VISIBLE);
        requestLayout();
    }

    @Override
    public void onDismissScreen(Ad ad) {
    }

    @Override
    public void onFailedToReceiveAd(Ad ad, ErrorCode eCode) {
    }

    @Override
    public void onLeaveApplication(Ad ad) {
    }

    @Override
    public void onPresentScreen(Ad ad) {
    }

    @Override
    public void onReceiveAd(Ad ad) {

    }

    @Override
    public void onClick(View v) {
        if (Application.DEFAULT_IS_OFFLINE == true) {
            MiscUtil.startGooglePlay(v.getContext(), null);
        } else {
            MiscUtil.startGooglePlayByAuthor(v.getContext());
        }
    }

    public final int ScrollImageCount = 10;
    private int mLoadedImageCount = 0;

    public void startLoadingScrollingImage() {
        final ArrayList<String> imageUrls = new ArrayList<String>(ScrollImageCount);
        final String baseUrl = ImageResourceUtils.getServerUrl(getContext(), 0);
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return GirlLoader.loadNumberFromUrl(baseUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }

            protected void onPostExecute(Integer result) {
                ImageLoader loader = ImageLoader.getInstance();
                int total = result.intValue();
                if (total <= 0)
                    return;
                for (int i = 0; i < ScrollImageCount; i++) {
                    String imageUrl = ImageResourceUtils.getImageUrlByIndex(baseUrl, total - i - 1,
                            ImageResourceUtils.TYPE_SNAPSHOT, false);
                    loader.loadImage(imageUrl, new LoadingListener(imageUrl, imageUrls));
                }
            };
        }.execute();
    }

    class LoadingListener implements ImageLoadingListener {
        private String url;
        private ArrayList<String> urls;

        LoadingListener(String imageUrl, ArrayList<String> urls) {
            this.url = imageUrl;
            this.urls = urls;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            Log.d("MAdView", "Failed loading " + url);
            synchronized (urls) {
                mLoadedImageCount++;
                if (mLoadedImageCount == ScrollImageCount) {
                    showScrollerImages(urls);
                }
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Log.d("MAdView", "Loaded " + url);
            synchronized (urls) {
                mLoadedImageCount++;
                urls.add(url);
                if (mLoadedImageCount == ScrollImageCount) {
                    showScrollerImages(urls);
                }
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }
    }
}
