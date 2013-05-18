package com.prettygirl.superstar.components;

import java.io.File;
import java.util.ArrayList;

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

import com.prettygirl.app.utils.AdUtils;
import com.prettygirl.superstar.ImageEditorActivity;
import com.prettygirl.superstar.R;
import com.prettygirl.superstar.SuperStarApplication;
import com.prettygirl.superstar.util.StorageUtils;
import com.prettygirl.superstar.util.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class GalleryDetailView extends JazzyViewPager {

    private static final int MAX_VIEW_CACHE_COUNTS = 4;

    private View[] mViewCache = new View[MAX_VIEW_CACHE_COUNTS];

    private MPagerAdapter mPagerAdapter;

    private int mMaxPages;

    private Activity mActivity;

    private ArrayList<String> mUrls;

    public GalleryDetailView(Context context) {
        super(context);
    }

    public GalleryDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindActivity(Activity cActivity) {
        mActivity = cActivity;
    }

    public void init(ArrayList<String> imgList, int index) {
        mUrls = imgList;
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
        this.setCurrentItem(index);
    }

    class MPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(final ViewGroup container, final int position) {
            final int viewIndex = position % MAX_VIEW_CACHE_COUNTS;
            Context mContext = container.getContext();
            if (mViewCache[viewIndex] == null) {
                mViewCache[viewIndex] = new CustomImageViewWithLoading(mContext);
                mViewCache[viewIndex].setClickable(false);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).showMagicRod();
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).showMagicEditor(View.VISIBLE);
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setMagicRodClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            int index = (Integer) obj;
                            Context cContext = v.getContext();
                            Intent intent = new Intent();
                            intent.setClass(cContext, ImageEditorActivity.class);
                            intent.putExtra(ImageEditorActivity.EXT_STRING_BASE_URL, mUrls.get(index));
                            mActivity.startActivityForResult(intent, 1);
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setShareClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            int index = (Integer) obj;
                            String url = mUrls.get(index);
                            File imageFile = new File(StorageUtils.getImageCacheDir(), SuperStarApplication
                                    .getCacheImageUrl(url));
                            //                            if (!StorageUtils.isSdCardAvailable() || !imageFile.exists()) {
                            //                                imageFile = DefaultConfigurationFactory.createReserveDiscCache(getContext()).get(url);
                            //                            }
                            AdUtils.sharePic(mActivity, imageFile, getContext().getString(R.string.share_title));
                        }
                    }
                });
                ((CustomImageViewWithLoading) mViewCache[viewIndex]).setWallpaperClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getContext(), UMengKey.GALLERY_DETAIL_VIEW_WALLPAPER);
                        Object obj = v.getTag();
                        if (obj != null && obj instanceof Integer) {
                            int index = (Integer) obj;
                            String url = mUrls.get(index);
                            File imageFile = new File(StorageUtils.getImageCacheDir(), SuperStarApplication
                                    .getCacheImageUrl(url));
                            //                            if (!StorageUtils.isSdCardAvailable() || !imageFile.exists()) {
                            //                                imageFile = DefaultConfigurationFactory.createReserveDiscCache(getContext()).get(url);
                            //                            }
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
            }
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).cancelLoadImage();
            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setImageDrawable(null);
            ((ViewPager) container).removeView(mViewCache[viewIndex]);
            mViewCache[viewIndex].setTag(position);
            ((ViewPager) container).addView(mViewCache[viewIndex], 0);
            setObjectForPosition(mViewCache[viewIndex], position);

            ((CustomImageViewWithLoading) mViewCache[viewIndex]).loadImage(mUrls.get(position));

            ((CustomImageViewWithLoading) mViewCache[viewIndex]).setAutoPlay(isAutoPlayOn());
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
}
