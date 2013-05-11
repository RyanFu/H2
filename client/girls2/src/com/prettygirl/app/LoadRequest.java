package com.prettygirl.app;

import android.os.Handler;

public class LoadRequest {

    // Load the number of images in a single album
    public final static int LOAD_SINGLE_ALBUM_INFO_DONE = 10001;
    public final static int LOAD_SINGLE_ALBUM_INFO = 10001;
    // Load the total number of albums
    public final static int LOAD_TOTAL_ALBUM_INFO_DONE = 10003;
    public final static int LOAD_TOTAL_ALBUM_INFO = 10004;
    // Load a list of album infos
    public final static int LOAD_ALBUM_INFO_LIST_DONE = 10005;
    public final static int LOAD_ALBUM_INFO_LIST = 10006;

    public int what, arg1, arg2, arg3;
    public String str1, str2;
    public Object obj1, obj2;
    public Handler replyHandler;

    public LoadRequest reset() {
        what = 0;
        arg1 = 0;
        arg2 = 0;
        arg3 = 0;
        str1 = null;
        str2 = null;
        obj1 = null;
        obj2 = null;
        replyHandler = null;
        return this;
    }
}
