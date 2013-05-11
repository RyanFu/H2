package com.prettygirl.avgallery;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.avgallery.components.GalleryDetailView;
import com.prettygirl.avgallery1.R;

public class GalleryDetailActivity extends BaseActivity {

    public static final String EXT_IMAGE_INDEX = "ext_image_index";

    public static final String EXT_IMAGE_LIST = "ext_image_list";

    private GalleryDetailView mContextView;

    private ArrayList<String> urls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_gallery_detail);
        Intent intent = getIntent();
        mContextView = (GalleryDetailView) findViewById(R.id.tgallery);
        mContextView.bindActivity(this);
        int index = intent.getIntExtra(EXT_IMAGE_INDEX, 0);
        urls = intent.getStringArrayListExtra(EXT_IMAGE_LIST);
        mContextView.init(urls, index);
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

}
