package com.prettygirl.app.model.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.model.ThumbUp;
import com.prettygirl.app.model.ThumbUpList;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.Http;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.ThreadHandler;

public class ThumbUpManager {

    private static final ThumbUpManager INSTANCE = new ThumbUpManager();

    private ThumbUpList mThumbUpList = null;

    private File mCacheDir;

    private static final String FILE_NAME = "2.thumbup";

    private static String mThumbUpFile = null;

    public static final int THUMB_UP = 0;

    public static final int THUMB_DOWN = 1;

    public static final String RANK_LATEST = "bd";

    public static final String RANK_WEEKY = "bw";

    public static final String RANK_MONTHY = "bm";

    public static final int RANK_MAX_COUNT = 120;

    public static final int NORMAL_CACHE_COUNT = 20;

    private ThreadHandler mThreadHandler = new ThreadHandler();

    private ArrayList<ThumbUpChangedListener> mThumbUpChangedListeners;

    private HashMap<String, HashMap<Integer, ThumbUp>> mThumbUpMaps = null;

    public interface ThumbUpChangedListener {

        int INSERTED = 1;

        int UPDATED = 2;

        int DELETED = 3;

        void onChanged(int status, ThumbUp cThumpUp);
    }

    public interface ThumbUpLoadedListener {
        void onLoaded(boolean successed, ThumbUpList cThumbUpList);
    }

    private ThumbUpManager() {
        super();
        mThumbUpChangedListeners = new ArrayList<ThumbUpChangedListener>();
        mThumbUpMaps = new HashMap<String, HashMap<Integer, ThumbUp>>();
    }

    public static ThumbUpManager getInstance() {
        return INSTANCE;
    }

    public void loadThumbUpTop(final View cTextView, final String type, final int offset, final int count,
            final ThumbUpLoadedListener cThumbUpLoadedListener) {
        AsyncTask<Void, Integer, ThumbUpList> mloadAsyncTask = new AsyncTask<Void, Integer, ThumbUpList>() {

            private static final int POST_EXECUTE = 0xffffff1;

            private Handler mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == POST_EXECUTE) {
                        if (msg.obj != null && msg.obj instanceof ThumbUpList && !isCancelled()) {
                            ThumbUpList cThumbUpList = (ThumbUpList) msg.obj;
                            HashMap<Integer, ThumbUp> cThumbUps = mThumbUpMaps.get(type);
                            if (cThumbUps == null) {
                                cThumbUps = new HashMap<Integer, ThumbUp>();
                                mThumbUpMaps.put(type, cThumbUps);
                            }
                            ThumbUp cThumbUp = null;
                            for (int index = 0, size = cThumbUpList.size(); index < size; index++) {
                                cThumbUp = cThumbUpList.get(index);
                                cThumbUps.put(cThumbUp.id, cThumbUp);
                            }
                            mThumbUpMaps.put(type, cThumbUps);
                            if (cThumbUpLoadedListener != null) {
                                cThumbUpLoadedListener.onLoaded(true, cThumbUpList);
                                return;
                            }
                        } else {
                            if (isNumber(type)) {
                                Context context = cTextView.getContext();
                                DialogToastUtils.showMessage(context,
                                        context.getString(R.string.thumb_up_request_failed));
                            }
                        }
                        cThumbUpLoadedListener.onLoaded(false, null);
                    } else {
                        super.handleMessage(msg);
                    }
                }

            };

            @Override
            protected ThumbUpList doInBackground(Void... cVoid) {
                Http http = new Http(cTextView.getContext());
                String getString = http.get(String.format(MiscUtil.SERVER_PHP_GET_FORMAT,
                        MiscUtil.getRankUrl(cTextView.getContext()), type, offset, count));
                try {
                    JSONObject result = new JSONObject(getString);
                    int code = result.getInt("responseCode");
                    if (code == 0) {
                        if (result.has("data")) {
                            JSONArray object = result.getJSONArray("data");
                            if (object == null) {
                                return null;
                            } else {
                                return new ThumbUpList(object);
                            }
                        } else {
                            return null;
                        }
                    }
                } catch (Exception e) {
                    if (MiscUtil.IS_DEBUG) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ThumbUpList result) {
                super.onPostExecute(result);
                Message msg = mHandler.obtainMessage(POST_EXECUTE);
                msg.obj = result;
                msg.sendToTarget();
            }

        };
        mloadAsyncTask.execute();
    }

    public void removeCache(String type) {
        mThumbUpMaps.remove(type);
    }

    /**
     * 
     * @param cTextView
     * @param level
     * @param picId
     * @param type {bd, bw, bm, 0, 1, 2}
     */
    public void loadThumbUpCount(final TextView cUpTextView, final TextView cDownTextView, final int picId,
            final String type, final boolean forceRequest, final int maxPicIndex) {
        mThreadHandler.post(new Runnable() {

            private static final int POST_EXECUTE = 0xffffff1;

            private Handler mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == POST_EXECUTE) {
                        if (msg.obj != null && msg.obj instanceof ThumbUp) {
                            ThumbUp cThumbUp = (ThumbUp) (msg.obj);
                            if (cThumbUp == null) {
                                return;
                            }
                            cUpTextView.setVisibility(View.VISIBLE);
                            String text = cThumbUp.up + "";
                            cUpTextView.setText(formatText(text, text));
                            cUpTextView.setTag(null);

                            cDownTextView.setVisibility(View.VISIBLE);
                            text = cThumbUp.down + "";
                            cDownTextView.setText(formatText(text, text));
                        } else {
                            if (isNumber(type)) {
                                Context context = cUpTextView.getContext();
                                DialogToastUtils.showMessage(context,
                                        context.getString(R.string.thumb_up_request_failed));
                            }
                        }
                    } else {
                        super.handleMessage(msg);
                    }
                }

            };

            private void postResult(ThumbUp cThumbUp) {
                Message msg = mHandler.obtainMessage(POST_EXECUTE);
                msg.obj = cThumbUp;
                msg.sendToTarget();
            }

            @Override
            public void run() {
                HashMap<Integer, ThumbUp> cThumbUps = mThumbUpMaps.get(type);
                if (cThumbUps == null) {
                    cThumbUps = new HashMap<Integer, ThumbUp>();
                    mThumbUpMaps.put(type, cThumbUps);
                }
                if (!forceRequest && cThumbUps.containsKey(picId)) {
                    postResult(cThumbUps.get(picId));
                } else {
                    int cIndex = 0;
                    int cPicId = picId;
                    if (cPicId < NORMAL_CACHE_COUNT) {
                        cPicId = 0;
                    } else if (maxPicIndex - cPicId < (NORMAL_CACHE_COUNT / 2)) {
                        cPicId = maxPicIndex - NORMAL_CACHE_COUNT;
                    } else {
                        do { // 判断其上一个是否包含,如果包含,继续向上找,最多找总数的一半
                            cPicId -= 1;
                            cIndex++;
                            if (cIndex == (NORMAL_CACHE_COUNT / 2)) {
                                break;
                            }
                        } while (!cThumbUps.containsKey(cPicId));
                    }
                    Http http = new Http(cUpTextView.getContext());
                    String getString = http.get(String.format(MiscUtil.SERVER_PHP_GET_FORMAT,
                            MiscUtil.getRankUrl(cUpTextView.getContext()), type, cPicId,
                            ThumbUpManager.NORMAL_CACHE_COUNT));
                    try {
                        JSONObject result = new JSONObject(getString);
                        int code = result.getInt("responseCode");
                        if (code == 0) {
                            if (result.has("data")) {
                                JSONArray object = result.getJSONArray("data");
                                if (object == null) {
                                    postResult(null);
                                } else {
                                    ThumbUpList cThumbUpList = new ThumbUpList(object);
                                    ThumbUp cThumbUp = null;
                                    for (int index = 0, size = cThumbUpList.size(); index < size; index++) {
                                        cThumbUp = cThumbUpList.get(index);
                                        cThumbUps.put(cThumbUp.id, cThumbUp);
                                    }
                                    mThumbUpMaps.put(type, cThumbUps);
                                    postResult(cThumbUps.get(picId));
                                }
                            } else {
                                postResult(null);
                            }
                        }
                    } catch (Exception e) {
                        if (MiscUtil.IS_DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void updateThumbUpCountWithoutCallback(final View cTextView, final int level, final String type,
            final int picId, final int s, final ThumbUpChangedListener changedListener) {
        Object mObj = cTextView.getTag();
        if (mObj != null && mObj instanceof AsyncTask) {
            AsyncTask<?, ?, ?> mloadAsyncTaskTag = (AsyncTask<?, ?, ?>) mObj;
            mloadAsyncTaskTag.cancel(true);
        }
        AsyncTask<Void, Integer, ThumbUp> mloadAsyncTask = new AsyncTask<Void, Integer, ThumbUp>() {

            private static final int POST_EXECUTE = 0xffffff1;

            private Handler mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == POST_EXECUTE) {
                        if (msg.obj != null && msg.obj instanceof ThumbUp && !isCancelled()) {
                            cTextView.setTag(null);
                            if (changedListener != null) {
                                changedListener.onChanged(ThumbUpChangedListener.UPDATED, (ThumbUp) msg.obj);
                            }
                        } else {
                            if (isNumber(type)) {
                                Context context = cTextView.getContext();
                                DialogToastUtils.showMessage(context,
                                        context.getString(R.string.thumb_up_request_failed));
                            }
                        }
                    } else {
                        super.handleMessage(msg);
                    }
                }

            };

            @Override
            protected ThumbUp doInBackground(Void... cVoid) {
                addThumbUp(new ThumbUp(picId, level, s));
                Http http = new Http(cTextView.getContext());
                String getString = http.get(String.format(MiscUtil.SERVER_PHP_UPDATE_FORMAT,
                        MiscUtil.getVoteUrl(cTextView.getContext()), picId, type, s, level));
                try {
                    JSONObject result = new JSONObject(getString);
                    int code = result.getInt("responseCode");
                    if (code == 0) {
                        if (result.has("data")) {
                            JSONObject object = result.getJSONObject("data");
                            if (object == null) {
                                return null;
                            } else {
                                return new ThumbUp(object);
                            }
                        } else {
                            return null;
                        }
                    }
                } catch (Exception e) {
                    if (MiscUtil.IS_DEBUG) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ThumbUp result) {
                super.onPostExecute(result);
                Message msg = mHandler.obtainMessage(POST_EXECUTE);
                msg.obj = result;
                msg.sendToTarget();
            }

        };
        mloadAsyncTask.execute();
        cTextView.setTag(mloadAsyncTask);
    }

    private boolean isNumber(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateThumbUpCount(final TextView cUpTextView, final TextView cDownTextView, final int level,
            final String type, final int picId, final int s, final ThumbUpChangedListener changedListener) {
        Object mObj = cUpTextView.getTag();
        if (mObj != null && mObj instanceof AsyncTask) {
            AsyncTask<?, ?, ?> mloadAsyncTaskTag = (AsyncTask<?, ?, ?>) mObj;
            mloadAsyncTaskTag.cancel(true);
        }
        AsyncTask<Void, Integer, ThumbUp> mloadAsyncTask = new AsyncTask<Void, Integer, ThumbUp>() {

            private static final int POST_EXECUTE = 0xffffff1;

            private static final int POST_EXECUTE1 = 0xffffff2;

            private Handler mHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == POST_EXECUTE) {
                        if (msg.obj != null && msg.obj instanceof ThumbUp && !isCancelled()) {
                            ThumbUp cThumbUp = (ThumbUp) msg.obj;
                            cUpTextView.setVisibility(View.VISIBLE);
                            String text = cThumbUp.up + "";
                            cUpTextView.setText(formatText(text, text));
                            cUpTextView.setTag(null);

                            cDownTextView.setVisibility(View.VISIBLE);
                            text = cThumbUp.down + "";
                            cDownTextView.setText(formatText(text, text));
                            if (changedListener != null) {
                                changedListener.onChanged(ThumbUpChangedListener.UPDATED, cThumbUp);
                            }
                        } else {
                            if (isNumber(type)) {
                                Context context = cUpTextView.getContext();
                                DialogToastUtils.showMessage(context,
                                        context.getString(R.string.thumb_up_request_failed));
                            }
                        }
                    } else if (msg.what == POST_EXECUTE1) {
                        if (changedListener != null) {
                            changedListener.onChanged(ThumbUpChangedListener.UPDATED, null);
                        }
                    } else {
                        super.handleMessage(msg);
                    }
                }

            };

            @Override
            protected ThumbUp doInBackground(Void... cVoid) {
                addThumbUp(new ThumbUp(picId, level, s));
                Message msg = mHandler.obtainMessage(POST_EXECUTE1);
                msg.sendToTarget();
                Http http = new Http(cUpTextView.getContext());
                String getString = http.get(String.format(MiscUtil.SERVER_PHP_UPDATE_FORMAT,
                        MiscUtil.getVoteUrl(cUpTextView.getContext()), picId, type, s, level));
                try {
                    JSONObject result = new JSONObject(getString);
                    int code = result.getInt("responseCode");
                    if (code == 0) {
                        if (result.has("data")) {
                            JSONObject object = result.getJSONObject("data");
                            if (object == null) {
                                return null;
                            } else {
                                return new ThumbUp(object);
                            }
                        } else {
                            return null;
                        }
                    }
                } catch (Exception e) {
                    if (MiscUtil.IS_DEBUG) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ThumbUp result) {
                super.onPostExecute(result);
                Message msg = mHandler.obtainMessage(POST_EXECUTE);
                msg.obj = result;
                msg.sendToTarget();
            }

        };
        mloadAsyncTask.execute();
        cUpTextView.setTag(mloadAsyncTask);
    }

    public static SpannableString formatText(String text, String target) {
        SpannableString result = new SpannableString(text);
        int index = text.lastIndexOf(target);
        int to = index + target.length();
        // 2.0f表示默认字体大小的两倍
        result.setSpan(new RelativeSizeSpan(2.0f), index, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        result.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), index, to,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (to < result.length()) {
            result.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), to, result.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return result;
    }

    public void init(Context cContext) {
        mCacheDir = cContext.getCacheDir();
        mThumbUpFile = mCacheDir.getPath() + File.separator + FILE_NAME;
        loadThumb();
    }

    public void loadThumb() {
        mThumbUpList = ThumbUpList.load(mThumbUpFile);
        if (mThumbUpList == null) {
            mThumbUpList = new ThumbUpList();
        }
    }

    public void registerThumbUpChangedListener(ThumbUpChangedListener cListener) {
        if (!mThumbUpChangedListeners.contains(cListener)) {
            mThumbUpChangedListeners.add(cListener);
        }
    }

    public void unregisterThumbUpChangedListener(ThumbUpChangedListener cListener) {
        mThumbUpChangedListeners.remove(cListener);
    }

    private void onThumbUpDataChanged(int status, ThumbUp cThumbUp) {
        for (ThumbUpChangedListener cListener : mThumbUpChangedListeners) {
            cListener.onChanged(status, cThumbUp);
        }
    }

    public void addThumbUp(ThumbUp cThumbUp) {
        mThumbUpList.addThumbUp(cThumbUp);
        flush(ThumbUpChangedListener.INSERTED, cThumbUp);
    }

    public boolean hadThumbUp(ThumbUp cThumbUp) {
        return mThumbUpList.hasThumbUp(cThumbUp);
    }

    public boolean hadThumbUp(int imageId, int level, int method) {
        return mThumbUpList.hasThumbUp(new ThumbUp(imageId, level, method));
    }

    public void removeThumbUp(ThumbUp cThumbUp) {
        if (mThumbUpList.removeThumbUp(cThumbUp)) {
            flush(ThumbUpChangedListener.DELETED, cThumbUp);
        }
    }

    public int getThumbUpSize() {
        return mThumbUpList.size();
    }

    public ThumbUpList getThumbUpList() {
        return mThumbUpList;
    }

    public void flush(int status, ThumbUp cThumbUp) {
        mThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                mThumbUpList.save(mThumbUpFile);
            }

        });
        onThumbUpDataChanged(status, cThumbUp);
    }

}
