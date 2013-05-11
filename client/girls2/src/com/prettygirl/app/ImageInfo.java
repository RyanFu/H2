package com.prettygirl.app;

import com.prettygirl.app.utils.MiscUtil;


public class ImageInfo {
    public String baseUrl;
    public int albumIndex;
    public int imageIndex;
    public boolean isSimple;

    public final String url(boolean simpleVersion) {
        return MiscUtil.getAlbumImageUrl(baseUrl, albumIndex, imageIndex, simpleVersion);
    }
}
