package com.prettygirl.avgallery.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.avgallery1.R;

public class StorageUtils {

    public static final String CACHE_DIR_SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator
            + "avgallery";

    public static final String CACHE_DIR_ASSETS = "pics/";

    public static final String SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/avimage_%s.jpg";

    private static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/avgallery_%s.jpg";
    
    public final static String getImageCacheDir() {
        return CACHE_DIR_SDCARD;
    }

    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void saveImage(final Context context, String url) {
        if (!isSdCardAvailable()) {
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
                    String path = String.format(SAVE_PATH, System.currentTimeMillis());
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
        // TODO image save
    }

}
