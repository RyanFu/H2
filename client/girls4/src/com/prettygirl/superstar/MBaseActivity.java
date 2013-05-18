package com.prettygirl.superstar;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.prettygirl.app.base.BaseActivity;
import com.prettygirl.app.dialog.AlertDialog;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.superstar.util.PreferenceUtils;
import com.prettygirl.superstar.util.UMengKey;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;

public class MBaseActivity extends BaseActivity {

    private boolean isShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener() {
            @Override
            public void onDataReceived(JSONObject data) {
                parseOnlineConfig(data);
            }
        });
    }

    private Handler mHandler = new Handler();

    protected void parseOnlineConfig(JSONObject data) {
        if (data != null) {
            PreferenceUtils.setBoolean(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA_CHANGED, true);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    showOnlineAdDialog();
                }

            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isShowing = false;
    }

    private void showOnlineAdDialog() {
        if (isShowing) {
            return;
        }
        String value = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_SHOWABLE);
        if (TextUtils.isEmpty(value) || !Boolean.valueOf(value)) {
            return;
        } else {
            final OnCancelListener cancelListener = new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    isShowing = false;
                }
            };
            final OnDismissListener cDismissListener = new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    isShowing = false;
                }
            };
            final String title = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_TITLE);
            final String msg = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_CONTENT);
            value = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_HAS_IMAGE);
            final String okUrl = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_BUTTON_YES);
            String imgUrl = null;
            if (!TextUtils.isEmpty(value) && Boolean.valueOf(value)) {
                imgUrl = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_IMAGE_URL);
                ImageLoader.getInstance().loadImage(imgUrl, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        AlertDialog cDialog = DialogToastUtils.showAdUpdateDialogImage(MBaseActivity.this, title, msg,
                                loadedImage, getString(R.string.ok), okUrl,
                                getString(R.string.e_gallery_detail_image_cancel));
                        cDialog.setOnCancelListener(cancelListener);
                        cDialog.setOnDismissListener(cDismissListener);
                        isShowing = true;
                        PreferenceUtils.setLong(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA,
                                System.currentTimeMillis());
                        PreferenceUtils.setBoolean(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA_CHANGED, false);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
            } else {
                AlertDialog cDialog = DialogToastUtils.showAdUpdateDialogImage(MBaseActivity.this, title, msg, null,
                        getString(R.string.ok), okUrl, getString(R.string.e_gallery_detail_image_cancel));
                cDialog.setOnCancelListener(cancelListener);
                cDialog.setOnDismissListener(cDismissListener);
                isShowing = true;
                PreferenceUtils.setLong(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA, System.currentTimeMillis());
                PreferenceUtils.setBoolean(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA_CHANGED, false);
            }
        }
    }

    protected void tryShowOnlineConfigAd() {
        long lastTime = PreferenceUtils.getLong(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA, -1);
        if (lastTime == -1) {
            return;
        }
        String value = MobclickAgent.getConfigParams(this, UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_SHOW_INTERVAL);
        if (TextUtils.isEmpty(value) || "-1".equals(value)) {
            return;
        }
        int interval = 0;
        try {
            interval = Integer.valueOf(value);
        } catch (Exception e) {
            // NG;
        }
        boolean hasChanged = PreferenceUtils.getBoolean(PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA_CHANGED, false);
        if (interval == 0 && !hasChanged) {
            // do nothing
        } else if (System.currentTimeMillis() - lastTime > DateUtils.DAY_IN_MILLIS * interval) {
            showOnlineAdDialog();
        }
    }

}
