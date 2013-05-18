package com.prettygirl.superstar.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

import com.prettygirl.app.imagezoom.ImageViewTouchBase;
import com.prettygirl.app.utils.Utils;

public class ZoomableImageView extends ImageViewTouchBase {

    private static final float RADIUS = 12f;

    private static final float INVALID_POINT = Float.NEGATIVE_INFINITY;

    private static final boolean INNER_RECT = false;

    private static final float SCROLL_DELTA_THRESHOLD = 1.0f;
    static final float MIN_ZOOM = 0.9f;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected float mCurrentScaleFactor;
    protected float mScaleFactor;
    protected int mDoubleTapDirection;
    protected OnGestureListener mGestureListener;
    protected OnScaleGestureListener mScaleListener;
    protected boolean mDoubleTapToZoomEnabled = true;
    protected boolean mScaleEnabled = true;
    protected boolean mScrollEnabled = true;

    private RectF mMarginRectF = new RectF();

    private Paint mMarginPaint = null;

    private PointF mDragPoint = new PointF(INVALID_POINT, INVALID_POINT);

    private Paint mDragHandlerPaint = null;

    private static float mRadius = -1;

    private float[] mBitmapRectPoints;

    private OnImageViewTouchDoubleTapListener doubleTapListener;

    public ZoomableImageView(Context context) {
        super(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleListener = getScaleListener();

        if (mScaleListener != null) {
            mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        }
        try {
            mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);
        } catch (Throwable e) {
        }
        mCurrentScaleFactor = 1f;
        mDoubleTapDirection = 1;
        mRadius = Utils.dip2px(getContext(), RADIUS);
    }

    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener doubleTapListener) {
        this.doubleTapListener = doubleTapListener;
    }

    public void setDoubleTapToZoomEnabled(boolean value) {
        mDoubleTapToZoomEnabled = value;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapToZoomEnabled;
    }

    protected OnGestureListener getGestureListener() {
        return new GestureListener();
    }

    protected OnScaleGestureListener getScaleListener() {
        try {
            return new ScaleListener();
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    protected void onBitmapChanged(Drawable drawable) {
        super.onBitmapChanged(drawable);

        float v[] = new float[9];
        mSuppMatrix.getValues(v);
        mCurrentScaleFactor = v[Matrix.MSCALE_X];
    }

    @Override
    protected void _setImageDrawable(final Drawable drawable, final boolean reset, final Matrix initial_matrix,
            final float maxZoom) {
        super._setImageDrawable(drawable, reset, initial_matrix, maxZoom);
        mScaleFactor = getMaxZoom() / 3;
    }

    private boolean circleContain(float x, float y, float cx, float cy, float radiusx, float radiusy) {
        if ((Math.abs(x - cx) <= radiusx) && (Math.abs(y - cy) <= radiusy)) {
            return true;
        } else {
            return false;
        }
    }

    private float computeRadius(float x1, float y1, float x2, float y2, float crossx, float crossy) {
        float k1 = (crossy - y1) / (crossx - x1);
        float k2 = (crossy - y2) / (crossx - x2);
        float radius = (float) Math.atan(0 - ((k2 - k1) / (1 + k1 * k2)));
        return radius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScaleDetector != null) {
            mScaleDetector.onTouchEvent(event);
            if (!mScaleDetector.isInProgress()) {
                if (mGestureDetector != null) {
                    mGestureDetector.onTouchEvent(event);
                }
            }
        }
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            if (INNER_RECT) {
                if (circleContain(x, y, mBitmapRectPoints[0], mBitmapRectPoints[1], mRadius, mRadius)) {
                    mDragPoint.set(mBitmapRectPoints[0], mBitmapRectPoints[1]);
                } else if (circleContain(x, y, mBitmapRectPoints[2], mBitmapRectPoints[3], mRadius, mRadius)) {
                    mDragPoint.set(mBitmapRectPoints[2], mBitmapRectPoints[3]);
                } else if (circleContain(x, y, mBitmapRectPoints[4], mBitmapRectPoints[5], mRadius, mRadius)) {
                    mDragPoint.set(mBitmapRectPoints[4], mBitmapRectPoints[5]);
                } else if (circleContain(x, y, mBitmapRectPoints[6], mBitmapRectPoints[7], mRadius, mRadius)) {
                    mDragPoint.set(mBitmapRectPoints[6], mBitmapRectPoints[7]);
                } else {
                    break;
                }
            } else {
                if (circleContain(x, y, mMarginRectF.left, mMarginRectF.top, mRadius, mRadius)) {
                    mDragPoint.set(mMarginRectF.left, mMarginRectF.top);
                } else if (circleContain(x, y, mMarginRectF.right, mMarginRectF.top, mRadius, mRadius)) {
                    mDragPoint.set(mMarginRectF.right, mMarginRectF.top);
                } else if (circleContain(x, y, mMarginRectF.left, mMarginRectF.bottom, mRadius, mRadius)) {
                    mDragPoint.set(mMarginRectF.left, mMarginRectF.bottom);
                } else if (circleContain(x, y, mMarginRectF.right, mMarginRectF.bottom, mRadius, mRadius)) {
                    mDragPoint.set(mMarginRectF.right, mMarginRectF.bottom);
                } else {
                    break;
                }
            }
            postInvalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            if (mDragPoint.x == INVALID_POINT && mDragPoint.y == INVALID_POINT) {
                break;
            }
            float scale = Float.NEGATIVE_INFINITY;
            float newSpanX = 0;
            float newSpanY = 0;
            float oldSpanX = 0;
            float oldSpanY = 0;
            float scaleX = 0;
            float scaleY = 0;
            if (INNER_RECT) {
                if (circleContain(mDragPoint.x, mDragPoint.y, mMarginRectF.left, mMarginRectF.top,
                        mMarginRectF.centerX(), mMarginRectF.centerY())) {
                    newSpanX = mMarginRectF.centerX() - x;
                    newSpanY = mMarginRectF.centerY() - y;
                    oldSpanX = mMarginRectF.centerX() - mDragPoint.x;
                    oldSpanY = mMarginRectF.centerY() - mDragPoint.y;
                } else if (circleContain(mDragPoint.x, mDragPoint.y, mMarginRectF.left, mMarginRectF.bottom,
                        mMarginRectF.centerX(), mMarginRectF.centerY())) {
                    newSpanX = mMarginRectF.centerX() - x;
                    newSpanY = y - mMarginRectF.centerY();
                    oldSpanX = mMarginRectF.centerX() - mDragPoint.x;
                    oldSpanY = mDragPoint.y - mMarginRectF.centerY();
                } else if (circleContain(mDragPoint.x, mDragPoint.y, mMarginRectF.right, mMarginRectF.top,
                        mMarginRectF.centerX(), mMarginRectF.centerY())) {
                    newSpanX = x - mMarginRectF.centerX();
                    newSpanY = mMarginRectF.centerY() - y;
                    oldSpanX = mDragPoint.x - mMarginRectF.centerX();
                    oldSpanY = mMarginRectF.centerY() - mDragPoint.y;
                } else {
                    newSpanX = x - mMarginRectF.centerX();
                    newSpanY = y - mMarginRectF.centerY();
                    oldSpanX = mDragPoint.x - mMarginRectF.centerX();
                    oldSpanY = mDragPoint.y - mMarginRectF.centerY();
                }
            } else {
                if (mDragPoint.x == mMarginRectF.left) {
                    if (mDragPoint.y == mMarginRectF.top) {
                        newSpanX = mMarginRectF.centerX() - x;
                        newSpanY = mMarginRectF.centerY() - y;
                        oldSpanX = mMarginRectF.centerX() - mMarginRectF.left;
                        oldSpanY = mMarginRectF.centerY() - mMarginRectF.top;
                    } else {
                        newSpanX = mMarginRectF.centerX() - x;
                        newSpanY = y - mMarginRectF.centerY();
                        oldSpanX = mMarginRectF.centerX() - mMarginRectF.left;
                        oldSpanY = mMarginRectF.bottom - mMarginRectF.centerY();
                    }
                } else {
                    if (mDragPoint.y == mMarginRectF.top) {
                        newSpanX = x - mMarginRectF.centerX();
                        newSpanY = mMarginRectF.centerY() - y;
                        oldSpanX = mMarginRectF.right - mMarginRectF.centerX();
                        oldSpanY = mMarginRectF.centerY() - mMarginRectF.top;
                    } else {
                        newSpanX = x - mMarginRectF.centerX();
                        newSpanY = y - mMarginRectF.centerY();
                        oldSpanX = mMarginRectF.right - mMarginRectF.centerX();
                        oldSpanY = mMarginRectF.bottom - mMarginRectF.centerY();
                    }
                }
            }
            scaleX = newSpanX / oldSpanX;
            scaleY = newSpanY / oldSpanY;
            scale = Math.max(Math.abs(scaleX), Math.abs(scaleY));
            if (INNER_RECT) {
                float radius = computeRadius(x, y, mDragPoint.x, mDragPoint.y, mMarginRectF.centerX(),
                        mMarginRectF.centerY());
                postScaleWithRadius(scale, radius, mMarginRectF.centerX(), mMarginRectF.centerY());
            } else {
                postScale(scale, mMarginRectF.centerX(), mMarginRectF.centerY());
            }
            break;
        case MotionEvent.ACTION_UP:
            if (getScale() < 1f) {
                zoomTo(1f, 50);
            }
        default:
            mDragPoint.set(INVALID_POINT, INVALID_POINT);
            postInvalidate();
            break;
        }
        return true;
    }

    public void postZoomBy(float scale) {
        postScale(scale, mMarginRectF.centerX(), mMarginRectF.centerY());
    }

    public void postZoomBy(float scale, float x, float y) {
        postScale(scale, x, y);
    }

    @Override
    protected void onZoom(float scale) {
        super.onZoom(scale);
        if (mScaleDetector != null) {
            if (!mScaleDetector.isInProgress())
                mCurrentScaleFactor = scale;
        }
    }

    private void measureRect() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (mMarginPaint == null) {
            mMarginPaint = new Paint();
            mMarginPaint.setColor(0xff00ffff);
            mMarginPaint.setStrokeWidth(Utils.dip2px(getContext(), 5));
            mMarginPaint.setStyle(Paint.Style.STROKE);
        }
        if (mDragHandlerPaint == null) {
            mDragHandlerPaint = new Paint();
            mDragHandlerPaint.setColor(0xff00ffff);
            mDragHandlerPaint.setStrokeWidth(Utils.dip2px(getContext(), 5));
            mDragHandlerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        mMarginRectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mBitmapRectPoints = updateRectPoints(mMarginRectF);
        mMarginRectF.left = Math.min(Math.min(mBitmapRectPoints[0], mBitmapRectPoints[2]),
                Math.min(mBitmapRectPoints[4], mBitmapRectPoints[6]));
        mMarginRectF.top = Math.min(Math.min(mBitmapRectPoints[1], mBitmapRectPoints[3]),
                Math.min(mBitmapRectPoints[5], mBitmapRectPoints[7]));
        mMarginRectF.right = Math.max(Math.max(mBitmapRectPoints[0], mBitmapRectPoints[2]),
                Math.max(mBitmapRectPoints[4], mBitmapRectPoints[6]));
        mMarginRectF.bottom = Math.max(Math.max(mBitmapRectPoints[1], mBitmapRectPoints[3]),
                Math.max(mBitmapRectPoints[5], mBitmapRectPoints[7]));
    }

    private float[] updateRectPoints(RectF rectf) {
        if (mBitmapRectPoints == null) {
            mBitmapRectPoints = new float[] { rectf.left, rectf.top, rectf.left, rectf.bottom, rectf.right, rectf.top,
                    rectf.right, rectf.bottom };
        } else {
            mBitmapRectPoints[0] = rectf.left; // 左上
            mBitmapRectPoints[1] = rectf.top;
            mBitmapRectPoints[2] = rectf.left; // 左下
            mBitmapRectPoints[3] = rectf.bottom;
            mBitmapRectPoints[4] = rectf.right; // 右上
            mBitmapRectPoints[5] = rectf.top;
            mBitmapRectPoints[6] = rectf.right; // 右下
            mBitmapRectPoints[7] = rectf.bottom;
        }
        getImageViewMatrix().mapPoints(mBitmapRectPoints);
        return mBitmapRectPoints;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measureRect();

        if (mDragHandlerPaint == null) {
            return;
        }
        if (mMarginPaint == null) {
            return;
        }
        if (INNER_RECT) {
            canvas.save();
            canvas.drawLine(mBitmapRectPoints[0], mBitmapRectPoints[1], mBitmapRectPoints[2], mBitmapRectPoints[3],
                    mMarginPaint);
            canvas.drawLine(mBitmapRectPoints[4], mBitmapRectPoints[5], mBitmapRectPoints[6], mBitmapRectPoints[7],
                    mMarginPaint);
            canvas.drawLine(mBitmapRectPoints[0], mBitmapRectPoints[1], mBitmapRectPoints[4], mBitmapRectPoints[5],
                    mMarginPaint);
            canvas.drawLine(mBitmapRectPoints[2], mBitmapRectPoints[3], mBitmapRectPoints[6], mBitmapRectPoints[7],
                    mMarginPaint);
            canvas.drawCircle(mBitmapRectPoints[0], mBitmapRectPoints[1], mRadius, mDragHandlerPaint);
            canvas.drawCircle(mBitmapRectPoints[2], mBitmapRectPoints[3], mRadius, mDragHandlerPaint);
            canvas.drawCircle(mBitmapRectPoints[4], mBitmapRectPoints[5], mRadius, mDragHandlerPaint);
            canvas.drawCircle(mBitmapRectPoints[6], mBitmapRectPoints[7], mRadius, mDragHandlerPaint);
            canvas.restore();
        } else {
            canvas.save();
            canvas.drawRect(mMarginRectF, mMarginPaint);
            canvas.drawCircle(mMarginRectF.left, mMarginRectF.top, mRadius, mDragHandlerPaint);
            canvas.drawCircle(mMarginRectF.right, mMarginRectF.top, mRadius, mDragHandlerPaint);
            canvas.drawCircle(mMarginRectF.left, mMarginRectF.bottom, mRadius, mDragHandlerPaint);
            canvas.drawCircle(mMarginRectF.right, mMarginRectF.bottom, mRadius, mDragHandlerPaint);
            canvas.restore();
        }

        if (!(mDragPoint.x == INVALID_POINT && mDragPoint.y == INVALID_POINT)) {
            canvas.save();
            canvas.drawCircle(mMarginRectF.centerX(), mMarginRectF.centerY(), mRadius, mDragHandlerPaint);
            canvas.restore();
        }
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     * 
     * @param direction
     *            - positive direction value means scroll from right to left,
     *            negative value means scroll from left to right
     * 
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(LOG_TAG, "onDoubleTap. double tap enabled? " + mDoubleTapToZoomEnabled);
            if (mDoubleTapToZoomEnabled) {
                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxZoom());
                targetScale = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                mCurrentScaleFactor = targetScale;
                zoomTo(targetScale, e.getX(), e.getY(), 200);
                invalidate();
            }

            if (null != doubleTapListener) {
                doubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (mScaleDetector != null) {
                    if (!mScaleDetector.isInProgress()) {
                        setPressed(true);
                        performLongClick();
                    }
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScrollEnabled)
                return false;

            if (e1 == null || e2 == null)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector != null) {
                if (mScaleDetector.isInProgress())
                    return false;
            }
            if (getScale() == 1f)
                return false;
            scrollBy(-distanceX, -distanceY);
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mScrollEnabled)
                return false;

            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector != null) {
                if (mScaleDetector.isInProgress())
                    return false;
            }
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
                scrollBy(diffX / 2, diffY / 2, 300);
                invalidate();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @SuppressWarnings("unused")
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
            if (mScaleEnabled) {
                targetScale = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                mCurrentScaleFactor = Math.min(getMaxZoom(), Math.max(targetScale, MIN_ZOOM));
                mDoubleTapDirection = 1;
                invalidate();
                return true;
            }
            return false;
        }
    }

    public interface OnImageViewTouchDoubleTapListener {
        void onDoubleTap();
    }
}
