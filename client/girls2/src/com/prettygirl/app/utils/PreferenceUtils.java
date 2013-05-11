package com.prettygirl.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class PreferenceUtils {

	public static final String KEY_LEVEL = "";
	public static final String KEY_LAST_SHOW_AD_DIALOG_DATA = "key_last_show_ad_dialog_data";

	private static SharedPreferences mSharedPreferences;

	public static final void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(
				context.getPackageName(), Context.MODE_PRIVATE);
	}

	public static final String getString(String key, String defValue) {
		return mSharedPreferences.getString(key, defValue);
	}

	public static final void setString(String key, String value) {
		Editor cEditor = mSharedPreferences.edit();
		cEditor.putString(key, value);
		cEditor.commit();
	}

	public static final int getInt(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}

	public static final void setInt(String key, int value) {
		Editor cEditor = mSharedPreferences.edit();
		cEditor.putInt(key, value);
		cEditor.commit();
	}

	public static final long getLong(String key, long defValue) {
		return mSharedPreferences.getLong(key, defValue);
	}

	public static final void setLong(String key, long value) {
		Editor cEditor = mSharedPreferences.edit();
		cEditor.putLong(key, value);
		cEditor.commit();
	}
}
