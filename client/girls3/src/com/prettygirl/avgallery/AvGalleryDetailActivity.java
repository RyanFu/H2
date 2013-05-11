package com.prettygirl.avgallery;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.prettygirl.app.base.AdTitleBaseActivity;
import com.prettygirl.app.utils.AdUtils;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.ServerUtils;
import com.prettygirl.avgallery.components.ScrollerView;
import com.prettygirl.avgallery.model.AVGirl;
import com.prettygirl.avgallery.util.UMengKey;
import com.prettygirl.avgallery1.R;
import com.umeng.analytics.MobclickAgent;

public class AvGalleryDetailActivity extends AdTitleBaseActivity implements OnClickListener {

    private AVGirl mGirl;

    private DecimalFormat decimalFormat = new DecimalFormat("000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.av_gallery_detail_activity);
        ScrollerView cScrollerView = (ScrollerView) findViewById(R.id.scrollerView);
        cScrollerView.bindActivity(this);
        ArrayList<String> urls = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            urls.add(ServerUtils.getPicServerRoot(this) + "/images/000/000/" + decimalFormat.format(i + 1) + ".jpg");
        }
        cScrollerView.setImages(urls, false);
        WebView view = (WebView) findViewById(R.id.main_container_web_view);
        Intent intent = getIntent();
        Object o = intent.getParcelableExtra(AvGalleryMainActivity.EXT_KEY_GIRL);
        AVGirl girl = null;
        if (o == null || !(o instanceof AVGirl)) {
            DialogToastUtils.showMessage(this, "error opt");
            finish();
        } else {
            girl = (AVGirl) o;
        }
        mGirl = girl;
        view.loadUrl("file:///android_asset/langs/" + AvApplication.getCurrentLang() + "/" + mGirl.path);
        setGoBackIconVisibility(View.VISIBLE);
        setTitle(girl.name);
        setAdViewClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.entry_point_ad_icon) {
            MobclickAgent.onEvent(this, UMengKey.ENTRY_POINT_ACTIVITY_AD);
            AdUtils.handleMoreAppEvent(this);
        }
    }

}
