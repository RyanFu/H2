package com.prettygirl.app.components;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.Application;
import com.prettygirl.app.BaseActivity;
import com.prettygirl.app.GalleryActivity;
import com.prettygirl.app.GalleryDetailActivity;
import com.prettygirl.app.ImageEditorActivity;
import com.prettygirl.app.model.Favorite;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.GirlLoader;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class GalleryDetailView extends JazzyViewPager {

    private static final int MAX_VIEW_CACHE_COUNTS = 4;

    private View[] mViewCache = new View[MAX_VIEW_CACHE_COUNTS];

    private View mAdView = null;

    private int MAX_INDEX_OFFLINE_TRY_MODEL;

    private MPagerAdapter mPagerAdapter;

    private int mMaxPages;

    private GirlLoader mGirlLoader;

    private Activity mActivity;

    private int mLevel;

    private int mType;

    public GalleryDetailView(Context context) {
        super(context);
    }

    public GalleryDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindActivity(Activity cActivity) {
        mActivity = cActivity;
    }

    public void init(int from, String baseUrl, int level, int cType) {
        mLevel = level;
        mType = cType;
        mGirlLoader = new GirlLoader(baseUrl, cType == BaseActivity.EXT_VALUE_OFFLINE);
        if (cType == BaseActivity.EXT_VALUE_FAVORITE) {
            mMaxPages = FavoriteManager.getInstance().getFavoriteSize();
            if (mMaxPages == 0 && mActivity != null) {
                DialogToastUtils.showMessage(mActivity, mActivity.getString(R.string.favorite_null_msg));
                mActivity.finish();
            }
        } else {
            if (mType == BaseActivity.EXT_VALUE_ONLINE && Application.DEFAULT_IS_OFFLINE) {
                MAX_INDEX_OFFLINE_TRY_MODEL = Application.OFFLINE_BROWSER_MAX_PAGE_COUNT * 6;
            }
            mMaxPages = mGirlLoader.getCachedTotalImageCount(getContext(), level);
        }
        setTransitionEffect(TransitionEffect.CubeOut);
        mPagerAdapter = new MPagerAdapter();
        setAdapter(mPagerAdapter);
        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                final int viewIndex = getCurrentItem() % MAX_VIEW_CACHE_COUNTS;
                if (mViewCache[viewIndex] != null) {
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).setAutoPlay(isAutoPlayOn());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final int viewIndex = getCurrentItem() % MAX_VIEW_CACHE_COUNTS;
                if (mViewCache[viewIndex] != null) {
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).resetState();
                }
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });
        this.setCurrentItem(mMaxPages - 1 - from);
    }

    class MPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mType == BaseActivity.EXT_VALUE_ONLINE && Application.DEFAULT_IS_OFFLINE) {
                return MAX_INDEX_OFFLINE_TRY_MODEL + 1;
            } else {
                return mMaxPages;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // int viewIndex = position % MAX_VIEW_CACHE_COUNTS;
            // mGirlLoader.cancelLoadImage((CustomImageViewWithLoading)
            // mViewCache[viewIndex]);
            // ((CustomImageViewWithLoading)
            // mViewCache[viewIndex]).setImageDrawable(null);
            // ((ViewPager) container).removeView(mViewCache[viewIndex]);
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(final ViewGroup container, final int position) {
            if (position >= MAX_INDEX_OFFLINE_TRY_MODEL && mType == BaseActivity.EXT_VALUE_ONLINE
                    && Application.DEFAULT_IS_OFFLINE) {
                if (mAdView == null) {
                    mAdView = View.inflate(mActivity, R.layout.e_online_try_ad, null);
                    OnClickListener oc = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobclickAgent.onEvent(v.getContext(), UMengKey.DOWNLOAD_ONLINE_APP);
                            MiscUtil.handleMoreAppEvent(v.getContext());
                        }
                    };
                    int count = mGirlLoader.getCachedTotalImageCount(getContext(), mLevel);
                    ((TextView) mAdView.findViewById(R.id.txt)).setText(getContext().getString(R.string.try_online,
                            count));
                    mAdView.findViewById(R.id.more).setOnClickListener(oc);
                    mAdView.findViewById(R.id.download).setOnClickListener(oc);
                    mAdView.setOnClickListener(oc);
                }
                ((ViewPager) container).removeView(mAdView);
                ((ViewPager) container).addView(mAdView, 0);
                setObjectForPosition(mAdView, position);
                return mAdView;
            }
            final int viewIndex = position % MAX_VIEW_CACHE_COUNTS;
            Context mContext = container.getContext();
            if (mViewCache[viewIndex] == null) {
                mViewCache[viewIndex] = new CustomImageViewWithLoading(mContext);
                mViewCache[viewIndex].setClickable(false);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).showMagicRod();
                if (Application.DEFAULT_IS_OFFLINE == true) {
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).setMagicAdVisibility(View.VISIBLE);
                } else { // 仅在ONLINE(根据manifest中配置的模式来判断)下才具有收藏功能
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavoriteVisibility(View.VISIBLE);
                }
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).showMagicEditor(View.VISIBLE);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setMagicRodClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_EDITOR);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            Context cContext = v.getContext();
                            Intent intent = new Intent();
                            intent.setClass(cContext, ImageEditorActivity.class);
                            intent.putExtra(GalleryActivity.EXT_LEVEL_KEY, mLevel);
                            intent.putExtra(BaseActivity.EXT_TYPE, mType);
                            intent.putExtra(BaseActivity.EXT_METHOD, "" + mLevel);
                            intent.putExtra(GalleryDetailActivity.EXT_INT_INDEX, mMaxPages - 1 - ((Integer) obj));
                            intent.putExtra(GalleryDetailActivity.EXT_STRING_BASE_URL, mGirlLoader.getBaseUrl());
                            mActivity.startActivityForResult(intent, 1);
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setShareClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_SHARE);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            String fileName = Application.generatorFileName(ImageResourceUtils.getImageUrlByIndex(
                                    mGirlLoader.getBaseUrl(), mMaxPages - 1 - ((Integer) obj),
                                    ImageResourceUtils.TYPE_ORIGINAL, mType == BaseActivity.EXT_VALUE_OFFLINE));
                            File imageFile = new File(MiscUtil.getImageCacheDir(), fileName);
                            MiscUtil.sharePic(mActivity, imageFile);
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setWallpaperClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_WALLPAPER);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            String fileName = Application.generatorFileName(ImageResourceUtils.getImageUrlByIndex(
                                    mGirlLoader.getBaseUrl(), mMaxPages - 1 - ((Integer) obj),
                                    ImageResourceUtils.TYPE_ORIGINAL, mType == BaseActivity.EXT_VALUE_OFFLINE));
                            File imageFile = new File(MiscUtil.getImageCacheDir(), fileName);
                            int width = mActivity.getWallpaperDesiredMinimumWidth();
                            int height = mActivity.getWallpaperDesiredMinimumHeight();
                            Display d = mActivity.getWindowManager().getDefaultDisplay();
                            @SuppressWarnings("deprecation")
                            Point size = new Point(d.getWidth(), d.getHeight());
                            float spotlightX = (float) size.x / width;
                            float spotlightY = (float) size.y / height;
                            Intent intent = new Intent("com.android.camera.action.CROP")
                                    .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT).putExtra("outputX", width)
                                    .putExtra("outputY", height).putExtra("aspectX", width).putExtra("aspectY", height)
                                    .putExtra("spotlightX", spotlightX).putExtra("spotlightY", spotlightY)
                                    .putExtra("scale", true).putExtra("scaleUpIfNeeded", true)
                                    .putExtra("noFaceDetection", true).putExtra("set-as-wallpaper", true);
                            intent.setDataAndType(Uri.fromFile(imageFile), "image/*");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mActivity.startActivity(intent);
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setSlideShowClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_AUTO_PLAY);
                        boolean isAuto = false;
                        if (isAutoPlayOn()) {
                            cancelAutoPlay();
                        } else {
                            startAutoPlay(3000);
                            isAuto = true;
                        }
                        if (v instanceof CustomImageViewWithLoading) {
                            ((CustomImageViewWithLoading) v).setAutoPlay(isAuto);
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setThumbDownClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.PIC_THUMB_DOWN);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(mLevel, mMaxPages - 1
                                    - ((Integer) obj), ThumbUpManager.THUMB_DOWN, mMaxPages, "" + mLevel);
                        }
                    }

                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setThumbUpClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.PIC_THUMB_UP);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(mLevel, mMaxPages - 1
                                    - ((Integer) obj), ThumbUpManager.THUMB_UP, mMaxPages, "" + mLevel);
                        }
                    }

                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setMagicAdClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_AD);
                        MiscUtil.handleMoreAppEvent(getContext());
                    }

                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavoriteClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_FAVORITE);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            // int pos = mMaxPages - 1 - ((Integer) obj);
                            int pIndex = ((Integer) obj) % MAX_VIEW_CACHE_COUNTS;
                            FavoriteManager mFavoriteManager = FavoriteManager.getInstance();
                            boolean isFavorited = false;
                            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                                int index = mMaxPages - 1 - ((Integer) obj);
                                if (index <= 0) {
                                    index = 0;
                                }
                                Favorite cFavorite = FavoriteManager.getInstance().getFavoriteList().get(index);
                                isFavorited = mFavoriteManager.isFavorite(cFavorite);
                                ((CustomImageViewWithLoading) mViewCache[pIndex]).setFavorited(!isFavorited);
                                if (isFavorited) {
                                    mFavoriteManager.removeFavorite(cFavorite);
                                } else {
                                    mFavoriteManager.addFavorite(cFavorite);
                                }
                            } else {
                                isFavorited = mFavoriteManager.isFavorite(mMaxPages - 1 - ((Integer) obj), mLevel);
                                ((CustomImageViewWithLoading) mViewCache[pIndex]).setFavorited(!isFavorited);
                                if (isFavorited) {
                                    mFavoriteManager.removeFavorite(new Favorite(mMaxPages - 1 - ((Integer) obj),
                                            mLevel, ImageResourceUtils.getImageUrlByIndex(mGirlLoader.getBaseUrl(),
                                                    mMaxPages - 1 - ((Integer) obj), ImageResourceUtils.TYPE_ORIGINAL,
                                                    false)));
                                } else {
                                    mFavoriteManager.addFavorite(new Favorite(mMaxPages - 1 - ((Integer) obj), mLevel,
                                            ImageResourceUtils.getImageUrlByIndex(mGirlLoader.getBaseUrl(), mMaxPages
                                                    - 1 - ((Integer) obj), ImageResourceUtils.TYPE_ORIGINAL, false)));
                                    // 赞一个
                                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(mLevel, mMaxPages
                                            - 1 - ((Integer) obj), ThumbUpManager.THUMB_UP, mMaxPages, "" + mLevel);
                                }
                            }
                        }
                    }

                });
            }
            mGirlLoader.cancelLoadImage((CustomImageViewWithLoading) mViewCache[viewIndex]);
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setImageDrawable(null);
            ((ViewPager) container).removeView(mViewCache[viewIndex]);
            if (!Application.DEFAULT_IS_OFFLINE) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).resetThumbStatus();
            }
            mViewCache[viewIndex].setTag(position);
            ((ViewPager) container).addView(mViewCache[viewIndex], 0);
            setObjectForPosition(mViewCache[viewIndex], position);
            if (!Application.DEFAULT_IS_OFFLINE) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumbStatus(mLevel,
                        mMaxPages - 1 - position, mMaxPages, "" + mLevel);
            }
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setAutoPlay(isAutoPlayOn());
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                Favorite cFavorite = FavoriteManager.getInstance().getFavoriteList().get(mMaxPages - 1 - position);
                mGirlLoader.loadImage(cFavorite.path, (CustomImageViewWithLoading) mViewCache[viewIndex]);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(true);
            } else {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(FavoriteManager.getInstance()
                        .isFavorite(mMaxPages - 1 - position, mLevel));
                mGirlLoader.loadImage(mMaxPages - 1 - position, ImageResourceUtils.TYPE_ORIGINAL,
                        (CustomImageViewWithLoading) mViewCache[viewIndex]);
            }
            mViewCache[viewIndex].setOnTouchListener(new OnSingleTouchListener());
            return mViewCache[viewIndex];
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    class OnSingleTouchListener implements OnTouchListener {
        @SuppressWarnings("deprecation")
        GestureDetector gestureDetector = new GestureDetector(new SimpleOnGestureListener() {
            public boolean onSingleTapConfirmed(MotionEvent e) {
                int halfWidth = getWidth() / 2;
                ViewPager c = GalleryDetailView.this;
                int pageToShow = c.getCurrentItem();
                if (e.getX() > halfWidth) {
                    // flip to show next page
                    pageToShow = c.getCurrentItem() + 1;
                } else {
                    // flip to show prev page
                    pageToShow = c.getCurrentItem() - 1;
                }
                pageToShow = Math.max(Math.min(c.getAdapter().getCount() - 1, pageToShow), 0);
                c.setCurrentItem(pageToShow, true);
                return true;
            };

            public boolean onDown(MotionEvent e) {
                return true;
            };
        });

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }

    public int getSelectedItem() {
        return mMaxPages - getCurrentItem();
    }

    public void onFavoriteChanged() {
        if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
            int size = FavoriteManager.getInstance().getFavoriteSize();
            mMaxPages = size;
            if (size < 0) {
                mActivity.finish();
                return;
            }
            int position = getCurrentItem();
            int next = -1;
            if (position == 0) {
                if (mMaxPages >= 1) {
                    next = 0;
                } else {
                    DialogToastUtils.showMessage(mActivity, mActivity.getString(R.string.favorite_null_msg));
                    mActivity.finish();
                    return;
                }
            } else if (mMaxPages == position) {
                next = 0;
            } else {
                next = position;
            }
            int index = mMaxPages - 1 - next;
            if (next == 0) {
                index = 0;
            }
            int viewIndex = index % MAX_VIEW_CACHE_COUNTS;

            if (mViewCache[viewIndex] != null) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).getContextView().setImageBitmap(null);
                if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                    Favorite cFavorite = FavoriteManager.getInstance().getFavoriteList().get(index);
                    mGirlLoader.loadImage(cFavorite.path, (CustomImageViewWithLoading) mViewCache[viewIndex]);
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(true);
                } else {
                    ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(FavoriteManager.getInstance()
                            .isFavorite(mMaxPages - 1 - index, mLevel));
                    mGirlLoader.loadImage(mMaxPages - 1 - index, ImageResourceUtils.TYPE_ORIGINAL,
                            (CustomImageViewWithLoading) mViewCache[viewIndex]);
                }
            }
            setCurrentItem(next);
            mPagerAdapter.notifyDataSetChanged();
        }
    }
}
