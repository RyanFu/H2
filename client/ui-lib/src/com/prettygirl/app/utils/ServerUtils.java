package com.prettygirl.app.utils;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.prettygirl.app.utils.Http.Callback;
import com.umeng.analytics.MobclickAgent;

public final class ServerUtils {

    private static final String SERVER_IP = "http://106.187.48.40";

    private static ArrayList<String> SERVER_IPS = new ArrayList<String>();

    {
        SERVER_IPS.add(SERVER_IP);
    }

    private static final String ONLINE_CONFIG_KEY_PIC_SERVER_IP = "pic_server_ip";

    public static String mServerIp = null;

    /**
     * 获取图片服务器地址
     * @param context
     * @return
     */
    public static String getPicServerRoot(Context context) {
        String value = MobclickAgent.getConfigParams(context, ONLINE_CONFIG_KEY_PIC_SERVER_IP);
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        return SERVER_IP;
    }

    public static final void tryGet(final Context context, String sub, final Callback<String> cCallback) {
        String value = MobclickAgent.getConfigParams(context, ONLINE_CONFIG_KEY_PIC_SERVER_IP);
        if (!TextUtils.isEmpty(value)) {
            SERVER_IPS.add(0, value);
        }
        tryGet(context, 0, 0, sub, SERVER_IPS, new Callback<String>() {

            @Override
            public void onSuccess(String url, int status, String result) {
                for (String uprefix : SERVER_IPS) {
                    if (url.startsWith(uprefix)) {
                        cCallback.onSuccess(url, status, result);
                        mServerIp = uprefix;
                        return;
                    }
                }
                mServerIp = SERVER_IPS.get(0);
                cCallback.onSuccess(url, status, result);
            }

            @Override
            public void onFailure(String url, Exception e) {
                mServerIp = SERVER_IPS.get(0);
                cCallback.onFailure(url, e);
            }
        });
    }

    public static void tryGet(final Context context, final int cIndex, final int uIndex, final String sub,
            final ArrayList<String> urls, final Callback<String> cCallback) {
        Http http = new Http(context);
        System.out.println("-------http---try------" + cIndex + "-----" + urls.get(uIndex) + sub);
        http.get(urls.get(uIndex) + sub, new Callback<String>() {

            @Override
            public void onSuccess(String url, int status, String result) {
                cCallback.onSuccess(url, status, result);
            }

            @Override
            public void onFailure(String url, Exception e) {
                if (cIndex < 2) {
                    tryGet(context, cIndex + 1, uIndex, sub, urls, cCallback);
                } else {
                    if (uIndex < (urls.size() - 1)) {
                        tryGet(context, 0, uIndex + 1, sub, urls, cCallback);
                    } else {
                        cCallback.onFailure(url, e);
                    }
                }
            }
        });
    }
}
