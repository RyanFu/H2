package com.prettygirl.superstar.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.capricorn.ArcMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.prettygirl.app.utils.Utils;
import com.prettygirl.superstar.R;

public class CustomImageViewWithLoading extends RelativeLayout implements OnClickListener {

    private int mBitmapWidth = 0;

    private int mBitmapHeight = 0;

    private int mClickedColor;

    private ImageView mContentView;

    private View mLoadingView;

    private OnClickListener onMagicRodClickListener;

    private OnClickListener onShareClickListener;

    private OnClickListener onWallpaperClickListener;

    private OnClickListener onSlideShowClickListener;

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
            int padding = (int) Utils.dip2px(context, 7);
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

    public void loadImage(String url) {
        ImageLoader.getInstance().displayImage(url, mContentView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                showLoading();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.OUT_OF_MEMORY) {
                    System.gc();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                setImageBitmap(loadedImage);
                hideLoading();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public void cancelLoadImage() {
        ImageLoader.getInstance().cancelDisplayTask(mContentView);
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
        }
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

    public void setMagicRodClickListener(OnClickListener onClickListener) {
        onMagicRodClickListener = onClickListener;
    }

}
