package com.prettygirl.app.utils;

import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

/**
 * 机型识别工具类.
 */
public class DeviceUtils {

    public static final int ANDROID_SDK_VERSION = Build.VERSION.SDK_INT;

    private static final String PRODUCT = Build.PRODUCT.toLowerCase();

    private static final String MODEL = Build.MODEL.toLowerCase();

    private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();

    private static final String DISPLAY = Build.DISPLAY.toLowerCase();

    private DeviceUtils() {
    }

    public static boolean isMeizuM9() {
        return PRODUCT.contains("meizu_m9") && MODEL.contains("m9");
    }

    public static boolean isMeizuMX() {
        return PRODUCT.contains("meizu_mx");
    }

    public static boolean isHtcDevice() {
        return MODEL.contains("htc") || MODEL.contains("desire");
    }

    public static boolean isLephoneDevice() {
        return PRODUCT.contains("lephone");
    }

    public static boolean isZTEU880() {
        return MANUFACTURER.equals("zte") && MODEL.contains("blade");
    }

    /**
     * 制造商：ZTE
     * 型号：ZTE-U V880
     * @return
     */
    public static boolean isZTEUV880() {
        return MANUFACTURER.equals("zte") && MODEL.contains("zte-u v880");
    }

    public static boolean isHTCHD2() {
        return MANUFACTURER.equals("htc") && MODEL.contains("hd2");
    }

    public static boolean isHTCOneX() {
        return MANUFACTURER.equals("htc") && MODEL.contains("htc one x");
    }

    public static boolean isI9100() {
        return MANUFACTURER.equals("samsung") && MODEL.equals("gt-i9100");
    }

    public static boolean isMiui() {
        return MODEL.startsWith("mi-one");
    }

    public static boolean isGtS5830() {
        return MODEL.equalsIgnoreCase("gt-s5830");
    }

    public static boolean isGtS5830i() {
        return MODEL.equalsIgnoreCase("gt-s5830i");
    }

    public static boolean isGTP1000() {
        return MODEL.equalsIgnoreCase("gt-p1000");
    }

    public static boolean isMb525() {
        return MODEL.startsWith("mb525");
    }

    public static boolean isMe525() {
        return MODEL.startsWith("me525");
    }

    public static boolean isMb526() {
        return MODEL.startsWith("mb526");
    }

    public static boolean isMe526() {
        return MODEL.startsWith("me526");
    }

    public static boolean isMe860() {
        return MODEL.startsWith("me860");
    }

    public static boolean isMe865() {
        return MODEL.startsWith("me865");
    }

    public static boolean isYulong() {
        return MANUFACTURER.equalsIgnoreCase("yulong");
    }

    public static boolean isKindleFire() {
        return MODEL.contains("kindle fire");
    }

    public static boolean isHtcG7() {
        return MANUFACTURER.equals("htc") && MODEL.equals("htc desire");
    }

    public static boolean isMiuiRom() {
        return DISPLAY.toLowerCase().contains("miui") || DISPLAY.toLowerCase().indexOf("mione") >= 0;
    }

    /**
     * 特工机 deovo V5
     * MANUFACTURER: NVIDIA, PRODUCT: kai ID:IML74K, brand:generic, display:IML74K.V03.023.1992-user, MODEL: deovo V5, screen: 720*1184 DPI:320
     * @return
     */
    public static boolean isDeovoV5() {
        return MODEL.contains("deovo v5");
    }

    /**
     * 特工机 BOVO
     * MANUFACTURER: BOVO, PRODUCT: full_blaze ID:IMM76D, brand:BOVO, display:IMM76D, MODEL: S-F16, screen: 720*1184 DPI:320
     * @return
     */
    public static boolean isBOVO() {
        return MANUFACTURER.equals("bovo") && MODEL.equals("s-f16");
    }

    /**
     * 三星S3(I9300)
     * MANUFACTURER: samsung, PRODUCT: m0xx ID:IMM76D, brand:samsung, display:IMM76D.I9300XXBLG1, MODEL: GT-I9300, screen: 720*1280 DPI:320
     * @return
     */
    public static boolean isS3() {
        return MANUFACTURER.equals("samsung") && MODEL.equals("gt-i9300");
    }

    /**
     * HTC G8
     * MANUFACTURER: HTC, PRODUCT: htc_buzz ID:GRI40, brand:htc_wwe, display:GWK74, MODEL: HTC Wildfire, screen: 240*320 DPI:120
     * @return
     */
    public static boolean isHtcG8() {
        return MANUFACTURER.equals("htc") && MODEL.equals("htc wildfire");
    }

    /**
     * HUAWEI U9200
     * MANUFACTURER: huawei, PRODUCT: viva, MODEL: u9200, display: u9200-1v100r001chnc00b508
     * */
    public static boolean isU9200() {
        return MANUFACTURER.equals("huawei") && MODEL.equals("u9200");
    }

    /**
     * 判断是否 Android 4.1
     * @return
     */
    public static boolean isApiLevel16() {
        return ANDROID_SDK_VERSION >= 16;
    }

    public static boolean isIceCreamSandwich() {
        return ANDROID_SDK_VERSION >= 14;
    }

    public static boolean isHoneycomb() {
        return ANDROID_SDK_VERSION >= 11 && ANDROID_SDK_VERSION < 14;
    }

    /**
     * if android sdk is 2.2
     *
     * @return
     */
    public static boolean isFroyo() {
        return ANDROID_SDK_VERSION == 8;
    }

    /**
     * if android sdk is 2.1
     *
     * @return
     */
    public static boolean isEclair() {
        return ANDROID_SDK_VERSION == 7;
    }

    public static boolean isSendBroadcastDirectlySupported() {
        return ANDROID_SDK_VERSION < 12;
    }

    public static boolean isLockScreenSupported() {
        return !isKindleFire();
    }

    public static void printDeviceInfo() {
        Log.d("aap", "PRODUCT = " + PRODUCT + ", MODEL = " + MODEL + ", MANUFACTURER = " + MANUFACTURER);
    }

    /**
     * 某些机型的 PackageManager.resolveActivity 以及 PackageManager.queryIntentActivities 返回为空
     * 此处对这些机型做特殊处理
     * @param action
     * @param uri
     * @return
     */
    public static ComponentName resolveActivity(String action, String uri) {
        if (isZTEUV880()) {
            if ("android.intent.action.VIEW".equals(action) && "content://com.android.contacts/contacts".equals(uri)) {
                ComponentName cn = new ComponentName("com.android.contacts",
                        "com.android.contacts.DialtactsContactsEntryActivity");
                return cn;
            }
        }
        return null;
    }
}
