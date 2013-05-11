package com.prettygirl.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.ZoomableImageView;
import com.prettygirl.app.model.Favorite;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.utils.GirlLoader;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class ImageEditorActivity extends BaseActivity implements OnClickListener {

    public static final String EXT_INT_INDEX = "GalleryIndex";
    public static final String EXT_STRING_BASE_URL = "BaseUrl";

    private int mLevel;
    private ZoomableImageView mMainImageView;
    private GirlLoader mGirlLoader;
    private int cIndex, mType;

    private String mMethod;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_gallery_detail_image_editor);
        mMainImageView = (ZoomableImageView) findViewById(R.id.zoomable_image_view);

        findViewById(R.id.picture_widget_save).setOnClickListener(this);
        findViewById(R.id.picture_widget_reset).setOnClickListener(this);
        findViewById(R.id.picture_rotate_left).setOnClickListener(this);
        findViewById(R.id.picture_rotate_right).setOnClickListener(this);
        findViewById(R.id.picture_zoom_in).setOnClickListener(this);
        findViewById(R.id.picture_zoom_out).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);

        Intent intent = getIntent();
        cIndex = intent.getIntExtra(EXT_INT_INDEX, 0);
        mLevel = intent.getIntExtra(EXT_LEVEL_KEY, 0);
        mType = intent.getIntExtra(EXT_TYPE, EXT_VALUE_OFFLINE);
        mMethod = intent.getStringExtra(EXT_METHOD);
        String baseUrl = intent.getStringExtra(EXT_STRING_BASE_URL);
        if (TextUtils.isEmpty(baseUrl) == true) {
            baseUrl = ImageResourceUtils.getServerUrl(this, mLevel);
        }
        mGirlLoader = new GirlLoader(baseUrl, mType == BaseActivity.EXT_VALUE_OFFLINE);
        if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
            Favorite cFavorite = FavoriteManager.getInstance().getFavoriteList().get(cIndex);
            mGirlLoader.loadImage(cFavorite.path, mMainImageView);
        } else {
            mGirlLoader.loadImage(cIndex, ImageResourceUtils.TYPE_ORIGINAL, mMainImageView);
        }
        if (mType == BaseActivity.EXT_VALUE_OFFLINE) {
            findViewById(R.id.mAdView).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_widget_reset) {
            mMainImageView.reset();
        } else if (id == R.id.picture_rotate_left) {
            mMainImageView.rotateBy(-5f);
        } else if (id == R.id.picture_rotate_right) {
            mMainImageView.rotateBy(5f);
        } else if (id == R.id.picture_zoom_in) {
            mMainImageView.postZoomBy(0.8f);
        } else if (id == R.id.picture_zoom_out) {
            mMainImageView.postZoomBy(1.2f);
        } else if (id == R.id.button1) {
            finish();
        } else if (id == R.id.button2 || id == R.id.picture_widget_save) {
            MobclickAgent.onEvent(this, UMengKey.IMAGE_EDITOR_ACTIVITY_DOWNLOAD);
            mGirlLoader.saveImage(this, cIndex, ImageResourceUtils.TYPE_ORIGINAL);
            ThumbUpManager.getInstance().updateThumbUpCountWithoutCallback(v, mLevel,
                    mMethod == null ? "" + mLevel : mMethod, cIndex, ThumbUpManager.THUMB_UP, null);
        }
    }

}
