package com.prettygirl.app.components;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pretty.girl.app.R;
import com.prettygirl.app.Application;
import com.prettygirl.app.BaseActivity;
import com.prettygirl.app.GalleryActivity;
import com.prettygirl.app.GalleryDetailActivity;
import com.prettygirl.app.ImageEditorActivity;
import com.prettygirl.app.model.Favorite;
import com.prettygirl.app.model.ThumbUp;
import com.prettygirl.app.model.ThumbUpList;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class RankDetailView extends JazzyViewPager {

    private static final int MAX_VIEW_CACHE_COUNTS = 4;

    private View[] mViewCache = new View[MAX_VIEW_CACHE_COUNTS];

    private MPagerAdapter mPagerAdapter;

    private int mMaxPages;

    private Activity mActivity;

    private int mType;

    private ThumbUpList mThumbUpList;

    private String mMethod;

    public RankDetailView(Context context) {
        super(context);
    }

    public RankDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindActivity(Activity cActivity, String cMethod) {
        mActivity = cActivity;
        mMethod = cMethod;
    }

    public void init(ThumbUpList cThumbUpList) {
        mMaxPages = cThumbUpList.size();
        mThumbUpList = cThumbUpList;
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
    }

    class MPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mMaxPages;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //NG
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(final ViewGroup container, final int position) {
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
                            int index = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(index);
                            Context cContext = v.getContext();
                            Intent intent = new Intent();
                            intent.setClass(cContext, ImageEditorActivity.class);
                            intent.putExtra(GalleryActivity.EXT_LEVEL_KEY, thumbUp.level);
                            intent.putExtra(BaseActivity.EXT_TYPE, mType);
                            intent.putExtra(BaseActivity.EXT_METHOD, mMethod);
                            intent.putExtra(GalleryDetailActivity.EXT_INT_INDEX, thumbUp.id);
                            intent.putExtra(GalleryDetailActivity.EXT_STRING_BASE_URL,
                                    ImageResourceUtils.getServerUrl(cContext, thumbUp.level));
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
                            int index = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(index);
                            String fileName = Application.generatorFileName(ImageResourceUtils.getImageUrlByIndex(
                                    ImageResourceUtils.getServerUrl(v.getContext(), thumbUp.level), thumbUp.id,
                                    ImageResourceUtils.TYPE_ORIGINAL, false));
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
                            int index = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(index);
                            String fileName = Application.generatorFileName(ImageResourceUtils.getImageUrlByIndex(
                                    ImageResourceUtils.getServerUrl(v.getContext(), thumbUp.level), thumbUp.id,
                                    ImageResourceUtils.TYPE_ORIGINAL, false));
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
                            int index = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(index);
                            ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(thumbUp.level, thumbUp.id,
                                    ThumbUpManager.THUMB_DOWN, mMaxPages, mMethod);
                        }
                    }

                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setThumbUpClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.PIC_THUMB_UP);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            int index = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(index);
                            ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(thumbUp.level, thumbUp.id,
                                    ThumbUpManager.THUMB_UP, mMaxPages, mMethod);
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
                            int pos = ((Integer) obj);
                            ThumbUp thumbUp = mThumbUpList.get(pos);
                            int pIndex = pos % MAX_VIEW_CACHE_COUNTS;
                            FavoriteManager mFavoriteManager = FavoriteManager.getInstance();
                            boolean isFavorited = false;
                            isFavorited = mFavoriteManager.isFavorite(thumbUp.id, thumbUp.level);
                            ((CustomImageViewWithLoading) mViewCache[pIndex]).setFavorited(!isFavorited);
                            if (isFavorited) {
                                mFavoriteManager.removeFavorite(new Favorite(thumbUp.id, thumbUp.level,
                                        ImageResourceUtils.getImageUrlByIndex(
                                                ImageResourceUtils.getServerUrl(v.getContext(), thumbUp.level),
                                                thumbUp.id, ImageResourceUtils.TYPE_ORIGINAL, false)));
                            } else {
                                mFavoriteManager.addFavorite(new Favorite(thumbUp.id, thumbUp.level, ImageResourceUtils
                                        .getImageUrlByIndex(
                                                ImageResourceUtils.getServerUrl(v.getContext(), thumbUp.level),
                                                thumbUp.id, ImageResourceUtils.TYPE_ORIGINAL, false)));
                                // 赞一个
                                ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumb(thumbUp.level,
                                        thumbUp.id, ThumbUpManager.THUMB_UP, mMaxPages, mMethod);
                            }
                        }
                    }

                });
            }
            cancelLoadImage((CustomImageViewWithLoading) mViewCache[viewIndex]);
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setImageDrawable(null);
            ((ViewPager) container).removeView(mViewCache[viewIndex]);
            if (!Application.DEFAULT_IS_OFFLINE) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).resetThumbStatus();
            }
            mViewCache[viewIndex].setTag(position);
            ((ViewPager) container).addView(mViewCache[viewIndex], 0);
            setObjectForPosition(mViewCache[viewIndex], position);
            ThumbUp thumbUp = mThumbUpList.get(position);
            if (!Application.DEFAULT_IS_OFFLINE) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).updateThumbStatus(thumbUp.level, thumbUp.id,
                        mMaxPages, mMethod);
            }
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setIndex(position + 1);
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setAutoPlay(isAutoPlayOn());
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(FavoriteManager.getInstance().isFavorite(
                    thumbUp.id, thumbUp.level));
            loadImage(thumbUp.id, thumbUp.level, ImageResourceUtils.TYPE_ORIGINAL,
                    (CustomImageViewWithLoading) mViewCache[viewIndex]);
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
                ViewPager c = RankDetailView.this;
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
        return getCurrentItem();
    }

    public void loadImage(final int index, int level, final int type, final CustomImageViewWithLoading image) {
        String url = ImageResourceUtils.getImageUrlByIndex(ImageResourceUtils.getServerUrl(image.getContext(), level),
                index, type, false);
        ImageLoader.getInstance().displayImage(url, image.getContextView(), new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                image.showLoading();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.OUT_OF_MEMORY) {
                    System.gc();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                image.setImageBitmap(loadedImage);
                image.hideLoading();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void cancelLoadImage(CustomImageViewWithLoading image) {
        ImageLoader.getInstance().cancelDisplayTask(image.getContextView());
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
            ThumbUp thumbUp = mThumbUpList.get(index);
            if (mViewCache[viewIndex] != null) {
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).getContextView().setImageBitmap(null);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setFavorited(FavoriteManager.getInstance()
                        .isFavorite(thumbUp.id, thumbUp.level));
                loadImage(thumbUp.id, thumbUp.level, ImageResourceUtils.TYPE_ORIGINAL,
                        (CustomImageViewWithLoading) mViewCache[viewIndex]);
            }
            setCurrentItem(next);
            mPagerAdapter.notifyDataSetChanged();
        }
    }
}
