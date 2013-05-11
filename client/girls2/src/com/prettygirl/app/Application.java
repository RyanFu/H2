package com.prettygirl.app;

import java.io.File;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.utils.GirlLoader;
import com.prettygirl.app.utils.MUnlimitedDiscCache;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.PreferenceUtils;

public class Application extends android.app.Application {

    public static boolean DEFAULT_IS_OFFLINE = true;

    public static boolean OFFLINE_BROWSER_ONLINE_ENABLE = true;
    
    public static int OFFLINE_BROWSER_MAX_PAGE_COUNT = 12;
    
    private static AlbumCacheFileNameGenerator mAlbumCacheFileNameGenerator;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationInfo ai;
        try {
            ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Application.DEFAULT_IS_OFFLINE = (Boolean) ai.metaData.get("OFFLINE");
            if (Application.DEFAULT_IS_OFFLINE == true) {
                GirlLoader.DEFAULT_COUNT = guessOfflineImageCount();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        final File cacheDir = new File(MiscUtil.getImageCacheDir());
        if (cacheDir.exists() == false || cacheDir.isDirectory() == false) {
            cacheDir.mkdir();
        }
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        PreferenceUtils.init(this);
        long halfOfAvailableMegs = mi.availMem / 2;
        int inMemoryCacheSize;
        if (halfOfAvailableMegs > 100 * 1024) {
            inMemoryCacheSize = 10 * 1024 * 1024;
        } else {
            inMemoryCacheSize = Math.min(5 * 1024 * 1024, ((int) halfOfAvailableMegs) - 3 * 1024 * 1024);
        }
        Log.i(getApplicationInfo().packageName, "Using in memory cache with size of " + inMemoryCacheSize);

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .discCache(
                        new MUnlimitedDiscCache(cacheDir,
                                mAlbumCacheFileNameGenerator = new AlbumCacheFileNameGenerator(this)))
                .memoryCache(new FIFOLimitedMemoryCache(inMemoryCacheSize))
                .imageDownloader(new BaseImageDownloader(this)).tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);

        MiscUtil.VISIT_COUNT = MiscUtil.getVisitCount(this);

        if (getResources().getDisplayMetrics().widthPixels <= 480) {
            GirlLoader.IS_WIDTH_LE_480 = true;
        } else {
            GirlLoader.IS_WIDTH_LE_480 = false;
        }

        FavoriteManager.getInstance().init(this);
        ThumbUpManager.getInstance().init(this);
    }

    public static String generatorFileName(String url) {
        return mAlbumCacheFileNameGenerator.generate(url);
    }

    private final int guessOfflineImageCount() throws Exception {
        int r = 0;
        for (String s : getAssets().list("images")) {
            int lastDot = s.lastIndexOf('.');
            if (lastDot > 0) {
                try {
                    Integer.parseInt(s.substring(0, lastDot));
                    r++;
                } catch (Exception e) {
                }
            }
        }
        return r;
    }
}
