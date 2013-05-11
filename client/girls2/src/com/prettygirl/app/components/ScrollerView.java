package com.prettygirl.app.components;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.prettygirl.app.Application;
import com.prettygirl.app.OnlineTryGalleryActivity;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.UMengKey;
import com.umeng.analytics.MobclickAgent;

public class ScrollerView extends HorizontalScrollView implements Runnable {

    private LinearLayout container;
    private int scrollDirection = LEFT_TO_RIGHT;

    private static int LEFT_TO_RIGHT = 0;
    private static int RIGHT_TO_LEFT = 1;

    @SuppressWarnings("deprecation")
	public ScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        container = new LinearLayout(context);
        addView(container, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        setHorizontalScrollBarEnabled(false);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(v.getContext(), UMengKey.SCROLLER_VIEW_ONCLICK);
                //                String title = getContext().getString(R.string.dialog_more_title);
                //                String msg = getContext().getString(R.string.dialog_more_content);
                //                DialogToastUtils.showDialog(getContext(), title, msg, getContext().getString(R.string.ok),
                //                        new DialogInterface.OnClickListener() {
                //                            @Override
                //                            public void onClick(DialogInterface dialog, int which) {
                //                                MobclickAgent.onEvent(getContext(), UMengKey.SCROLLER_VIEW_ONCLICK_OK);
                //                                if (MiscUtil.isPackageInstalled("com.prettygirl.app", getContext()) == true) {
                //                                    MiscUtil.startGooglePlayByAuthor(getContext());
                //                                } else {
                //                                    MiscUtil.startGooglePlay(getContext(), "com.prettygirl.app");
                //                                }
                //                            }
                //                        });
                if (MiscUtil.isPackageInstalled("com.prettygirl.app", getContext()) == true) {
                    MiscUtil.startGooglePlayByAuthor(getContext());
                } else {
                	if(Application.OFFLINE_BROWSER_ONLINE_ENABLE) {
	                    Intent intent = new Intent();
	                    intent.setClass(getContext(), OnlineTryGalleryActivity.class);
	                    getContext().startActivity(intent);
                	} else {
                        MobclickAgent.onEvent(getContext(), UMengKey.SCROLLER_VIEW_ONCLICK_OK);
                        if (MiscUtil.isPackageInstalled("com.prettygirl.app", getContext()) == true) {
                            MiscUtil.startGooglePlayByAuthor(getContext());
                        } else {
                            MiscUtil.startGooglePlay(getContext(), "com.prettygirl.app");
                        }
                	}
                }
            }
        });
    }

    public void setImages(ArrayList<String> urls) {
        container.removeAllViews();
        int margin = (int) ImageResourceUtils.dip2px(getContext(), 3);
        for (String url : urls) {
            ImageView v = new ImageView(getContext());
            @SuppressWarnings("deprecation")
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.FILL_PARENT);
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            v.setScaleType(ScaleType.CENTER_INSIDE);
            v.setAdjustViewBounds(true);
            container.addView(v, lp);
            ImageLoader.getInstance().displayImage(url, v);
        }
        postDelayed(this, 1000);
    }

    @Override
    public void run() {
        if (scrollDirection == LEFT_TO_RIGHT) {
            if (getWidth() + getScrollX() >= container.getWidth()) {
                scrollDirection = RIGHT_TO_LEFT;
            } else {
                scrollBy(1, 0);
            }
        } else {
            if (getScrollX() <= 0) {
                scrollDirection = LEFT_TO_RIGHT;
            } else {
                scrollBy(-1, 0);
            }
        }
        postDelayed(this, 15);
    }

}
