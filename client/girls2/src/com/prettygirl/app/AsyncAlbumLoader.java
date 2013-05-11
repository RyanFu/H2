package com.prettygirl.app;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

import com.prettygirl.app.utils.MiscUtil;

public class AsyncAlbumLoader implements Callback {
    private static AsyncAlbumLoader sInstance;
    private Handler mHandler;

    private AsyncAlbumLoader() {
        HandlerThread t = new HandlerThread(AsyncAlbumLoader.class.getName());
        t.start();
        mHandler = new Handler(t.getLooper(), this);
    }

    public final static AsyncAlbumLoader getInstance() {
        if (sInstance == null) {
            sInstance = new AsyncAlbumLoader();
        }
        return sInstance;
    }

    public void loadAlbumList(int startIndex, int count, String startUrl, Handler replyHandler) {
        LoadRequest r = new LoadRequest();
        r.what = LoadRequest.LOAD_ALBUM_INFO_LIST;
        r.arg1 = startIndex;
        r.arg2 = count;
        r.str1 = startUrl;
        r.replyHandler = replyHandler;
        mHandler.obtainMessage(LoadRequest.LOAD_ALBUM_INFO_LIST, r).sendToTarget();
    }

    public void loadAlbumInfo(int albumIndex, String startUrl, Handler replyHandler) {
        LoadRequest r = new LoadRequest();
        r.arg1 = albumIndex;
        r.str1 = startUrl;
        r.replyHandler = replyHandler;
        mHandler.obtainMessage(LoadRequest.LOAD_SINGLE_ALBUM_INFO, r).sendToTarget();
    }

    public void loadTotalAlbumInfo(String startUrl, Handler replyHandler) {
        LoadRequest r = new LoadRequest();
        r.what = LoadRequest.LOAD_TOTAL_ALBUM_INFO;
        r.str1 = startUrl;
        r.replyHandler = replyHandler;
        mHandler.obtainMessage(LoadRequest.LOAD_TOTAL_ALBUM_INFO, r).sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        final LoadRequest req = (LoadRequest) msg.obj;
        final Handler replyTo = req.replyHandler;
        switch (msg.what) {
        case LoadRequest.LOAD_SINGLE_ALBUM_INFO: {
            AlbumInfo info = doLoadAlbumInfo(req.arg1, req.str1);
            if (info != null) {
                info.albumIndex = req.arg1;
            }
            replyTo.obtainMessage(LoadRequest.LOAD_SINGLE_ALBUM_INFO_DONE, info).sendToTarget();
            break;
        }
        case LoadRequest.LOAD_ALBUM_INFO_LIST: {
            int startIndex = req.arg1, count = req.arg2;
            ArrayList<AlbumInfo> infos = new ArrayList<AlbumInfo>(count);
            for (; startIndex > 0 & count > 0; count--) {
                AlbumInfo info = new AlbumInfo();
                info.albumIndex = startIndex--;
                info.baseUrl = req.str1;
                infos.add(info);
            }
            Message rm = replyTo.obtainMessage(LoadRequest.LOAD_ALBUM_INFO_LIST_DONE, infos);
            replyTo.sendMessageDelayed(rm, 100);
            break;
        }
        case LoadRequest.LOAD_TOTAL_ALBUM_INFO: {
            int total = doLoadTotalAlbumInfo(req.str1);
            Message r = replyTo.obtainMessage(LoadRequest.LOAD_TOTAL_ALBUM_INFO_DONE);
            r.arg1 = total;
            r.sendToTarget();
            break;
        }
        }
        return true;
    }

    private AlbumInfo doLoadAlbumInfo(int index, String startingUrl) {
        try {
            String albumUrl = MiscUtil.getAlbumConfigUrl(index) + "/";
            String url = startingUrl + "imgs/" + albumUrl + "a.js";
            String content = MiscUtil.readUrl(url);
            JSONObject jsonObject = new JSONObject(content);
            int total = jsonObject.getInt("total");
            ImageInfo[] urls = new ImageInfo[total];
            for (int i = 0; i < total; i++) {
                ImageInfo r = new ImageInfo();
                r.albumIndex = index;
                r.baseUrl = startingUrl;
                r.imageIndex = i + 1;
                urls[i] = r;
            }
            AlbumInfo r = new AlbumInfo();
            r.images = urls;
            r.baseUrl = startingUrl;
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int doLoadTotalAlbumInfo(String startingUrl) {
        try {
            String url = startingUrl + "total.js";
            String content = MiscUtil.readUrl(url);
            return new JSONObject(content).getInt("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
