package com.prettygirl.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pretty.lib.R;
import com.prettygirl.app.components.ImageViewWithLoading;
import com.prettygirl.app.dialog.AlertDialog;

public class DialogToastUtils {

	private static final String TAG = "DialogToastUtils";

	public static Toast toast;

	public static AlertDialog showAdUpdateDialogImage(final Context context,
			CharSequence title, CharSequence msg, Bitmap img, CharSequence ok,
			final String okUrl, CharSequence cancel) {
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(okUrl));
				context.startActivity(i);
			}

		};
		if (img == null) {
			return showDialog(context, title, msg, ok, onClickListener, cancel,
					null, null, null, null, false);
		} else {
			View cContentView = View.inflate(context,
					R.layout.ad_dialog_with_img, null);
			((TextView) cContentView.findViewById(R.id.textView1)).setText(msg);
			final ImageViewWithLoading cImageViewWithLoading = (ImageViewWithLoading) cContentView
					.findViewById(R.id.imageView1);
			cImageViewWithLoading.setImageBitmap(img);
			return showDialog(context, title, cContentView, ok,
					onClickListener, cancel, null);
		}
	}

	public static AlertDialog showAdUpdateDialog(final Context context,
			CharSequence title, CharSequence msg, String imgUrl,
			CharSequence ok, final String okUrl, CharSequence cancel) {
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(okUrl));
				context.startActivity(i);
			}

		};
		if (imgUrl == null) {
			return showDialog(context, title, msg, ok, onClickListener, cancel,
					null, null, null, null, false);
		} else {
			View cContentView = View.inflate(context,
					R.layout.ad_dialog_with_img, null);
			((TextView) cContentView.findViewById(R.id.textView1)).setText(msg);
			final ImageViewWithLoading cImageViewWithLoading = (ImageViewWithLoading) cContentView
					.findViewById(R.id.imageView1);
			ImageLoader.getInstance().loadImage(imgUrl,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							cImageViewWithLoading.showLoading();
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							cImageViewWithLoading.showFailed();
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							cImageViewWithLoading.setImageBitmap(loadedImage);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							cImageViewWithLoading.showFailed();
						}
					});
			return showDialog(context, title, cContentView, ok,
					onClickListener, cancel, null);
		}
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn,
			DialogInterface.OnClickListener rightListener,
			CharSequence middleBtn,
			DialogInterface.OnClickListener middleListener,
			DialogInterface.OnCancelListener cancelListener) {
		return showDialog(context, title, msg, leftBtn, leftListener, rightBtn,
				rightListener, middleBtn, middleListener, cancelListener, false);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn,
			DialogInterface.OnClickListener rightListener,
			CharSequence middleBtn,
			DialogInterface.OnClickListener middleListener,
			DialogInterface.OnCancelListener cancelListener, boolean reverse) {
		if (isContextFinished(context)) {
			return null;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setReverse(reverse);

		if (title != null) {
			builder.setTitle(title);
		}
		if (msg != null) {
			builder.setMessage(msg);
		}
		if (leftBtn != null) {
			builder.setPositiveButton(leftBtn, leftListener);
		}
		if (rightBtn != null) {
			builder.setNegativeButton(rightBtn, rightListener);
		}
		if (middleBtn != null) {
			builder.setNeutralButton(middleBtn, middleListener);
		}
		if (cancelListener != null) {
			builder.setOnCancelListener(cancelListener);
		}
		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				}
				return false;
			}
		});

		try {
			return builder.show();
		} catch (Throwable e) {
			Log.e(TAG, "Failed to show the dialog.", e);
			return null;
		}
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			View cContentView, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn,
			DialogInterface.OnClickListener rightListener,
			CharSequence middleBtn,
			DialogInterface.OnClickListener middleListener,
			DialogInterface.OnCancelListener cancelListener, boolean reverse) {
		if (isContextFinished(context)) {
			return null;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setReverse(reverse);

		if (title != null) {
			builder.setTitle(title);
		}
		if (cContentView != null) {
			builder.setView(cContentView);
		}
		if (leftBtn != null) {
			builder.setPositiveButton(leftBtn, leftListener);
		}
		if (rightBtn != null) {
			builder.setNegativeButton(rightBtn, rightListener);
		}
		if (middleBtn != null) {
			builder.setNeutralButton(middleBtn, middleListener);
		}
		if (cancelListener != null) {
			builder.setOnCancelListener(cancelListener);
		}
		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				}
				return false;
			}
		});

		try {
			return builder.show();
		} catch (Throwable e) {
			Log.e(TAG, "Failed to show the dialog.", e);
			return null;
		}
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			View cContentView) {
		return DialogToastUtils.showDialog(context, title, cContentView, null,
				null, null, null, null, null, null, false);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			View cContentView, CharSequence leftBtn,
			OnClickListener leftListener, CharSequence rightBtn,
			OnClickListener rightListener) {
		return DialogToastUtils.showDialog(context, title, cContentView,
				leftBtn, leftListener, rightBtn, rightListener, null, null,
				null, false);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg) {
		return DialogToastUtils.showDialog(context, title, msg, null, null,
				null, null, null, null, null);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, DialogInterface.OnCancelListener cancelListener) {
		return DialogToastUtils.showDialog(context, title, msg, null, null,
				null, null, null, null, cancelListener);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence btn,
			DialogInterface.OnClickListener listener) {
		return DialogToastUtils.showDialog(context, title, msg, btn, listener,
				null, null, null, null, null);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence btn,
			DialogInterface.OnClickListener listener,
			DialogInterface.OnCancelListener cancelListener) {
		return DialogToastUtils.showDialog(context, title, msg, btn, listener,
				null, null, null, null, cancelListener);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn, DialogInterface.OnClickListener rightListener) {
		return DialogToastUtils.showDialog(context, title, msg, leftBtn,
				leftListener, rightBtn, rightListener, null, null, null);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn,
			DialogInterface.OnClickListener rightListener, boolean reverse) {
		return DialogToastUtils.showDialog(context, title, msg, leftBtn,
				leftListener, rightBtn, rightListener, null, null, null,
				reverse);
	}

	public static AlertDialog showDialog(Context context, CharSequence title,
			CharSequence msg, CharSequence leftBtn,
			DialogInterface.OnClickListener leftListener,
			CharSequence rightBtn,
			DialogInterface.OnClickListener rightListener,
			DialogInterface.OnCancelListener cancelListener) {
		return DialogToastUtils.showDialog(context, title, msg, leftBtn,
				leftListener, rightBtn, rightListener, null, null,
				cancelListener);
	}

	public static void showMessage(Context context, String msg) {
		if (DialogToastUtils.toast != null && !DeviceUtils.isIceCreamSandwich()) {
			DialogToastUtils.toast.cancel();
		}
		DialogToastUtils.toast = Toast.makeText(
				context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
		DialogToastUtils.toast.show();
	}

	public static void showMessage(Context context, int resid) {
		showMessage(context, resid, Toast.LENGTH_SHORT);
	}

	public static void showMessage(Context context, CharSequence text) {
		DialogToastUtils.showMessage(context, text, Toast.LENGTH_SHORT);
	}

	public static void showMessage(Context context, int resid, int duration) {
		if (DialogToastUtils.toast != null && !DeviceUtils.isIceCreamSandwich()) {
			DialogToastUtils.toast.cancel();
		}
		DialogToastUtils.toast = Toast.makeText(
				context.getApplicationContext(), resid,
				duration == Toast.LENGTH_LONG ? Toast.LENGTH_LONG
						: Toast.LENGTH_SHORT);
		DialogToastUtils.toast.show();
	}

	public static void showMessage(Context context, CharSequence text,
			int duration) {
		if (DialogToastUtils.toast != null && !DeviceUtils.isIceCreamSandwich()) {
			DialogToastUtils.toast.cancel();
		}
		DialogToastUtils.toast = Toast.makeText(
				context.getApplicationContext(), text,
				duration == Toast.LENGTH_LONG ? Toast.LENGTH_LONG
						: Toast.LENGTH_SHORT);
		DialogToastUtils.toast.show();
	}

	public static ProgressDialog showProgressDialog(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		ProgressDialog progressDialog = new ProgressDialog(context);

		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(indeterminate);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				}
				return false;
			}
		});
		try {
			progressDialog.show();
		} catch (Exception e) {
			Log.e(TAG, "Show progress dialog failed and safe ignored.", e);
		}

		return progressDialog;
	}

	public static ProgressDialog showProgressDialog(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			OnCancelListener onCancelLister) {
		ProgressDialog progressDialog = new ProgressDialog(context);

		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(indeterminate);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(onCancelLister);
		progressDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				}
				return false;
			}
		});
		progressDialog.show();

		return progressDialog;
	}

	public static void dismissProgressDialog(ProgressDialog progressDialog,
			Context context) {
		// check weather the activity is still alive
		if (isContextFinished(context)) {
			Log.e(TAG, "[Silent Exception] Activity ["
					+ context.getClass().getName()
					+ "]finished. No need to Dismiss progress dialog ");
			return;
		}
		if (progressDialog != null && progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			} catch (RuntimeException e) {
				Log.e(TAG,
						"[Silent Exception] Dismiss progress dialog failed. ",
						e);
			}
		}
	}

	public static boolean isContextFinished(Context context) {
		return context instanceof Activity
				&& ((Activity) context).isFinishing();
	}
}
