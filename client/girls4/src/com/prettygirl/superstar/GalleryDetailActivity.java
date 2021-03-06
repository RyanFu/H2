package com.prettygirl.superstar;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.superstar.components.GalleryDetailView;
import com.prettygirl.superstar.util.PreferenceUtils;
import com.prettygirl.superstar.util.StorageUtils;
import com.prettygirl.superstar.util.StorageUtils.ILoadListener;

public class GalleryDetailActivity extends BaseActivity implements ILoadListener, OnClickListener {

    public static final String EXT_IMAGE_INDEX = "ext_image_index";

    public static final String EXT_IMAGE_NAME = "ext_image_name";
    private GalleryDetailView mContextView;

    private View mProgressView;
    private View mContexts;

    private int mId;

    private String mName = null;

    private View mFailedPanelView;

    private ArrayList<String> urls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.e_gallery_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_entry_point_title);
        mProgressView = findViewById(R.id.loadingPanel);
        mContexts = findViewById(R.id.tgallery_p);
        mFailedPanelView = findViewById(R.id.failedPanel);
        mContextView = (GalleryDetailView) findViewById(R.id.tgallery);
        mContextView.bindActivity(this);

        findViewById(R.id.personal_info).setOnClickListener(this);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        Intent intent = getIntent();
        int id = intent.getIntExtra(EXT_IMAGE_INDEX, -1);
        if (id == -1) {
            finish();
            return;
        }
        setTitle(mName = intent.getStringExtra(EXT_IMAGE_NAME));
        ((TextView) findViewById(R.id.custom_entry_title)).setText(mName);
        findViewById(R.id.custom_entry_title).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        StorageUtils.loadGrilPics(this, id, this);
        mId = id;

        int count = PreferenceUtils.getInt(PreferenceUtils.KEY_GIRL_PIC_NUM + mId, 0);
        urls = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            urls.add(String.format("%s/girl/%s/%s.jpg", ServerUtils.getPicServerRoot(this), mId, i));
        }
        mContextView.init(urls, 0);
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
        mContexts.setVisibility(View.GONE);
        mFailedPanelView.setVisibility(View.GONE);
    }

    @Override
    public void loadFinished(Status status, Object obj) {
        int n = -1;
        if (obj == null || !(obj instanceof Integer)) {
            n = -1;
        } else {
            n = ((Integer) obj).intValue();
        }
        if (status == Status.Failed || n == -1) {
            mProgressView.setVisibility(View.GONE);
            mContexts.setVisibility(View.GONE);
            mFailedPanelView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
            mContexts.setVisibility(View.VISIBLE);
            mFailedPanelView.setVisibility(View.GONE);
            urls = new ArrayList<String>();
            for (int i = 0; i < n; i++) {
                urls.add(String.format("%s/girl/%s/%s.jpg", ServerUtils.getPicServerRoot(this), mId, i));
            }
            PreferenceUtils.setInt(PreferenceUtils.KEY_GIRL_PIC_NUM + mId, n);
            mContextView.update(urls);
        }
    }

    private void retry() {
        StorageUtils.loadGrilPics(this, mId, this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            retry();
        } else if (id == R.id.personal_info) {
            Intent intent = new Intent();
            intent.setClass(this, PersonalInfoActivity.class);
            intent.putExtra(EXT_IMAGE_INDEX, mId);
            intent.putExtra(EXT_IMAGE_NAME, mName);
            startActivity(intent);
        } else if (id == R.id.button2) {
            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        }
    }
}
