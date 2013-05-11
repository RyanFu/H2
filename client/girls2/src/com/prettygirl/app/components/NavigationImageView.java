package com.prettygirl.app.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pretty.girl.app.R;
import com.prettygirl.app.components.animation.Rotate3dAnimation;
import com.prettygirl.app.utils.ImageResourceUtils;

public class NavigationImageView extends RelativeLayout {

    public static final int INVALID_INDEX = -1;

    private CustomImageView mCustomImageView1;
    private CustomImageView mCustomImageView2;

    private int mLevel;

    private Handler mHandler = new Handler();

    public NavigationImageView(Context context) {
        super(context);
        init();
    }

    public NavigationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.e_navigation_trans_image, this);
        mCustomImageView1 = (CustomImageView) findViewById(R.id.imageView1);
        mCustomImageView1.setClickable(false);
        mCustomImageView2 = (CustomImageView) findViewById(R.id.imageView2);
        mCustomImageView2.setClickable(false);
    }

    public void setLocked(boolean isLocked, int level) {
        mLevel = level;
        setEnabled(!isLocked);
    }

    public void cancelUpdate() {
        cancelImage(mCustomImageView1);
        cancelImage(mCustomImageView2);
    }

    @SuppressWarnings("unchecked")
    private void cancelImage(ImageView imageView) {
        imageView.setImageDrawable(null);
        Object mObj = imageView.getTag();
        if (mObj != null && mObj instanceof AsyncTask) {
            AsyncTask<Void, Void, Bitmap> mloadAsyncTaskTag = (AsyncTask<Void, Void, Bitmap>) mObj;
            mloadAsyncTaskTag.cancel(true);
        }
        imageView.clearAnimation();
    }

    public void updateContext(boolean next, int index) {
        loadImage(mCustomImageView1, next, index);
    }

    @SuppressWarnings("unchecked")
    private void loadImage(final ImageView imageView, final boolean next, final int index) {
        Object mObj = imageView.getTag();
        if (mObj != null && mObj instanceof AsyncTask) {
            AsyncTask<Void, Void, Bitmap> mloadAsyncTaskTag = (AsyncTask<Void, Void, Bitmap>) mObj;
            mloadAsyncTaskTag.cancel(true);
        }
        AsyncTask<Void, Void, Bitmap> loadTask = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                if (next) {
                    return ImageResourceUtils.getNextNavigationImage(getContext(), mLevel);
                } else if (index == INVALID_INDEX) {
                    return ImageResourceUtils.getLastNavigationImage(getContext(), mLevel);
                } else {
                    return ImageResourceUtils.getNavigationImageByIndex(getContext(), mLevel, index);
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result == null) {
                    return;
                }
                imageView.setImageBitmap(result);
                if (isEnabled()) {
                    if (mCustomImageView2.getDrawable() == null) {
                        loadImage(mCustomImageView2, true, INVALID_INDEX);
                    } else {
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                start3DTranslate(
                                        imageView == mCustomImageView1 ? mCustomImageView2 : mCustomImageView1,
                                        new IAnimationFinished() {

                                            @Override
                                            public void finished() {
                                                start3DTranslate(imageView, new IAnimationFinished() {

                                                    @Override
                                                    public void finished() {
                                                        mHandler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                loadImage(
                                                                        imageView == mCustomImageView1 ? mCustomImageView2
                                                                                : mCustomImageView1, true,
                                                                        INVALID_INDEX);
                                                            }

                                                        }, 1500);
                                                    }

                                                });
                                            }

                                        });
                            }

                        }, 1500);
                    }
                }
            }

        };
        imageView.setTag(loadTask);
        loadTask.execute();
    }

    private void start3DTranslate(ImageView imageView, IAnimationFinished iAnimationFinished) {
        if (imageView == mCustomImageView1) {
            if (imageView.getVisibility() == View.VISIBLE) {
                start3DTranslate(imageView, 0, 90, 1.0f, new AccelerateInterpolator(), iAnimationFinished);
                imageView.setVisibility(INVISIBLE);
            } else {
                start3DTranslate(imageView, 90, 0, 1.0f, new DecelerateInterpolator(), iAnimationFinished);
                imageView.setVisibility(VISIBLE);
            }
        } else if (imageView == mCustomImageView2) {
            if (imageView.getVisibility() == View.VISIBLE) {
                start3DTranslate(imageView, 0, 90, 1.0f, new AccelerateInterpolator(), iAnimationFinished);
                imageView.setVisibility(INVISIBLE);
            } else {
                start3DTranslate(imageView, 90, 0, 1.0f, new DecelerateInterpolator(), iAnimationFinished);
                imageView.setVisibility(VISIBLE);
            }
        }
    }

    private void start3DTranslate(final View cView, float fromDegrees, float toDegrees, float depthZ,
            Interpolator interpolator, final IAnimationFinished iAnimationFinished) {
        float centerX = cView.getWidth() / 2.0f;
        float centerY = cView.getHeight() / 2.0f;
        Rotate3dAnimation animation = new Rotate3dAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, true);
        animation.setDuration(500);
        animation.setFillAfter(true);
        if (interpolator != null) {
            animation.setInterpolator(interpolator);
        }
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                cView.clearAnimation();
                iAnimationFinished.finished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        cView.startAnimation(animation);
    }

    public interface IAnimationFinished {
        public void finished();
    }

}
