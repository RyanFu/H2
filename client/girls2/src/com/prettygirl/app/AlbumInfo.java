package com.prettygirl.app;

import com.prettygirl.app.utils.MiscUtil;


public class AlbumInfo {
    public ImageInfo[] images;
    public int imageCount = -1;
    public int albumIndex;
    public String baseUrl;

    public final String imageUrl(int imageIndex, boolean isSimpleVersion) {
        return MiscUtil.getAlbumImageUrl(baseUrl, albumIndex, imageIndex, isSimpleVersion);
    }
}
