package com.prettygirl.app;


public class PrefKey {

    public final static String KEY_NORMAL_ALBUM_COUNT = "total_album_count";
    public final static String KEY_PRIVATE_ALBUM_COUNT = "total_private_album_count";

    public final static String PRIVATE_MODE_ENABLED = "private_mode_enabled";
    public final static String REVIEW_REQUEST_SHOWED = "review_request_showed";

    public final static int MIN_VISIT_COUNT_BEFORE_PRIVATE_MODE_ENABLED = 30;
    public final static int MIN_VISIT_COUNT_BEFORE_REQUEST_REVIEW = MIN_VISIT_COUNT_BEFORE_PRIVATE_MODE_ENABLED + 15;
}
