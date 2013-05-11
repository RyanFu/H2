package com.prettygirl.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.TypedValue;

public class Utils {

    private static final String TAG = "Utils";
    private static final boolean LOGE_ENABLED = false;
    private static final boolean LOGW_ENABLED = false;

    public static String getLocale(Context context) {
        return context.getResources().getConfiguration().locale.toString();
    }

    public static void handleOutOfMemory() {
        Log.e(TAG, "OutOfMemory happens! GC next.");
        System.gc();
    }

    public static String getLcFromAssets(Context packageContext) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(packageContext.getResources().getAssets().open("cid.dat")));
            String lc = reader.readLine();

            if (lc != null) {
                lc = lc.trim();
            }

            return lc.length() == 0 ? null : lc;
        } catch (Throwable e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "Failed to get the lc info.");
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static String readAssetToString(Context context, String packageName, String filename) {
        InputStream inputStream = null;
        try {
            inputStream = getAssetManager(context, packageName).open(filename);
            return IOUtils.toString(inputStream);
        } catch (NameNotFoundException e) {
            if (LOGW_ENABLED) {
                Log.w(TAG, "Name not found [" + filename + "] on open asset.", e);
            }
            return null;
        } catch (IOException e) {
            if (LOGW_ENABLED) {
                Log.w(TAG, "Read asset [" + filename + "] to string failed.", e);
            }
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static AssetManager getAssetManager(Context context, String packageName) throws NameNotFoundException {
        Context packageContext = context.createPackageContext(packageName, 0);
        return packageContext.getAssets();
    }

    public static int bytesToInt(byte[] b) {
        //0xff 表示的是4个字节的整形，所以ok
        int cc = (b[0] & 0xff) | ((b[1] & 0xff) << 8) | (b[2] & 0xff << 16) | ((b[3] & 0xff) << 24);
        return cc;
    }

    public static float dip2px(Context context, float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

}
