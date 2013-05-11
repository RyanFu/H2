package com.prettygirl.app.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capricorn.ArcMenu;
import com.pretty.girl.app.R;
import com.prettygirl.app.model.ThumbUp;
import com.prettygirl.app.model.provider.ThumbUpManager;
import com.prettygirl.app.model.provider.ThumbUpManager.ThumbUpChangedListener;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class CustomImageViewWithLoading extends RelativeLayout implements OnClickListener {

    private int mBitmapWidth = 0;

    private int mBitmapHeight = 0;

    private int mClickedColor;

    private ImageView mContentView;

    private View mLoadingView;

    private OnClickListener onMagicRodClickListener;

    private OnClickListener onMagicAdClickListener;

    private OnClickListener onFavoriteClickListener;

    private OnClickListener onShareClickListener;

    private OnClickListener onWallpaperClickListener;

    private OnClickListener onSlideShowClickListener;

    private OnClickListener onThumbUpClickListener;

    private OnClickListener onThumbDownClickListener;

    public CustomImageViewWithLoading(Context context) {
        super(context);
        init(context);
    }

    public CustomImageViewWithLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomImageViewWithLoading(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mClickedColor = context.getResources().getColor(R.color.custom_click_mask_color);
        View.inflate(context, R.layout.e_custom_image_view_with_loading, this);
        mContentView = (ImageView) findViewById(R.id.imageContent);
        findViewById(R.id.imageEditor).setOnClickListener(this);
        findViewById(R.id.thumb_up_coins).setOnClickListener(this);
        findViewById(R.id.thumb_down_coins).setOnClickListener(this);
        mLoadingView = findViewById(R.id.loadingProgressBar);

        ArcMenu mArcMenu = (ArcMenu) findViewById(R.id.image_arc_menu);
        Resources resource = context.getResources();
        final String items[] = resource.getStringArray(R.array.image_arc_menu_items);

        final int itemCount = items.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(context);
            if (i == 2) {
                item.setImageResource(resource.getIdentifier("play_arc", "drawable", context.getPackageName()));
            } else {
                item.setImageResource(resource.getIdentifier(items[i], "drawable", context.getPackageName()));
            }
            item.setAdjustViewBounds(true);
            int padding = (int) ImageResourceUtils.dip2px(context, 7);
            item.setPadding(padding, padding, padding, padding);
            item.setScaleType(ScaleType.FIT_CENTER);
            item.setBackgroundResource(R.drawable.arc_menu_icon_bg);
            item.setTag(i);
            mArcMenu.addItem(item, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if (obj != null && obj instanceof Integer && v instanceof ImageView) {
                        int index = ((Integer) obj).intValue();
                        switch (index) {
                        // case 0:
                        // if (onMagicRodClickListener != null) {
                        // onMagicRodClickListener.onClick(CustomImageViewWithLoading.this);
                        // }
                        // break;
                        case 0: // 设置为壁纸
                            if (onWallpaperClickListener != null) {
                                onWallpaperClickListener.onClick(CustomImageViewWithLoading.this);
                            }
                            break;
                        case 1: // 分享
                            if (onShareClickListener != null) {
                                onShareClickListener.onClick(CustomImageViewWithLoading.this);
                            }
                            break;
                        default: // 幻灯片
                            if (onSlideShowClickListener != null) {
                                onSlideShowClickListener.onClick(CustomImageViewWithLoading.this);
                            }
                            break;
                        }
                        // DialogToastUtils.showMessage(getContext(),
                        // items[index]);
                    }
                }
            });// Add a menu item
        }

        findViewById(R.id.imageFavorite).setOnClickListener(this);
        findViewById(R.id.imageAd).setOnClickListener(this);
        this.setClickable(true);
    }

    public void resetState() {
        ArcMenu mArcMenu = (ArcMenu) findViewById(R.id.image_arc_menu);
        mArcMenu.cancelAllStatus();
    }

    public void showMagicRod() {
        findViewById(R.id.image_arc_menu).setVisibility(VISIBLE);
    }

    public void showMagicEditor(int visible) {
        findViewById(R.id.imageEditor).setVisibility(visible);
    }

    public void setFavoriteVisibility(int visible) {
        findViewById(R.id.imageFavorite).setVisibility(visible);
    }

    public void setAutoPlay(boolean isAutoPlay) {
        View view = findViewById(R.id.image_arc_menu).findViewWithTag(2);
        if (view == null || !(view instanceof ImageView)) {
            return;
        }
        if (isAutoPlay) {
            ((ImageView) view).setImageResource(R.drawable.pause_arc);
        } else {
            ((ImageView) view).setImageResource(R.drawable.play_arc);
        }
    }

    public void setFavorited(boolean isFavorite) {
        if (isFavorite) {
            ((ImageView) findViewById(R.id.imageFavorite)).setImageResource(R.drawable.e_favorite_checked);
        } else {
            ((ImageView) findViewById(R.id.imageFavorite)).setImageResource(R.drawable.e_favorite_normal);
        }
    }

    public void setIndex(int pos) {
        findViewById(R.id.rank_index).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.rank_index)).setText(ThumbUpManager.formatText(pos + "th", "" + pos));
    }

    public void updateThumb(final int level, final int picId, int type, final int maxPicIndex, final String method) {
        String value = MobclickAgent.getConfigParams(getContext(),
                UMengKey.ONLINE_CONFIG_KEY_GALLERY_DETAIL_VIEW_RANK_SHOWABLE);
        if (!TextUtils.isEmpty(value) && !Boolean.valueOf(value)) {
            return;
        }
        ThumbUpManager cThumbUpManager = ThumbUpManager.getInstance();
        ThumbUpChangedListener changedListener = new ThumbUpChangedListener() {

            @Override
            public void onChanged(int status, ThumbUp cThumbUp) {
                updateThumbStatus(level, picId, false, maxPicIndex, method);
            }

        };
        if (!cThumbUpManager.hadThumbUp(picId, level, type)) {
            cThumbUpManager.updateThumbUpCount(((TextView) findViewById(R.id.thumb_up_coins)),
                    ((TextView) findViewById(R.id.thumb_down_coins)), level, method, picId, type, changedListener);
        }
    }

    public void updateThumbStatus(int level, int picId, int maxPicIndex, String method) {
        updateThumbStatus(level, picId, true, maxPicIndex, method);
    }

    public void resetThumbStatus() {
        String value = MobclickAgent.getConfigParams(getContext(),
                UMengKey.ONLINE_CONFIG_KEY_GALLERY_DETAIL_VIEW_RANK_SHOWABLE);
        if (!TextUtils.isEmpty(value) && !Boolean.valueOf(value)) {
            return;
        }
        ((TextView) findViewById(R.id.thumb_up_coins)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.thumb_up_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.thumb_up_normal, 0);
        ((TextView) findViewById(R.id.thumb_down_coins)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.thumb_down_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.thumb_down_normal, 0);
    }

    public void updateThumbStatus(int level, int picId, boolean request, int maxPicIndex, String method) {
        String value = MobclickAgent.getConfigParams(getContext(),
                UMengKey.ONLINE_CONFIG_KEY_GALLERY_DETAIL_VIEW_RANK_SHOWABLE);
        if (!TextUtils.isEmpty(value) && !Boolean.valueOf(value)) {
            return;
        }
        ThumbUpManager cThumbUpManager = ThumbUpManager.getInstance();
        if (cThumbUpManager.hadThumbUp(picId, level, ThumbUpManager.THUMB_UP)) {
            ((TextView) findViewById(R.id.thumb_up_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.thumb_up_pressed, 0);
        } else {
            ((TextView) findViewById(R.id.thumb_up_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.thumb_up_normal, 0);
        }
        if (cThumbUpManager.hadThumbUp(picId, level, ThumbUpManager.THUMB_DOWN)) {
            ((TextView) findViewById(R.id.thumb_down_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.thumb_down_pressed, 0);
        } else {
            ((TextView) findViewById(R.id.thumb_down_coins)).setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.thumb_down_normal, 0);
        }
        if (request) {
            cThumbUpManager.loadThumbUpCount(((TextView) findViewById(R.id.thumb_up_coins)),
                    ((TextView) findViewById(R.id.thumb_down_coins)), picId, method, false, maxPicIndex);
        }
    }

    public void setMagicAdVisibility(int visible) {
        findViewById(R.id.imageAd).setVisibility(visible);
    }

    public void showLoading() {
        mLoadingView.setVisibility(VISIBLE);
    }

    public void hideLoading() {
        mLoadingView.setVisibility(GONE);
    }

    public ImageView getContextView() {
        return mContentView;
    }

    public void setImageBitmap(Bitmap bm) {
        mLoadingView.setVisibility(GONE);
        mContentView.setImageBitmap(bm);
        if (bm != null) {
            mBitmapWidth = bm.getWidth();
            mBitmapHeight = bm.getHeight();
            applyCustomMatrix(mBitmapWidth, mBitmapHeight);
        } else {
            mBitmapWidth = 0;
            mBitmapHeight = 0;
        }
    }

    private void applyCustomMatrix(int imageWidth, int imageHeight) {
        // Portrait images
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (viewWidth <= 0 || viewHeight <= 0) {
            return;
        }

        if (viewWidth * imageHeight < viewHeight * imageWidth) {
            // 纵向无需拉伸
            mContentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return;
        }

        float scaleFactor = (float) (viewWidth) / (float) imageWidth;

        float dy, dx = 0;
        if (viewWidth / viewHeight >= 2) {
            if (imageHeight / imageWidth >= 2) {
                dy = (viewHeight - imageHeight * scaleFactor) * 0.4f;
            } else if (imageHeight / imageWidth >= 1.5f) {
                dy = (viewHeight - imageHeight * scaleFactor) * 0.3f;
            } else if (imageHeight / imageWidth >= 1f) {
                dy = (viewHeight - imageHeight * scaleFactor) * 0.25f;
            } else {
                dy = (viewHeight - imageHeight * scaleFactor) * 0.2f;
            }
        } else {
            // start drawing the image from 10% of the top
            dy = (viewHeight - imageHeight * scaleFactor) * 0.15f;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaleFactor, scaleFactor);
        matrix.postTranslate(dx, (int) (dy + 0.5f));

        mContentView.setScaleType(ImageView.ScaleType.MATRIX);
        mContentView.setImageMatrix(matrix);
    }

    public ScaleType getScaleType() {
        return mContentView.getScaleType();
    }

    public void setScaleType(ScaleType scaleType) {
        mContentView.setScaleType(scaleType);
    }

    public Matrix getImageMatrix() {
        return mContentView.getImageMatrix();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        invalidate();
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w > 0 && h > 0 && (w != oldw || h != oldh) && mBitmapWidth > 0 && mBitmapHeight > 0) {
            applyCustomMatrix(mBitmapWidth, mBitmapHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.isClickable() && (this.isPressed() || this.isFocused())) {
            canvas.drawColor(mClickedColor);
        }
    }

    public void setImageResource(int resId) {
        mLoadingView.setVisibility(GONE);
        mContentView.setImageResource(resId);
    }

    public void setImageDrawable(Drawable drawable) {
        mLoadingView.setVisibility(GONE);
        mContentView.setImageDrawable(drawable);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageEditor && onMagicRodClickListener != null) {
            onMagicRodClickListener.onClick(this);
        } else if (id == R.id.imageFavorite && onFavoriteClickListener != null) {
            onFavoriteClickListener.onClick(this);
        } else if (id == R.id.imageAd && onMagicAdClickListener != null) {
            onMagicAdClickListener.onClick(this);
        } else if (id == R.id.thumb_up_coins && onThumbUpClickListener != null) {
            onThumbUpClickListener.onClick(this);
        } else if (id == R.id.thumb_down_coins && onThumbDownClickListener != null) {
            onThumbDownClickListener.onClick(this);
        }
    }

    public void setThumbUpClickListener(OnClickListener onClickListener) {
        onThumbUpClickListener = onClickListener;
    }

    public void setThumbDownClickListener(OnClickListener onClickListener) {
        onThumbDownClickListener = onClickListener;
    }

    public void setWallpaperClickListener(OnClickListener onClickListener) {
        onWallpaperClickListener = onClickListener;
    }

    public void setShareClickListener(OnClickListener onClickListener) {
        onShareClickListener = onClickListener;
    }

    public void setSlideShowClickListener(OnClickListener onClickListener) {
        onSlideShowClickListener = onClickListener;
    }

    public void setFavoriteClickListener(OnClickListener onClickListener) {
        onFavoriteClickListener = onClickListener;
    }

    public void setMagicAdClickListener(OnClickListener onClickListener) {
        onMagicAdClickListener = onClickListener;
    }

    public void setMagicRodClickListener(OnClickListener onClickListener) {
        onMagicRodClickListener = onClickListener;
    }

}
