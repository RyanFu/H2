package com.prettygirl.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class SlideLayout extends ViewGroup {

    public final static int DURATION = 300;

    protected boolean mPlaceLeft = false;
    
    protected boolean mOpened;
    
    protected View mSidebar;
    
    protected View mContent;
    
    protected int mSidebarWidth = 0;

    protected Animation mAnimation;
    
    protected OpenSlideSideListener mOpenListener;
    
    protected CloseSlideSideListener mCloseListener;
    
    protected SlideSideListener mListener;

    protected boolean mPressed = false;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateView(View cSidebar, View cContent) {
        mSidebar = cSidebar;
        mContent = cContent;
        addView(cSidebar);
        addView(cContent);
        mOpenListener = new OpenSlideSideListener(mSidebar, mContent);
        mCloseListener = new CloseSlideSideListener(mSidebar, mContent);
        requestLayout();
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mSidebar == null || mContent == null) {
            return;
        }
        /* the title bar assign top padding, drop it */
        int sidebarLeft = l;
        if (!mPlaceLeft) {
            sidebarLeft = r - mSidebarWidth;
        }
        mSidebar.layout(sidebarLeft, 0, sidebarLeft + mSidebarWidth, 0 + mSidebar.getMeasuredHeight());

        if (mOpened) {
            if (mPlaceLeft) {
                mContent.layout(l + mSidebarWidth, 0, r + mSidebarWidth, b);
            } else {
                mContent.layout(l - mSidebarWidth, 0, r - mSidebarWidth, b);
            }
        } else {
            mContent.layout(l, 0, r, b);
        }
    }

    @Override
    public void onMeasure(int w, int h) {
        super.onMeasure(w, h);
        super.measureChildren(w, h);
        if (mSidebar == null) {
            mSidebarWidth = 0;
        } else {
            mSidebarWidth = getWidth() / 2;// mSidebar.getMeasuredWidth();
        }
    }

    @Override
    protected void measureChild(View child, int parentWSpec, int parentHSpec) {
        if (child == mSidebar) {
            int mode = MeasureSpec.getMode(parentWSpec);
            int width = (int) (getMeasuredWidth() * 0.9);
            super.measureChild(child, MeasureSpec.makeMeasureSpec(width, mode), parentHSpec);
        } else {
            super.measureChild(child, parentWSpec, parentHSpec);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mContent == null) {
            return false;
        }
        if (!isSlideSideOpening()) {
            return false;
        }

        int action = ev.getAction();

        if (action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_DOWN) {
            return false;
        }

        /* if user press and release both on Content while
         * sidebar is opening, call listener. otherwise, pass
         * the event to child. */
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (mContent.getLeft() < x && mContent.getRight() > x && mContent.getTop() < y && mContent.getBottom() > y) {
            if (action == MotionEvent.ACTION_DOWN) {
                mPressed = true;
            }

            if (mPressed && action == MotionEvent.ACTION_UP && mListener != null) {
                mPressed = false;
                return mListener.onContentTouchedWhenOpening();
            }
        } else {
            mPressed = false;
        }

        return false;
    }

    /**
     * 设置边界面的位置,默认是在右边
     * @param isLeft 是否在左边
     */
    public void setSlideSide(boolean isLeft) {
        mPlaceLeft = isLeft;
    }

    public void setSlideSideListener(SlideSideListener l) {
        mListener = l;
    }

    public boolean isSlideSideOpening() {
        return mOpened;
    }

    public void toggleSidebar() {
        if (mContent.getAnimation() != null) {
            return;
        }
        if (mOpened) {
            /* opened, make close animation*/
            if (mPlaceLeft) {
                mAnimation = new TranslateAnimation(0, -mSidebarWidth, 0, 0);
            } else {
                mAnimation = new TranslateAnimation(0, mSidebarWidth, 0, 0);
            }
            mAnimation.setAnimationListener(mCloseListener);
        } else {
            /* not opened, make open animation */
            if (mPlaceLeft) {
                mAnimation = new TranslateAnimation(0, mSidebarWidth, 0, 0);
            } else {
                mAnimation = new TranslateAnimation(0, -mSidebarWidth, 0, 0);
            }
            mAnimation.setAnimationListener(mOpenListener);
        }
        mAnimation.setDuration(DURATION);
        mAnimation.setFillAfter(true);
        mAnimation.setFillEnabled(true);
        mContent.startAnimation(mAnimation);
    }

    public void openSidebar() {
        if (!mOpened) {
            toggleSidebar();
        }
    }

    public void closeSidebar() {
        if (mOpened) {
            toggleSidebar();
        }
    }

    class OpenSlideSideListener implements Animation.AnimationListener {
        View iSidebar;
        View iContent;

        private OpenSlideSideListener(View sidebar, View content) {
            iSidebar = sidebar;
            iContent = content;
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
            iSidebar.setVisibility(View.VISIBLE);
        }

        public void onAnimationEnd(Animation animation) {
            iContent.clearAnimation();
            mOpened = !mOpened;
            requestLayout();
            if (mListener != null) {
                mListener.onSidebarOpened();
            }
        }
    }

    class CloseSlideSideListener implements Animation.AnimationListener {
        View iSidebar;
        View iContent;

        private CloseSlideSideListener(View sidebar, View content) {
            iSidebar = sidebar;
            iContent = content;
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            iContent.clearAnimation();
            iSidebar.setVisibility(View.INVISIBLE);
            mOpened = !mOpened;
            requestLayout();
            if (mListener != null) {
                mListener.onSidebarClosed();
            }
        }
    }

    public interface SlideSideListener {
        public void onSidebarOpened();

        public void onSidebarClosed();

        public boolean onContentTouchedWhenOpening();
    }
}
