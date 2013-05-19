package com.prettygirl.app.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.pretty.lib.R;

public class ImageViewWithLoading extends RelativeLayout {

	private int mBitmapWidth = 0;

	private int mBitmapHeight = 0;

	private int mClickedColor;

	private ImageView mContentView;

	private View mLoadingView;

	private View mLoadFailedView;

	public ImageViewWithLoading(Context context) {
		super(context);
		init(context);
	}

	public ImageViewWithLoading(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImageViewWithLoading(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mClickedColor = context.getResources().getColor(
				R.color.custom_click_mask_color);
		View.inflate(context, R.layout.image_view_with_loading, this);
		mContentView = (ImageView) findViewById(R.id.imageContent);
		mLoadingView = findViewById(R.id.loadingProgressBar);
		mLoadFailedView = findViewById(R.id.loadFailed);
	}

	public void showLoading() {
		mLoadingView.setVisibility(VISIBLE);
	}

	public void hideLoading() {
		mLoadingView.setVisibility(GONE);
	}

	public void showFailed() {
		mLoadingView.setVisibility(GONE);
		mLoadFailedView.setVisibility(VISIBLE);
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

		float dy;
		if (imageHeight / imageWidth >= 2) {
			dy = 0.0f;
		} else {
			// start drawing the image from 10% of the top
			dy = (viewHeight - imageHeight * scaleFactor) * 0.1f;
		}

		Matrix matrix = new Matrix();
		matrix.setScale(scaleFactor, scaleFactor);
		matrix.postTranslate(0, (int) (dy + 0.5f));

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
		if (w > 0 && h > 0 && (w != oldw || h != oldh) && mBitmapWidth > 0
				&& mBitmapHeight > 0) {
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

}
