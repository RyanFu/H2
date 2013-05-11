package com.prettygirl.app.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 所有位图Bitmpa对象相关的处理，暂时放在这个类<br>
 * 加载/转换/保存/等等（图形变换、图像算法不在此）
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    public static final String IMAGE_FILE_URI_PREFIX = "file:///";

    public static final String IMAGE_CONTENT_URI_PREFIX = "content://media/external/images/media";

    public static final String EXTENSION_JPG = ".jpg";
    public static final String EXTENSION_JPEG = ".jpeg";
    public static final String EXTENSION_PNG = ".png";

    public static final int TRY_GET_BITMAP_FROM_VIEW_MAX_REPEAT_TIME = 2;

    private static final boolean LOGE_ENABLED = false;

    private static final boolean LOGW_ENABLED = false;

    /*
     * return bitmap maybe same as src
     */
    public static Bitmap createScaledBitmap(Bitmap src, float scale, boolean filter) {
        return createScaledBitmap(src, (int) (src.getWidth() * scale), (int) (src.getHeight() * scale), filter);
    }

    /*
     * return bitmap maybe same as src
     */
    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) {
        try {
            return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
        } catch (OutOfMemoryError e) {
            Utils.handleOutOfMemory();
            return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter);
        }
    }

    /**
     * Read scalee bitmap from resourceID.
     * 
     * @param file
     * @param filename
     * @param requiredwidth
     * @param requiredheight
     * @return Built bitmap, or <code>null</code> if not exists
     */
    public static Bitmap decodeInputStreamToBitmap(InputStream bitmapInputStream, int requiredwidth,
            int requiredheight, boolean lowQualityFlag) {
        if (!bitmapInputStream.markSupported()) {
            bitmapInputStream = new BufferedInputStream(bitmapInputStream);
        }
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(bitmapInputStream, null, op);
            int width = op.outWidth;
            int height = op.outHeight;
            int scale = 1;
            while (true) {
                if (width / 2 < requiredwidth || height / 2 < requiredheight) {
                    break;
                }
                width /= 2;
                height /= 2;
                scale *= 2;
            }
            op = new BitmapFactory.Options();
            op.inSampleSize = scale;
            if (lowQualityFlag) {
                op = getLowQualityOptions(op);
            }
            bitmapInputStream.reset();
            Bitmap result = BitmapFactory.decodeStream(bitmapInputStream, null, op);
            Bitmap scaledBitmap = createScaledBitmap(result, requiredwidth, requiredheight, true);
            if (scaledBitmap != result) {
                recycleBitmap(result);
            }
            return scaledBitmap;
        } catch (Throwable e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "decodeInputStreamToBitmap failed", e);
            }
        }
        return null;
    }

    /**
     * Read asset to bitmap.
     * <p>
     * The parameter resPrefix should be only include path and base name of
     * asset file, this method will find matched image file automatically. The
     * order of matching is {@link EXTENSION_JPG}, {@link EXTENSION_PNG} and
     * {@link EXTENSION_JPEG}
     * </p>
     * 
     * @param context
     *            Application context
     * @param packageName
     *            Package name to looking for
     * @param resPrefix
     *            Resource name in asset directory, should only include path and
     *            base name of file
     * @param width
     *            Width of output image, should always less than a half of
     *            original image width
     * @param height
     *            Height of output image, should always less than a half of
     *            original image height
     * @return Built bitmap, or <code>null</code> if not exists
     */
    public static Bitmap readAssetResToBitmap(Context context, String packageName, String resPrefix, int width,
            int height) {
        Bitmap bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_JPG, width, height);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_PNG, width, height);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_JPEG, width, height);
        return bitmap;
    }

    /**
     * Read asset to bitmap.
     * <p>
     * The parameter resPrefix should be only include path and base name of
     * asset file, this method will find matched image file automatically. The
     * order of matching is {@link EXTENSION_JPG}, {@link EXTENSION_PNG} and
     * {@link EXTENSION_JPEG}
     * </p>
     * 
     * @param context
     *            Application context
     * @param packageName
     *            Package name to looking for
     * @param resPrefix
     *            Resource name in asset directory, should only include path and
     *            base name of file
     * @return Built bitmap, or <code>null</code> if not exists
     */
    public static Bitmap readAssetResToBitmap(Context context, String packageName, String resPrefix) {
        Bitmap bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_JPG, -1, -1);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_PNG, -1, -1);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = readAssetToBitmap(context, packageName, resPrefix + EXTENSION_JPEG, -1, -1);
        return bitmap;
    }

    /**
     * Read asset to bitmap.
     * <p>
     * Will return the original image when either width or height is negative.
     * </p>
     * 
     * @param context
     *            Application context
     * @param packageName
     *            Package name to looking for
     * @param filename
     *            Filename in asset directory
     * @param width
     *            Width of output image, should always less than a half of
     *            original image width
     * @param height
     *            Height of output image, should always less than a half of
     *            original image height
     * @return Built bitmap, or <code>null</code> if not exists
     */
    public static Bitmap readAssetToBitmap(Context context, String packageName, String filename, int width, int height) {
        InputStream inputStream = null;
        try {
            inputStream = Utils.getAssetManager(context, packageName).open(filename);
            if (width < 0 || height < 0) {
                return decodeStream(inputStream, true);
            } else {
                return BitmapUtils.decodeInputStreamToBitmap(inputStream, width, height, true);
            }
        } catch (NameNotFoundException e) {
            if (LOGW_ENABLED) {
                Log.w(TAG, "Name not found [" + filename + "] on open asset.", e);
            }
            return null;
        } catch (IOException e) {
            if (LOGW_ENABLED) {
                Log.w(TAG, "Read asset [" + filename + "] to bitmap failed.", e);
            }
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Read scalee bitmap from file.
     * 
     * @param file
     *            absolute path
     * @param filename
     * @param requiredwidth
     * @param requiredheight
     * @return Built bitmap, or <code>null</code> if not exists
     */
    public static Bitmap decodeFileToBitmap(File bitmapfile, int requiredwidth, int requiredheight,
            boolean lowQualityFlag) {

        if (!bitmapfile.exists()) {
            return null;
        }

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;

        try {
            FileInputStream fis = new FileInputStream(bitmapfile);
            BitmapFactory.decodeStream(fis, null, op);
            int width = op.outWidth;
            int height = op.outHeight;
            int scale = 1;
            while (true) {
                if (width / 2 < requiredwidth || height / 2 < requiredheight) {
                    break;
                }
                width /= 2;
                height /= 2;
                scale *= 2;
            }
            op = new BitmapFactory.Options();
            op.inSampleSize = scale;
            if (lowQualityFlag) {
                op = getLowQualityOptions(op);
            }

            Bitmap bitmap = BitmapFactory.decodeFile(bitmapfile.getAbsolutePath(), op);
            Bitmap scaledBitmap = createScaledBitmap(bitmap, requiredwidth, requiredheight, true);
            fis.close();
            if (bitmap != scaledBitmap) {
                bitmap.recycle();
            }
            return scaledBitmap;
        } catch (Throwable e) {
            if (LOGE_ENABLED) {
                Log.e(TAG, "decodeFileToBitmap failed", e);
            }
        }
        return null;
    }

    /*
     * decodeResource get bitmap from res id scaleSize The sample size is the
     * number of pixels in either dimension that correspond to a single pixel in
     * the decoded bitmap. lowQualityFlag if you want save more memery
     */
    public static Bitmap decodeResource(Resources res, int id, int scaleSize, boolean lowQualityFlag) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = scaleSize;

        if (lowQualityFlag) {
            op = getLowQualityOptions(op);
        }
        return BitmapFactory.decodeResource(res, id, op);
    }

    /**
     * get lowest options of bitmap to slow down the memory coast
     * 
     * @param op
     *            null is ok
     * @return
     */
    public static BitmapFactory.Options getLowQualityOptions(BitmapFactory.Options op) {
        if (op == null) {
            op = new BitmapFactory.Options();
        }
        op.inPurgeable = true;
        op.inPreferredConfig = Bitmap.Config.RGB_565;
        op.inDither = false;
        op.inInputShareable = true;
        return op;
    }

    public static Bitmap decodeFile(String pathName, boolean lowQualityFlag) {
        if (pathName == null || !new File(pathName).exists()) {
            return null;
        }

        BitmapFactory.Options op = new BitmapFactory.Options();
        if (lowQualityFlag) {
            op = getLowQualityOptions(op);
        }
        op.inDensity = DisplayMetrics.DENSITY_HIGH;
        op.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;

        return BitmapFactory.decodeFile(pathName, op);
    }

    public static Bitmap decodeStream(InputStream is, boolean lowQualityFlag) {
        BitmapFactory.Options op = null;
        if (lowQualityFlag) {
            op = getLowQualityOptions(null);
        }
        return op == null ? BitmapFactory.decodeStream(is) : BitmapFactory.decodeStream(is, null, op);
    }

    /**
     * 创建重复平铺的Drawable
     * @return 重复平铺的图片
     */
    public static Drawable createTiledRepeatDrawable(Bitmap bitmap, int color, int width, int height) {
        // 创建渲染器  
        BitmapShader bitmapShader = new BitmapShader(bitmap, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
        //得到画笔并设置渲染器
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setShader(bitmapShader);
        //设置显示区域  
        shapeDrawable.setBounds(20, 20, width - 20, height - 20);
        return shapeDrawable;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
