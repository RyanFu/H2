package com.prettygirl.app;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pretty.girl.app.R;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.PreferenceUtils;
import com.prettygirl.app.utils.UMengKey;
import com.prettygirl.app.utils.UserInfoManager;
import com.prettygirl.app.utils.UserInfoManager.LevelListener;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity implements LevelListener {

	public static final String EXT_LEVEL_KEY = "CURRENT_LEVEL";

	public static final String EXT_TYPE = "TYPE";

    public static final String EXT_METHOD = "METHOD";

	public static final int EXT_VALUE_OFFLINE = 1;

	public static final int EXT_VALUE_ONLINE = 2;

	public static final int EXT_VALUE_FAVORITE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.setDebugMode(false);
		UserInfoManager.getInstance().registerLevelListener(this);
	}

	private Handler mHandler = new Handler();

	protected void parseOnlineConfig(JSONObject data) {
		if (data != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					showOnlineAdDialog();
				}

			});
		}
	}

	private void showOnlineAdDialog() {
		String value = MobclickAgent.getConfigParams(this,
				UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_SHOWABLE);
		if (TextUtils.isEmpty(value) || !Boolean.valueOf(value)) {
			return;
		} else {
			final String title = MobclickAgent.getConfigParams(this,
					UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_TITLE);
			final String msg = MobclickAgent.getConfigParams(this,
					UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_CONTENT);
			value = MobclickAgent.getConfigParams(this,
					UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_HAS_IMAGE);
			final String okUrl = MobclickAgent.getConfigParams(this,
					UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_BUTTON_YES);
			String imgUrl = null;
			if (!TextUtils.isEmpty(value) && Boolean.valueOf(value)) {
				imgUrl = MobclickAgent.getConfigParams(this,
						UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_IMAGE_URL);
				ImageLoader.getInstance().loadImage(imgUrl,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								DialogToastUtils.showAdUpdateDialogImage(
										BaseActivity.this, title, msg,
										loadedImage, getString(R.string.ok),
										okUrl, getString(R.string.e_gallery_detail_image_cancel));
								PreferenceUtils
										.setLong(
												PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA,
												System.currentTimeMillis());
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
							}
						});
			} else {
				DialogToastUtils.showAdUpdateDialogImage(BaseActivity.this,
						title, msg, null, getString(R.string.ok), okUrl,
						getString(R.string.e_gallery_detail_image_cancel));
				PreferenceUtils.setLong(
						PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA,
						System.currentTimeMillis());
			}
		}
	}

	protected void tryShowOnlineConfigAd() {
		long lastTime = PreferenceUtils.getLong(
				PreferenceUtils.KEY_LAST_SHOW_AD_DIALOG_DATA, -1);
		if (lastTime == -1) {
			return;
		}
		String value = MobclickAgent.getConfigParams(this,
				UMengKey.ONLINE_CONFIG_KEY_AD_DIALOG_SHOW_INTERVAL);
		if (TextUtils.isEmpty(value)) {
			return;
		}
		value = MobclickAgent.getConfigParams(this,
				UMengKey.OFFLINE_CONFIG_KEY_BROWSER_ONLINE_PAGE_ENABLE);
		Application.OFFLINE_BROWSER_ONLINE_ENABLE = TextUtils
				.isEmpty(value) ? Application.OFFLINE_BROWSER_ONLINE_ENABLE
				: Boolean.valueOf(value);
		value = MobclickAgent.getConfigParams(this,
				UMengKey.OFFLINE_CONFIG_KEY_MAX_BROWSER_PAGE_COUNT);
		Application.OFFLINE_BROWSER_MAX_PAGE_COUNT = TextUtils
				.isEmpty(value) ? Application.OFFLINE_BROWSER_MAX_PAGE_COUNT
				: Integer.valueOf(value);
		if (System.currentTimeMillis() - lastTime > DateUtils.DAY_IN_MILLIS
				* Integer.valueOf(value)) {
			showOnlineAdDialog();
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UserInfoManager.getInstance().removeLevelListener(this);
	}

	@Override
	public void onLevelChange(int level, int previousLevel) {
		// System.out.println("level changed " + level);
	}

	@Override
	public void onPointChange(int point, int previousPoint) {
		// System.out.println("point changed " + point);
	}
}
