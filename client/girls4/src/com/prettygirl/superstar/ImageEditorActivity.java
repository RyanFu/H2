package com.prettygirl.superstar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.components.ZoomableImageView;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.superstar.util.StorageUtils;

public class ImageEditorActivity extends BaseActivity implements OnClickListener {

    public static final String EXT_INT_INDEX = "GalleryIndex";
    public static final String EXT_STRING_BASE_URL = "BaseUrl";

    private ZoomableImageView mMainImageView;
    private String mUrl;

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
        String baseUrl = intent.getStringExtra(EXT_STRING_BASE_URL);
        ImageLoader.getInstance().displayImage(baseUrl, mMainImageView);
        mUrl = baseUrl;
    }

    public void saveImage(final Context context, String url) {
        if (!StorageUtils.isSdCardAvailable()) {
            DialogToastUtils
                    .showDialog(context, context.getText(R.string.e_gallery_detail_image_save_error_title),
                            context.getText(R.string.e_gallery_detail_image_save_error_msg),
                            context.getText(R.string.ok), null);
            return;
        }
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // NG
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // NG
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    String path = String.format(StorageUtils.SAVE_IMAGE_PATH, System.currentTimeMillis());
                    loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(path));
                    DialogToastUtils.showMessage(context,
                            context.getString(R.string.e_gallery_detail_image_save_success_msg, path));
                } catch (FileNotFoundException e) {
                    DialogToastUtils.showMessage(context,
                            context.getString(R.string.e_gallery_detail_image_save_fail_msg));
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                // NG
            }
        });
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
            StorageUtils.saveImage(this, mUrl);
        }
    }
}
