package com.prettygirl.app.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.pretty.girl.app.R;

public class MiscUtil {
    public static int VISIT_COUNT = 0;

    public static final boolean IS_DEBUG = false;

    public static final String CACHE_DIR_ASSETS = "file:///android_assets/images";

    public final static String SERVER_PHP_GET_FORMAT = "%s?type=%s&&from=%s&&count=%s";

    public final static String SERVER_PHP_UPDATE_FORMAT = "%s?id=%s&&type=%s&&s=%s&&level=%s"
            + (IS_DEBUG ? "&&nocache=true" : "");

    public static final String CACHE_DIR_SDCARD = Environment.getExternalStorageDirectory().getPath() + File.separator
            + "prettygirl";

    public final static String getVoteUrl(Context context) {
        return ServerUtils.getPicServerRoot(context) + "/s/v1/vote.php";
    }

    public final static String getRankUrl(Context context) {
        return ServerUtils.getPicServerRoot(context) + "/s/v1/list.php";
    }

    public final static String readUrl(String url) throws IOException {
        StringBuilder buf = new StringBuilder();
        URL urlToRead = new URL(url + "?" + VISIT_COUNT);
        BufferedReader in = new BufferedReader(new InputStreamReader(urlToRead.openStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            buf.append(inputLine);
        }
        in.close();
        return buf.toString();
    }

    public final static String getAlbumConfigUrl(int index) {
        String fullDirName = String.format("%09d", index);
        String dir1 = String.format("%03d", (int) (index / 1000));
        String dir2 = String.format("%03d", ((int) (index / 1000) / 1000));
        return dir2 + "/" + dir1 + "/" + fullDirName;
    }

    public final static void setPref(String key, String value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).commit();
    }

    public final static String getPref(String key, String defaultValue, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    public final static void setPref(String key, int value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public final static int getPref(String key, int defaultValue, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    public final static String getAlbumImageUrl(String baseUrl, int albumIndex, int imageIndex, boolean simpleVersion) {
        return baseUrl + "imgs/" + getAlbumConfigUrl(albumIndex) + (simpleVersion == true ? "/t_" : "/") + imageIndex
                + ".jpg";
    }

    public final static void toast(int msg, int length, Context context) {
        toast(context.getString(msg), length, context);
    }

    public final static void toast(final String msg, int duration, final Context context) {
        DialogToastUtils.showMessage(context, msg);
        //        Toast t = Toast.makeText(context, msg, duration);
        //        t.setGravity(Gravity.CENTER, 0, 0);
        //        t.show();
    }

    public static void startGooglePlay(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="
                + (packageName == null ? context.getPackageName() : packageName)));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            if ("com.android.vending".equals(info.activityInfo.packageName) == true) {
                context.startActivity(intent.setComponent(new ComponentName("com.android.vending",
                        info.activityInfo.name)));
                break;
            }
        }
    }

    public static void shareApp(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String title = activity.getString(R.string.share_title);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT,
                activity.getString(R.string.share_text, "market://details?id=" + activity.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, title));
    }

    public static void sharePic(Activity activity, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        String title = activity.getString(R.string.share_title);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, title));
    }

    public final static void startGooglePlayByAuthor(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:\"prettygirl\""));
        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            if ("com.android.vending".equals(info.activityInfo.packageName) == true) {
                context.startActivity(intent.setComponent(new ComponentName("com.android.vending",
                        info.activityInfo.name)));
                break;
            }
        }
    }

    public final static void setVisitCount(int count, Context context) {
        VISIT_COUNT = count;
        setPref("visit", count, context);
    }

    public final static int getVisitCount(Context context) {
        return getPref("visit", 0, context);
    }

    public final static String getImageCacheDir() {
        return CACHE_DIR_SDCARD;
    }

    public static boolean isSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isPackageInstalled(String packageName, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkginfo = null;
        try {
            pkginfo = pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pkginfo != null;
    }

    public static void handleMoreAppEvent(Context context) {
        if (MiscUtil.isPackageInstalled("com.prettygirl.app", context) == true) {
            MiscUtil.startGooglePlayByAuthor(context);
        } else {
            MiscUtil.startGooglePlay(context, "com.prettygirl.app");
        }
    }
}
