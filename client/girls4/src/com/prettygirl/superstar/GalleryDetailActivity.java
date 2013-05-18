package com.prettygirl.superstar;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.superstar.components.GalleryDetailView;
import com.prettygirl.superstar.util.StorageUtils;
import com.prettygirl.superstar.util.StorageUtils.ILoadListener;

public class GalleryDetailActivity extends BaseActivity implements ILoadListener {

    public static final String EXT_IMAGE_INDEX = "ext_image_index";

    private GalleryDetailView mContextView;

    private View mProgressView;

    private int mId;

    private View mFailedPanelView;

    private ArrayList<String> urls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_gallery_detail);
        Intent intent = getIntent();
        mProgressView = findViewById(R.id.loadingPanel);
        mFailedPanelView = findViewById(R.id.failedPanel);
        int id = intent.getIntExtra(EXT_IMAGE_INDEX, 0);
        StorageUtils.loadGrilPics(this, id, this);
        mId = id;
        mContextView = (GalleryDetailView) findViewById(R.id.tgallery);
        mContextView.bindActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContextView.cancelAutoPlay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void startLoad() {
        mProgressView.setVisibility(View.VISIBLE);
        mContextView.setVisibility(View.GONE);
    }

    @Override
    public void loadFinished(Status status, Object obj) {
        int n = -1;
        if (obj == null || !(obj instanceof Integer)) {
            n = -1;
        } else {
            n = -1;
        }
        if (status == Status.Failed || n == -1) {
            mProgressView.setVisibility(View.GONE);
            mContextView.setVisibility(View.GONE);
            mFailedPanelView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
            mContextView.setVisibility(View.VISIBLE);
            mFailedPanelView.setVisibility(View.GONE);
            urls = new ArrayList<String>();
            for (int i = 0; i < n; i++) {
                urls.add(String.format("%s/girl/%s/%s.jpg", ServerUtils.getPicServerRoot(this), mId, i));
            }
            mContextView.init(urls, 0);
        }
    }
}
