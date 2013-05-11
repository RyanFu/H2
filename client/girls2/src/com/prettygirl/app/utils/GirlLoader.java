package com.prettygirl.app.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pretty.girl.app.R;
import com.prettygirl.app.components.CustomImageViewWithLoading;
import com.prettygirl.app.utils.Http.Callback;

public class GirlLoader {
    public static int DEFAULT_COUNT = 36;

    public final static String KEY_CACHED_SERVER_IMAGE_COUNT = "KEY_SERVER_IMAGE_COUNT";

    private ImageLoader mImageLoader;
    public static boolean IS_WIDTH_LE_480 = false;
    private String mBaseUrl;
    private boolean mIsOffline;
    private static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/prettygirl_%s.jpg";

    public GirlLoader(String baseUrl, boolean isOffLine) {
        mImageLoader = ImageLoader.getInstance();
        this.mBaseUrl = baseUrl;
        this.mIsOffline = isOffLine;
    }

    public void loadImage(int index, int type, ImageView image) {
        String url = ImageResourceUtils.getImageUrlByIndex(mBaseUrl, index, type, mIsOffline);
        mImageLoader.displayImage(url, image);
    }

    public void loadImage(String url, ImageView image) {
        mImageLoader.displayImage(url, image);
    }

    public void loadImage(String url, final CustomImageViewWithLoading image) {
        mImageLoader.loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                image.showLoading();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.OUT_OF_MEMORY) {
                    System.gc();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                image.setImageBitmap(loadedImage);
                image.hideLoading();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void saveImage(final Context context, int index, int type) {
        if (!ImageResourceUtils.isSdCardAvailable()) {
            DialogToastUtils
                    .showDialog(context, context.getText(R.string.e_gallery_detail_image_save_error_title),
                            context.getText(R.string.e_gallery_detail_image_save_error_msg),
                            context.getText(R.string.ok), null);
            return;
        }
        String uri = ImageResourceUtils.getImageUrlByIndex(mBaseUrl, index, type, mIsOffline);
        mImageLoader.loadImage(uri, new ImageLoadingListener() {

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

    public void cacheImage(int index, int type, ImageLoadingListener listener, Context context) {
        mImageLoader.loadImage(ImageResourceUtils.getImageUrlByIndex(mBaseUrl, index, type, mIsOffline), listener);
    }

    public void loadImage(final int index, final int type, final CustomImageViewWithLoading image) {
        String url = ImageResourceUtils.getImageUrlByIndex(mBaseUrl, index, type, mIsOffline);
        mImageLoader.loadImage(url, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                image.showLoading();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.OUT_OF_MEMORY) {
                    System.gc();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                image.setImageBitmap(loadedImage);
                image.hideLoading();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void cancelLoadImage(ImageView image) {
        mImageLoader.cancelDisplayTask(image);
    }

    public void cancelLoadImage(CustomImageViewWithLoading image) {
        mImageLoader.cancelDisplayTask(image.getContextView());
    }

    public static int loadNumberFromUrl(String url) throws Exception {
        String s = MiscUtil.readUrl(ImageResourceUtils.getTotalImageUrlByBaseUrl(url));
        return Integer.parseInt(s.trim());
    }

    public void forceAsyncLoadServerImageCount1(final Context context, final int level, final Runnable onJobDone) {
        ServerUtils.tryGet(context, ImageResourceUtils.getTotalImageSuffix(), new Callback<String>() {

            private static final int SHOW_TOAST = 0x01;
            private static final int OVER = 0x02;

            Handler cHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == SHOW_TOAST) {
                        MiscUtil.toast(context.getString(R.string.album_updated, msg.arg1), Toast.LENGTH_LONG, context);
                    } else {
                        if (onJobDone != null) {
                            try {
                                onJobDone.run();
                            } catch (Throwable e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

            };

            @Override
            public void onSuccess(String url, int status, String result) {
                Integer count = null;
                try {
                    count = Integer.parseInt(result.trim());
                } catch (Exception e) {
                    // Do Nothing
                }
                if (count != null) {
                    int oldCount = getCachedTotalImageCount(context, level);
                    if (count.intValue() != oldCount) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .putInt(KEY_CACHED_SERVER_IMAGE_COUNT + level, count.intValue()).commit();
                        Message msg = cHandler.obtainMessage(SHOW_TOAST);
                        msg.arg1 = count.intValue() - oldCount;
                        msg.sendToTarget();
                    }
                }
                cHandler.sendEmptyMessage(OVER);
            }

            @Override
            public void onFailure(String url, Exception e) {
                cHandler.sendEmptyMessage(OVER);
            }
        });
    }

    public void forceAsyncLoadServerImageCount(final Context context, final int level, final Runnable onJobDone) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return loadNumberFromUrl(mBaseUrl);
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Integer result) {
                if (result != null) {
                    int oldCount = getCachedTotalImageCount(context, level);
                    if (result.intValue() != oldCount) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .putInt(KEY_CACHED_SERVER_IMAGE_COUNT + level, result.intValue()).commit();
                        MiscUtil.toast(context.getString(R.string.album_updated, result.intValue() - oldCount),
                                Toast.LENGTH_LONG, context);
                    }
                }
                if (onJobDone != null) {
                    try {
                        onJobDone.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }.execute();
    }

    public void asyncLoadServerImageCount(final Context context, final int level, final Runnable onJobDone) {
        if (mIsOffline)
            return;
        forceAsyncLoadServerImageCount(context, level, onJobDone);
    }

    public int getCachedTotalImageCount(Context context, int level) {
        if (mIsOffline == true) {
            return DEFAULT_COUNT;
        } else {
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_CACHED_SERVER_IMAGE_COUNT + level,
                    DEFAULT_COUNT);
        }
    }
}