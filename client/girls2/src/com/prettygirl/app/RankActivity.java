package com.prettygirl.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.RankDetailView;
import com.prettygirl.app.model.ThumbUpList;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.model.provider.ThumbUpManager.ThumbUpLoadedListener;

public class RankActivity extends BaseActivity implements ThumbUpLoadedListener, OnClickListener {

    private static final int OFFSET = 0;

    private String mMethod;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_rank_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        Intent intent = getIntent();
        mMethod = intent.getStringExtra(EXT_METHOD);
        retry();
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
    protected void onDestroy() {
        super.onDestroy();
        ThumbUpManager.getInstance().removeCache(mMethod);
    }

    private void retry() {
        findViewById(R.id.rank_main).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.failedPanel).setVisibility(View.GONE);
        findViewById(R.id.tgallery_blank_view).setVisibility(View.GONE);
        ThumbUpManager.getInstance().loadThumbUpTop(findViewById(R.id.loadingPanel), mMethod, OFFSET,
                ThumbUpManager.RANK_MAX_COUNT, this);
    }

    @Override
    public void onLoaded(boolean successed, ThumbUpList cThumbUpList) {
        if (successed) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if (cThumbUpList == null || cThumbUpList.size() < 1) {
                findViewById(R.id.tgallery_blank_view).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rank_main).setVisibility(View.VISIBLE);
                RankDetailView rankDetailView = (RankDetailView) findViewById(R.id.tgallery);
                rankDetailView.bindActivity(this, mMethod);
                rankDetailView.init(cThumbUpList);
            }
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.failedPanel).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            retry();
        } else if (id == R.id.button2) {
            Intent intent = null;
            intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        }
    }

}
