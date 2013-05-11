package com.prettygirl.app.components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pretty.girl.app.R;
import com.prettygirl.app.GalleryActivity;
import com.prettygirl.app.utils.DialogToastUtils;
import com.prettygirl.app.utils.ImageResourceUtils;
import com.prettygirl.app.utils.MiscUtil;
import com.prettygirl.app.utils.PreferenceUtils;
import com.prettygirl.app.utils.UserInfoManager;
import com.prettygirl.app.utils.UserInfoManager.Event;

public class NavigationViewItem extends LinearLayout implements OnClickListener {

    private NavigationImageView mNavigationImageView1 = null;
    private NavigationImageView mNavigationImageView2 = null;
    private NavigationImageView mNavigationImageView3 = null;
    private NavigationImageView mNavigationImageView4 = null;
    private TextView mLevelView = null;
    //private TextView mCountView;

    private View mMaskView;
    private int mLevel;

    private boolean mLocked = true;

    public NavigationViewItem(Context context) {
        super(context);
        init();
    }

    public NavigationViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setOrientation(VERTICAL);
        View.inflate(getContext(), R.layout.e_navigation_item, this);
        mNavigationImageView1 = (NavigationImageView) findViewById(R.id.navigationImageView1);
        mNavigationImageView2 = (NavigationImageView) findViewById(R.id.navigationImageView2);
        mNavigationImageView3 = (NavigationImageView) findViewById(R.id.navigationImageView3);
        mNavigationImageView4 = (NavigationImageView) findViewById(R.id.navigationImageView4);
        mLevelView = (TextView) findViewById(R.id.textView_level);
        findViewById(R.id.navigationImageView).setOnClickListener(this);
        //mCountView = (TextView) findViewById(R.id.textView_count);
        mMaskView = findViewById(R.id.image_mask);
    }

    @Override
    public void onClick(View view) {
        if (mLocked) {
            String title, msg;
            if (PreferenceUtils.getInt("review_score", 0) == 0) {
                title = getContext().getString(R.string.dialog_review_title);
                msg = getContext().getString(R.string.dialog_review_content);
            } else {
                title = getContext().getString(R.string.dialog_rate_title);
                msg = getContext().getString(R.string.dialog_rate_title);
            }
            DialogToastUtils.showDialog(getContext(), title, msg, getContext().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MiscUtil.startGooglePlay(getContext(), null);
                            if (PreferenceUtils.getInt("review_score", 0) == 0) {
                                UserInfoManager.getInstance().onEvent(Event.Review);
                                PreferenceUtils.setInt("review_score", 1);
                            }
                        }
                    });

            return;
        }
        Intent intent = new Intent();
        intent.setClass(getContext(), GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXT_LEVEL_KEY, mLevel);
        intent.putExtra(GalleryActivity.EXT_TYPE, GalleryActivity.EXT_VALUE_ONLINE);
        intent.putExtra(GalleryActivity.EXT_STRING_BASE_URL, ImageResourceUtils.getServerUrl(getContext(), mLevel));
        getContext().startActivity(intent);
    }

    public void setCurrentLevel(int position) {
        UserInfoManager cUserInfoManager = UserInfoManager.getInstance();
        if (position == 0) {
            mLevelView.setText(getResources().getString(R.string.current_level, cUserInfoManager.getCurrentPoint()));
        } else {
            mLevelView.setText(getResources().getString(R.string.current_go_level,
                    cUserInfoManager.getLevelDivByLevel(position)));
        }
        mLevel = position;
        boolean isLocked = false;

        if (cUserInfoManager.getCurrentLevel() >= position) {
            // unlocked
            if (position != 0) {
                mLevelView.setText(getResources().getString(R.string.current_unlocked_level, position));
            }
            //mCountView.setVisibility(VISIBLE);
            //mCountView.setText(Integer.toString(GirlLoader.getTotalImageCountOnServer(getContext(), mLevel)));
            mMaskView.setVisibility(GONE);
        } else {
            isLocked = true;
            //mCountView.setVisibility(GONE);
            mMaskView.setVisibility(VISIBLE);
        }
        // setEnabled(!isLocked);
        mNavigationImageView1.setLocked(isLocked, position);
        mNavigationImageView2.setLocked(isLocked, position);
        mNavigationImageView3.setLocked(isLocked, position);
        mNavigationImageView4.setLocked(isLocked, position);

        mLocked = isLocked;
        if (isLocked) {
            int index = ImageResourceUtils.getLastNavigationIndex(getContext(), position);
            mNavigationImageView1.updateContext(false, index++);
            mNavigationImageView2.updateContext(false, index++);
            mNavigationImageView3.updateContext(false, index++);
            mNavigationImageView4.updateContext(false, index++);
        } else {
            mNavigationImageView1.updateContext(true, NavigationImageView.INVALID_INDEX);
            mNavigationImageView2.updateContext(true, NavigationImageView.INVALID_INDEX);
            mNavigationImageView3.updateContext(true, NavigationImageView.INVALID_INDEX);
            mNavigationImageView4.updateContext(true, NavigationImageView.INVALID_INDEX);
        }
    }

    public void cancelUpdate() {
        mNavigationImageView1.cancelUpdate();
        mNavigationImageView2.cancelUpdate();
        mNavigationImageView3.cancelUpdate();
        mNavigationImageView4.cancelUpdate();
    }
}
