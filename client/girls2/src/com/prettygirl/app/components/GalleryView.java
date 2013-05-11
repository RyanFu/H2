package com.prettygirl.app.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.BaseActivity;
import com.prettygirl.app.GalleryActivity;
import com.prettygirl.app.GalleryDetailActivity;
import com.prettygirl.app.model.Favorite;
import com.prettygirl.app.model.provider.FavoriteManager;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.GirlLoader;
import com.prettygirl.app.utils.ImageResourceUtils;

public class GalleryView extends JazzyViewPager {

    private static final int PAGE_SIZE = 6;
    private static final int DEFUALT_VIEW_CACHE_COUNTS = 4;

    private View[] mViewCache = null;

    private int mTempleteCount;

    private String[] mLayoutTempletes = null;

    private MPagerAdapter mPagerAdapter;

    private Activity mActivity;
    private GirlLoader mGirlLoader;

    private OnPageChangeListener mListener;

    private int mLevel;

    private int mType;

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
    }

    public void init(int level, int cType, String baseUrl) {
        mType = cType;
        mGirlLoader = new GirlLoader(baseUrl, mType == BaseActivity.EXT_VALUE_OFFLINE);
        mLevel = level;
        setTransitionEffect(TransitionEffect.Standard);
        if (cType == BaseActivity.EXT_VALUE_FAVORITE) {
            int size = FavoriteManager.getInstance().getFavoriteSize();
            if (size < 0) {
                return;
            }
            mPagerAdapter = new MPagerAdapter(size);
        } else {
            mPagerAdapter = new MPagerAdapter(mGirlLoader.getCachedTotalImageCount(getContext(), level));
        }
        Resources cResources = getResources();
        mLayoutTempletes = cResources.getStringArray(R.array.e_gallery_templetes);
        mTempleteCount = Math.max(mLayoutTempletes.length, DEFUALT_VIEW_CACHE_COUNTS);
        mViewCache = new View[mTempleteCount];
        setAdapter(mPagerAdapter);
        super.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int viewIndex = position % mTempleteCount;
                for (int index = 0; index < mTempleteCount; index++) {
                    if (index == viewIndex) {
                        mPagerAdapter.load(position);
                    } else {
                        mPagerAdapter.cancel(index);
                    }
                }
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mListener != null) {
                    mListener.onPageScrolled(arg0, arg1, arg2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(arg0);
                }
            }
        });

        if (cType != BaseActivity.EXT_VALUE_FAVORITE) {
            mGirlLoader.asyncLoadServerImageCount(getContext(), mLevel, new Runnable() {

                @Override
                public void run() {
                    mPagerAdapter.updateTotalImageCount(mGirlLoader.getCachedTotalImageCount(getContext(), mLevel));
                    mPagerAdapter.load(getCurrentItem());
                }
            });
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    public void resume() {
        mPagerAdapter.resume(getCurrentItem());
    }

    public void setSelectedItem(int item) {
        this.setCurrentItem(mPagerAdapter.getPageIndex(item), true);
        mPagerAdapter.notifyDataSetChanged();
    }

    public void gotoPageByIndex(int pageIndex) {
        if (mPagerAdapter.mTotalPageCount > pageIndex) {
            this.setCurrentItem(mPagerAdapter.mTotalPageCount - pageIndex, true);
        } else {
            this.setCurrentItem(mPagerAdapter.mTotalPageCount - 1, true);
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    public void updateDataChanged() {
        int size = FavoriteManager.getInstance().getFavoriteSize();
        if (size < 0) {
            return;
        }
        mPagerAdapter.updateTotalImageCount(size);
    }

    class MPagerAdapter extends PagerAdapter {

        private int mTotalImageCount;

        private int mTotalPageCount;

        private boolean isFirst = true;

        MPagerAdapter(int imageCount) {
            mTotalImageCount = imageCount;
            mTotalPageCount = mTotalImageCount / PAGE_SIZE + (mTotalImageCount % PAGE_SIZE == 0 ? 0 : 1);
        }

        public void resume(int position) {
            int viewIndex = position % mTempleteCount;
            if (mViewCache[viewIndex] == null) {
                return;
            }
            mViewCache[viewIndex].forceLayout();
        }

        public void updateTotalImageCount(int total) {
            mTotalImageCount = total;
            mTotalPageCount = mTotalImageCount / PAGE_SIZE + (mTotalImageCount % PAGE_SIZE == 0 ? 0 : 1);
            notifyDataSetChanged();
        }

        public int getPageIndex(int imageIndex) {
            if (imageIndex > mTotalImageCount) {
                return 0;
            }
            imageIndex = mTotalImageCount - 1 - imageIndex;
            return imageIndex / PAGE_SIZE;// + (imageIndex % PAGE_SIZE == 0 ? 1 : 0);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mTotalPageCount;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //((ViewPager) container).removeView(mViewCache[position % mTempleteCount]);
        }

        // 初始化arg1位置的界面
        public Object instantiateItem(ViewGroup container, int position) {
            int viewIndex = position % mTempleteCount;
            Context mContext = container.getContext();
            if (mViewCache[viewIndex] == null) {
                Resources cResources = mContext.getResources();
                mViewCache[viewIndex] = View.inflate(mContext, cResources.getIdentifier(mLayoutTempletes[position
                        % mLayoutTempletes.length], "layout", mContext.getPackageName()), null);
                if (GirlLoader.IS_WIDTH_LE_480 == true) {
                    // fix admob not displaying problem.Some ad requires full screen with to display in 480w phone
                    mViewCache[viewIndex].setPadding(0, 0, 0, 0);
                }
            }
            ((ViewPager) container).removeView(mViewCache[viewIndex]);
            ViewHolder cViewHolder = null;
            Object obj = mViewCache[viewIndex].getTag();
            if (obj == null || !(obj instanceof ViewHolder)) {
                cViewHolder = new ViewHolder(mViewCache[viewIndex]);
            }
            if (isFirst && position == 0) {
                load(position);
                isFirst = false;
            }
            // TOOD: start index might be <0
            mViewCache[viewIndex].setTag(cViewHolder);
            setObjectForPosition(mViewCache[viewIndex], position);
            ((ViewPager) container).addView(mViewCache[viewIndex], 0);
            return mViewCache[viewIndex];
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void cancel(int viewIndex) {
            ViewHolder cViewHolder = null;
            if (mViewCache[viewIndex] == null) {
                return;
            }
            mViewCache[viewIndex].setVisibility(GONE);
            Object obj = mViewCache[viewIndex].getTag();
            if (obj == null || !(obj instanceof ViewHolder)) {
                cViewHolder = new ViewHolder(mViewCache[viewIndex]);
            } else {
                cViewHolder = (ViewHolder) obj;
            }
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView1);
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView2);
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView3);
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView4);
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView5);
            mGirlLoader.cancelLoadImage(cViewHolder.mImageView6);
            cViewHolder.mImageView1.setImageDrawable(null);
            cViewHolder.mImageView2.setImageDrawable(null);
            cViewHolder.mImageView3.setImageDrawable(null);
            cViewHolder.mImageView4.setImageDrawable(null);
            cViewHolder.mImageView5.setImageDrawable(null);
            cViewHolder.mImageView6.setImageDrawable(null);
        }

        private void load(int position) {
            int viewIndex = position % mTempleteCount;
            if (mViewCache[viewIndex] == null) {
                return;
            }
            mViewCache[viewIndex].setVisibility(VISIBLE);
            ViewHolder cViewHolder = null;
            Object obj = mViewCache[viewIndex].getTag();
            if (obj == null || !(obj instanceof ViewHolder)) {
                cViewHolder = new ViewHolder(mViewCache[viewIndex]);
            } else {
                cViewHolder = (ViewHolder) obj;
            }
            int startIndex = (mTotalImageCount - 1) - position * PAGE_SIZE;

            cViewHolder.mIndexText.setText(((mTotalPageCount - 1) - position) + "");
            cViewHolder.mImageView1.setTag(startIndex);
            Favorite cFavorite;
            FavoriteManager cFavoriteManager = FavoriteManager.getInstance();
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView1);
                } else {
                    cViewHolder.mImageView1.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView1);
            }
            startIndex--;

            cViewHolder.mImageView2.setTag(startIndex);
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView2);
                } else {
                    cViewHolder.mImageView2.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView2);
            }
            startIndex--;

            cViewHolder.mImageView3.setTag(startIndex);
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView3);
                } else {
                    cViewHolder.mImageView3.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView3);
            }
            startIndex--;

            cViewHolder.mImageView4.setTag(startIndex);
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView4);
                } else {
                    cViewHolder.mImageView4.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView4);
            }
            startIndex--;

            cViewHolder.mImageView5.setTag(startIndex);

            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView5);
                } else {
                    cViewHolder.mImageView5.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView5);
            }
            startIndex--;

            cViewHolder.mImageView6.setTag(startIndex);
            if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
                if (startIndex >= 0) {
                    cFavorite = cFavoriteManager.getFavoriteList().get(startIndex);
                    mGirlLoader.loadImage(cFavorite.path, cViewHolder.mImageView6);
                } else {
                    cViewHolder.mImageView6.setImageDrawable(null);
                }
            } else {
                mGirlLoader.loadImage(startIndex, ImageResourceUtils.TYPE_SNAPSHOT, cViewHolder.mImageView6);
            }

        }

        private OnClickListener onClickListener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                Object obj = view.getTag();
                if (obj == null || !(obj instanceof Integer)) {
                    return;
                }
                Context cContext = view.getContext();
                Intent intent = new Intent();
                intent.setClass(cContext, GalleryDetailActivity.class);
                intent.putExtra(GalleryActivity.EXT_LEVEL_KEY, mLevel);
                intent.putExtra(GalleryActivity.EXT_TYPE, mType);
                intent.putExtra(GalleryDetailActivity.EXT_INT_INDEX, (Integer) obj);
                intent.putExtra(GalleryDetailActivity.EXT_STRING_BASE_URL, mGirlLoader.getBaseUrl());
                mActivity.startActivityForResult(intent, 1);
            }
        };

        class ViewHolder {
            TextView mIndexText;
            CustomImageViewWithLoading mImageView1;
            CustomImageViewWithLoading mImageView2;
            CustomImageViewWithLoading mImageView3;
            CustomImageViewWithLoading mImageView4;
            CustomImageViewWithLoading mImageView5;
            CustomImageViewWithLoading mImageView6;

            ViewHolder(View view) {
                mIndexText = ((TextView) view.findViewById(R.id.pages_index));
                mIndexText.setVisibility(GONE);
                View container = view.findViewById(R.id.image_container1);
                mImageView1 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView1);
                mImageView1.setOnClickListener(onClickListener);
                mImageView2 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView2);
                mImageView2.setOnClickListener(onClickListener);
                mImageView3 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView3);
                mImageView3.setOnClickListener(onClickListener);
                container = view.findViewById(R.id.image_container2);
                mImageView4 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView1);
                mImageView4.setOnClickListener(onClickListener);
                mImageView5 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView2);
                mImageView5.setOnClickListener(onClickListener);
                mImageView6 = (CustomImageViewWithLoading) container.findViewById(R.id.imageView3);
                mImageView6.setOnClickListener(onClickListener);
            }
        }
    }

    public void onFavoriteChanged() {
        if (mType == BaseActivity.EXT_VALUE_FAVORITE) {
            int size = FavoriteManager.getInstance().getFavoriteSize();
            if (size < 0) {
                mActivity.finish();
                return;
            }
            mPagerAdapter.updateTotalImageCount(size);
            int position = getCurrentItem();
            if (position == 0) {
                if (mPagerAdapter.mTotalPageCount == 0) {
                    DialogToastUtils.showMessage(mActivity, mActivity.getString(R.string.favorite_null_msg));
                    mActivity.finish();
                    return;
                } else if (mPagerAdapter.mTotalPageCount == 1) {
                    int viewIndex = position % mTempleteCount;
                    for (int index = 0; index < mTempleteCount; index++) {
                        if (index == viewIndex) {
                            mPagerAdapter.load(position);
                        } else {
                            mPagerAdapter.cancel(index);
                        }
                    }
                }
            } else {
                setCurrentItem(position);
                int viewIndex = position % mTempleteCount;
                for (int index = 0; index < mTempleteCount; index++) {
                    if (index == viewIndex) {
                        mPagerAdapter.load(position);
                    } else {
                        mPagerAdapter.cancel(index);
                    }
                }
            }
        }
    }
}
