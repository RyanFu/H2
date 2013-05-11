package com.prettygirl.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.GalleryDetailView;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.model.provider.FavoriteManager.FavoriteChangedListener;
import com.prettygirl.app.utils.ImageResourceUtils;

public class GalleryDetailActivity extends BaseActivity implements FavoriteChangedListener {

    public static final String EXT_INT_INDEX = "GalleryIndex";
    public static final String EXT_STRING_BASE_URL = "BaseUrl";

    private GalleryDetailView mContextView;

    private int mLevel;
    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_gallery_detail);
        Intent intent = getIntent();
        int cIndex = intent.getIntExtra(EXT_INT_INDEX, 0);
        mLevel = intent.getIntExtra(EXT_LEVEL_KEY, 0);
        mType = intent.getIntExtra(EXT_TYPE, EXT_VALUE_OFFLINE);
        mContextView = (GalleryDetailView) findViewById(R.id.tgallery);
        String baseUrl = intent.getStringExtra(EXT_STRING_BASE_URL);
        if (TextUtils.isEmpty(baseUrl) == true) {
            baseUrl = ImageResourceUtils.getServerUrl(this, mLevel);
        }
        mContextView.bindActivity(this);
        mContextView.init(cIndex, baseUrl, mLevel, mType);
        FavoriteManager.getInstance().registerFavoriteChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContextView.cancelAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        ThumbUpManager.getInstance().removeCache("" + mLevel);
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
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(EXT_INT_INDEX, mContextView.getSelectedItem());
        intent.putExtra(EXT_TYPE, mType);
        this.setResult(GalleryActivity.GO_DETAIL, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavoriteManager.getInstance().unregisterFavoriteChangedListener(this);
        ThumbUpManager.getInstance().removeCache("" + mLevel);
    }

    @Override
    public void onChanged() {
        this.mContextView.onFavoriteChanged();
    }

}
