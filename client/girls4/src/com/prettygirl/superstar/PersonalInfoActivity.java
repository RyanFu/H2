package com.prettygirl.superstar;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.utils.ServerUtils;

public class PersonalInfoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superstar_gallery_detail_activity);
        WebView view = (WebView) findViewById(R.id.main_container_web_view);
        Intent intent = getIntent();
        int id = intent.getIntExtra(GalleryDetailActivity.EXT_IMAGE_INDEX, -1);
        if (id == -1) {
            finish();
            return;
        }
        setTitle(intent.getStringExtra(GalleryDetailActivity.EXT_IMAGE_NAME));
        view.loadUrl(String.format("%s/girl/%s/i.html", ServerUtils.getPicServerRoot(this), id));
    }

}
