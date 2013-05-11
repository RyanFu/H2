package com.prettygirl.app.utils;

import java.io.File;
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

public final class AdUtils {

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

    public static void handleMoreAppEvent(Context context) {
        if (isPackageInstalled("com.prettygirl.app", context) == true) {
            startGooglePlayByAuthor(context);
        } else {
            startGooglePlay(context, "com.prettygirl.app");
        }
    }

    public static void shareApp(Activity activity, String title, int textFormat) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT,
                activity.getString(textFormat, "market://details?id=" + activity.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, title));
    }

    public static void sharePic(Activity activity, File file, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, title));
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

}
