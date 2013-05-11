package com.prettygirl.app.model.provider;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

import com.prettygirl.app.model.Favorite;
import com.prettygirl.app.model.FavoriteList;

public class FavoriteManager {

    private static final FavoriteManager INSTANCE = new FavoriteManager();

    private FavoriteList mFavoriteList = null;

    private File mCacheDir;

    private static final String FILE_NAME = "2.favorite";

    private static String mFavoriteFile = null;

    private Handler mHandler = new Handler();

    private ArrayList<FavoriteChangedListener> mFavoriteChangedListeners;

    public interface FavoriteChangedListener {
        void onChanged();
    }

    private FavoriteManager() {
        super();
        mFavoriteChangedListeners = new ArrayList<FavoriteChangedListener>();
    }

    public static FavoriteManager getInstance() {
        return INSTANCE;
    }

    public void init(Context cContext) {
        mCacheDir = cContext.getCacheDir();
        mFavoriteFile = mCacheDir.getPath() + File.separator + FILE_NAME;
        mFavoriteList = FavoriteList.load(mFavoriteFile);
    }

    public void registerFavoriteChangedListener(FavoriteChangedListener cListener) {
        if (!mFavoriteChangedListeners.contains(cListener)) {
            mFavoriteChangedListeners.add(cListener);
        }
    }

    public void unregisterFavoriteChangedListener(FavoriteChangedListener cListener) {
        mFavoriteChangedListeners.remove(cListener);
    }

    private void onFavoriteDataChanged() {
        for (FavoriteChangedListener cListener : mFavoriteChangedListeners) {
            cListener.onChanged();
        }
    }

    public void addFavorite(Favorite cFavorite) {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        mFavoriteList.addFavorite(cFavorite);
        flush();
    }

    public boolean isFavorite(Favorite cFavorite) {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        return mFavoriteList.isFavorite(cFavorite);
    }

    public boolean isFavorite(int imageId, int level) {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        return mFavoriteList.isFavorite(new Favorite(imageId, level, null));
    }

    public void removeFavorite(Favorite cFavorite) {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        if (mFavoriteList.removeFavorite(cFavorite)) {
            flush();
        }
    }

    public int getFavoriteSize() {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        return mFavoriteList.size();
    }

    public FavoriteList getFavoriteList() {
        if (mFavoriteList == null) {
            mFavoriteList = new FavoriteList();
        }
        return mFavoriteList;
    }

    public void flush() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mFavoriteList.save(mFavoriteFile);
            }

        });
        onFavoriteDataChanged();
    }

}
