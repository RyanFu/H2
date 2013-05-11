package com.prettygirl.app.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.TypedValue;

import com.pretty.girl.app.R;

public final class ImageResourceUtils {

    private static final String ASSET_PATH = "file:///android_asset";

    public static String[] PREFIXES;
    public static String[] PREFIXES_ASSET;

    private static ConcurrentHashMap<Integer, Integer> mChangedIndex = new ConcurrentHashMap<Integer, Integer>();
    private static ConcurrentHashMap<Integer, ArrayList<String>> mLevelIndex = new ConcurrentHashMap<Integer, ArrayList<String>>();

    public static ArrayList<String> getImageAssetFileList(Context context, final int level) {
        if (PREFIXES_ASSET == null) {
            PREFIXES_ASSET = context.getResources().getStringArray(R.array.e_navigation_level_res_prefix_asset);
        }
        if (PREFIXES_ASSET.length <= level) {
            return null;
        }
        try {
            String[] files = context.getAssets().list(PREFIXES_ASSET[level]);
            ArrayList<String> result = new ArrayList<String>(files.length / 2);
            for (String filename : files) {
                if (filename.matches(".*s[.]j.*pg")) {
                    result.add(String.format("%s/%s/%s", ASSET_PATH, PREFIXES_ASSET[level], filename));
                }
            }
            Collections.sort(result, new Comparator<String>() {

                @Override
                public int compare(String lhs, String rhs) {
                    int result = lhs.length() - rhs.length();
                    return result == 0 ? lhs.compareTo(rhs) : result;
                }
            });
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<String> getImageCacheFileList(Context context, final int level) {
        if (isSdCardAvailable()) {
            return null;
        }
        if (PREFIXES == null) {
            PREFIXES = context.getResources().getStringArray(R.array.e_navigation_level_res_prefix);
        }
        if (PREFIXES.length <= level) {
            return null;
        }
        String cCacheDir = MiscUtil.getImageCacheDir();
        File dir = new File(cCacheDir);
        if (dir.isFile()) {
            return null;
        }
        String[] files = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches(PREFIXES[level] + ".*s[.]j.*pg");
            }

        });
        if (files == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(files.length);
        for (String filename : files) {
            result.add(String.format("%s/%s", cCacheDir, filename));
        }
        Collections.sort(result, new Comparator<String>() {

            @Override
            public int compare(String lhs, String rhs) {
                int result = lhs.length() - rhs.length();
                return result == 0 ? lhs.compareTo(rhs) : result;
            }
        });
        return result;
    }

    public static String getTotalImageUrlByBaseUrl(String baseUrl) {
        return baseUrl + "/n";
    }

    public static String getTotalImageSuffix() {
        return "/n";
    }
    
    public final static String getServerUrl(Context context, int level) {
        if (PREFIXES == null) {
            PREFIXES = context.getResources().getStringArray(R.array.e_navigation_level_res_prefix);
        }
        if (level >= PREFIXES.length) {
            return null;
        }
        return String.format("%s/%s", ServerUtils.getPicServerRoot(context), PREFIXES[level]);
    }

    public final static Bitmap decodeImageFormFile(Context context, String file) {
        if (file.startsWith(ASSET_PATH)) {
            try {
                return BitmapFactory.decodeStream(context.getAssets().open(file.substring(ASSET_PATH.length() + 1)));
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return BitmapFactory.decodeFile(file);
        }
    }

    public final static int getLastNavigationIndex(Context context, int level) {
        ArrayList<String> files = mLevelIndex.get(level);
        if (files == null) {
            files = loadAllCachedImageInfo(context, level);
        }
        Integer indexInt = mChangedIndex.get(level);
        int index = indexInt == null ? 0 : indexInt.intValue();
        if (index >= files.size()) {
            index = 0;
        }
        return index;
    }

    public final static Bitmap getLastNavigationImage(Context context, int level) {
        ArrayList<String> files = mLevelIndex.get(level);
        if (files == null) {
            files = loadAllCachedImageInfo(context, level);
        }
        if (files == null || files.size() == 0) {
            return null;
        }
        Integer indexInt = mChangedIndex.get(level);
        int index = indexInt == null ? 0 : indexInt.intValue();
        if (index >= files.size()) {
            index = 0;
        }
        return decodeImageFormFile(context, files.get(index));
    }

    public static Bitmap getNavigationImageByIndex(Context context, int level, int index) {
        ArrayList<String> files = mLevelIndex.get(level);
        if (files == null) {
            files = loadAllCachedImageInfo(context, level);
        }
        if (files == null || files.size() == 0) {
            return null;
        }
        if (index >= files.size()) {
            index = 0;
        }
        Bitmap result = decodeImageFormFile(context, files.get(index));
        return result;
    }

    public final static Bitmap getNextNavigationImage(Context context, int level) {
        ArrayList<String> files = mLevelIndex.get(level);
        if (files == null) {
            files = loadAllCachedImageInfo(context, level);
        }
        if (files == null || files.size() == 0) {
            return null;
        }
        Integer indexInt = mChangedIndex.get(level);
        int index = indexInt == null ? 0 : indexInt.intValue();
        index++;
        if (index >= files.size()) {
            index = 0;
        }
        Bitmap result = decodeImageFormFile(context, files.get(index));
        mChangedIndex.put(level, index);
        return result;
    }

    public final static ArrayList<String> loadAllCachedImageInfo(final Context context, final int level) {
        ArrayList<String> assetFiles = getImageAssetFileList(context, level);
        //        ArrayList<String> sdcardFiles = getImageCacheFileList(context, level);
        //        if (assetFiles == null) {
        //            mLevelIndex.put(level, sdcardFiles);
        //            return sdcardFiles;
        //        } else if (sdcardFiles == null) {
        mLevelIndex.put(level, assetFiles);
        AsyncTask<Void, Void, Void> cScanSDCardFiles = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<String> sdcardFiles = getImageCacheFileList(context, level);
                if (sdcardFiles == null) {
                    return null;
                }
                ArrayList<String> currentList = mLevelIndex.get(level);
                currentList.addAll(sdcardFiles);
                mLevelIndex.put(level, currentList);
                return null;
            }

        };
        cScanSDCardFiles.execute();
        return assetFiles;
        //        } else {
        //            assetFiles.addAll(sdcardFiles);
        //            mLevelIndex.put(level, assetFiles);
        //            return assetFiles;
        //        }
    }

    public static float dip2px(Context context, float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public final static String getImageUrlByIndex(String baseUrl, int index, int type, boolean isOffLine) {
        if (isOffLine == true) {
            return String.format("assets://images/%d.jpg", index);
        } else {
            String s = String.format("%09d", index);
            return baseUrl + "/" + s.substring(0, 3) + "/" + s.substring(3, 6) + "/" + s.substring(6, 9)
                    + (type == ImageResourceUtils.TYPE_SNAPSHOT ? "s.jpg" : ".jpg");
        }
    }

    public final static int TYPE_SNAPSHOT = 1;

    public final static int TYPE_ORIGINAL = 2;
}
