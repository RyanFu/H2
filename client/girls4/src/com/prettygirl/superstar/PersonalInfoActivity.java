package com.prettygirl.superstar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.utils.ServerUtils;

public class PersonalInfoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.superstar_gallery_detail_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_entry_point_title);
        WebView view = (WebView) findViewById(R.id.main_container_web_view);
        findViewById(R.id.personal_info).setVisibility(View.GONE);
        findViewById(R.id.custom_entry_title).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
        Intent intent = getIntent();
        int id = intent.getIntExtra(GalleryDetailActivity.EXT_IMAGE_INDEX, -1);
        if (id == -1) {
            finish();
            return;
        }
        setTitle(intent.getStringExtra(GalleryDetailActivity.EXT_IMAGE_NAME));
        ((TextView) findViewById(R.id.custom_entry_title)).setText(intent
                .getStringExtra(GalleryDetailActivity.EXT_IMAGE_NAME));
        view.loadUrl(String.format("%s/girl/%s/i.html", ServerUtils.getPicServerRoot(this), id));
    }

}
